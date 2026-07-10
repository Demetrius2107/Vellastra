/**
 * git-net.js — GitHub 网络层核心模块
 * ================================
 * 本模块提供对 GitHub 网络连接的全方位检测与优化：
 *   - DNS 解析、TCP 连通性、延迟测量
 *   - 代理自动检测
 *   - 多端点智能切换（HTTPS / SSH / 镜像）
 *   - 指数退避重试策略
 *   - Git 网络参数优化建议
 *
 * 用法（热插拔）:
 *   import { checkConnectivity, measureLatency, … } from '../lib/git-net.js';
 */

import dns from 'dns/promises';
import net from 'net';
import https from 'https';
import http from 'http';
import { performance } from 'perf_hooks';

// ─── 常量 ───────────────────────────────────────────────────────

/** GitHub 官方域名与 IP */
const GITHUB_ENDPOINTS = [
  { label: 'HTTPS (github.com)',            url: 'https://github.com',   host: 'github.com',      port: 443 },
  { label: 'SSH (github.com)',              url: 'ssh://git@github.com', host: 'github.com',      port: 22  },
  { label: 'API (api.github.com)',          url: 'https://api.github.com', host: 'api.github.com', port: 443 },
  { label: '镜像 (hub.fastgit.xyz)',         url: 'https://hub.fastgit.xyz', host: 'hub.fastgit.xyz', port: 443 },
  { label: '镜像 (github.com.cnpmjs.org)',   url: 'https://github.com.cnpmjs.org', host: 'github.com.cnpmjs.org', port: 443 },
];

/** 默认重试配置 */
const RETRY_CONFIG = {
  maxRetries: 3,
  baseDelayMs: 1000,
  maxDelayMs: 10000,
  /** jitter 系数 0~1，防止惊群 */
  jitter: 0.2,
};

/** TCP 连接超时 */
const TCP_TIMEOUT = 5000;

/** HTTP 请求超时 */
const HTTP_TIMEOUT = 8000;

// ─── 网络检测函数 ───────────────────────────────────────────────

/**
 * DNS 解析目标域名（带超时），返回 IP 列表
 * @param {string}  hostname
 * @param {number}  [timeoutMs=5000]
 */
export async function resolveHost(hostname = 'github.com', timeoutMs = 5000) {
  const resolvePromise = dns.resolve4(hostname);
  const timeoutPromise = new Promise((_, reject) =>
    setTimeout(() => reject(new Error(`DNS 解析超时 (${timeoutMs}ms)`)), timeoutMs)
  );
  const addresses = await Promise.race([resolvePromise, timeoutPromise]);
  return addresses;
}

/**
 * TCP 连通性测试
 * @returns {{ reachable: boolean, latencyMs: number|null, error: string|null }}
 */
export async function checkTcpConnect(host, port, timeout = TCP_TIMEOUT) {
  const start = performance.now();
  return new Promise((resolve) => {
    const sock = new net.Socket();
    sock.setTimeout(timeout);

    sock.on('connect', () => {
      const latency = Math.round(performance.now() - start);
      sock.destroy();
      resolve({ reachable: true, latencyMs: latency, error: null });
    });

    sock.on('error', (err) => {
      sock.destroy();
      resolve({ reachable: false, latencyMs: null, error: err.message });
    });

    sock.on('timeout', () => {
      sock.destroy();
      resolve({ reachable: false, latencyMs: null, error: `连接超时 (${timeout}ms)` });
    });

    sock.connect(port, host);
  });
}

/**
 * HTTPS GET 延迟测量（通过发起 HEAD/GET 请求）
 */
export async function checkHttpsLatency(url, timeout = HTTP_TIMEOUT) {
  const start = performance.now();
  return new Promise((resolve) => {
    const parsed = new URL(url);
    const opts = {
      hostname: parsed.hostname,
      port: parsed.port || 443,
      path: parsed.pathname || '/',
      method: 'HEAD',
      timeout,
      rejectUnauthorized: false,
    };

    const req = https.request(opts, (res) => {
      const elapsed = Math.round(performance.now() - start);
      res.resume(); // 消费响应体
      resolve({ ok: true, latencyMs: elapsed, statusCode: res.statusCode, error: null });
    });

    req.on('error', (err) => {
      resolve({ ok: false, latencyMs: null, statusCode: null, error: err.message });
    });

    req.on('timeout', () => {
      req.destroy();
      resolve({ ok: false, latencyMs: null, statusCode: null, error: `请求超时 (${timeout}ms)` });
    });

    req.end();
  });
}

/**
 * 综合连通性测试（DNS + TCP + HTTPS），对一个端点执行全链路检测
 */
export async function checkEndpoint(endpoint) {
  const { label, host, port, url } = endpoint;

  const result = { label, host, port, dns: null, tcp: null, https: null, overall: false };

  // DNS
  try {
    const ips = await resolveHost(host);
    result.dns = { ok: true, ips };
  } catch (err) {
    result.dns = { ok: false, error: err.message };
    result.overall = false;
    return result;
  }

  // TCP
  result.tcp = await checkTcpConnect(host, port);
  if (!result.tcp.reachable) {
    result.overall = false;
    return result;
  }

  // HTTPS（只对 HTTPS 端点做）
  if (url.startsWith('https://')) {
    result.https = await checkHttpsLatency(url);
    if (!result.https.ok) {
      result.overall = false;
      return result;
    }
  } else {
    // SSH 端点没有 HTTPS 检测，以 TCP 为准
    result.https = { ok: true, latencyMs: result.tcp.latencyMs, note: 'SSH 端点跳过 HTTP 检测' };
  }

  result.overall = true;
  return result;
}

/**
 * 完整连通性诊断：对所有端点执行检测，返回排序后的结果
 */
export async function diagnoseAll() {
  const results = [];
  for (const ep of GITHUB_ENDPOINTS) {
    const r = await checkEndpoint(ep);
    results.push(r);
  }

  // 按延迟升序排列（可达的在前面）
  results.sort((a, b) => {
    if (a.overall && !b.overall) return -1;
    if (!a.overall && b.overall) return 1;
    const latA = a.https?.latencyMs ?? a.tcp?.latencyMs ?? Infinity;
    const latB = b.https?.latencyMs ?? b.tcp?.latencyMs ?? Infinity;
    return latA - latB;
  });

  return results;
}

/**
 * 自动选择最佳可用端点
 * @returns {{ endpoint: object, result: object } | null}
 */
export async function pickBestEndpoint() {
  const results = await diagnoseAll();
  const reachable = results.filter(r => r.overall);
  if (reachable.length === 0) return null;
  return { endpoint: reachable[0], result: reachable };
}

// ─── 代理检测 ───────────────────────────────────────────────────

/**
 * 检测系统代理设置（读取环境变量）
 * @returns {{ http: string|null, https: string|null, all: string|null, source: string }}
 */
export function detectProxy() {
  const env = process.env;
  const httpProxy  = env.HTTP_PROXY || env.http_proxy || null;
  const httpsProxy = env.HTTPS_PROXY || env.https_proxy || null;
  const allProxy   = env.ALL_PROXY || env.all_proxy || null;

  // Git 协议配置
  const gitHttpProxy = null; // 需主动 git config --get

  return {
    http: httpProxy,
    https: httpsProxy,
    all: allProxy,
    source: Object.keys(env).some(k => /proxy/i.test(k)) ? '环境变量' : '未检测到代理',
  };
}

/**
 * 配置 git 代理（返回需要执行的命令列表）
 */
export function buildGitProxyCommands(proxyUrl) {
  if (!proxyUrl) return [];
  return [
    `git config --global http.proxy "${proxyUrl}"`,
    `git config --global https.proxy "${proxyUrl}"`,
  ];
}

/**
 * 清除 git 代理配置
 */
export function buildGitProxyClearCommands() {
  return [
    `git config --global --unset http.proxy`,
    `git config --global --unset https.proxy`,
  ];
}

// ─── 指数退避重试 ───────────────────────────────────────────────

/**
 * 带指数退避 + jitter 的异步重试包装器
 *
 * @param {Function} fn          - 异步函数 () => Promise<T>
 * @param {object}   [options]   - 配置
 * @param {number}   [options.maxRetries=3]
 * @param {number}   [options.baseDelayMs=1000]
 * @param {number}   [options.maxDelayMs=10000]
 * @param {number}   [options.jitter=0.2]
 * @param {Function} [options.onRetry] - (attempt, delayMs, error) => void
 * @returns {Promise<T>}
 */
export async function withRetry(fn, options = {}) {
  const {
    maxRetries = RETRY_CONFIG.maxRetries,
    baseDelayMs = RETRY_CONFIG.baseDelayMs,
    maxDelayMs = RETRY_CONFIG.maxDelayMs,
    jitter = RETRY_CONFIG.jitter,
    onRetry = null,
  } = options;

  let lastError;

  for (let attempt = 0; attempt <= maxRetries; attempt++) {
    try {
      return await fn();
    } catch (err) {
      lastError = err;
      if (attempt < maxRetries) {
        // 指数退避 + 随机 jitter
        const delay = Math.min(baseDelayMs * Math.pow(2, attempt), maxDelayMs);
        const jitteredDelay = Math.round(delay * (1 + (Math.random() * 2 - 1) * jitter));
        if (onRetry) onRetry(attempt + 1, jitteredDelay, err);
        await sleep(jitteredDelay);
      }
    }
  }

  throw lastError;
}

/**
 * 判断 git 错误是否为 transient（可重试）
 * @param {Error|string} error
 * @returns {boolean}
 */
export function isTransientError(error) {
  const msg = (typeof error === 'string' ? error : (error?.message || '')).toLowerCase();
  const transientPatterns = [
    'timeout',
    'timed out',
    'connection reset',
    'connection refused',
    'connection closed',
    'connection refused',
    'econnrefused',
    'econnreset',
    'etimedout',
    'enetunreach',
    'could not read from remote',
    'could not connect',
    'remote end hung up',
    'early eof',
    'fatal: the remote end hung up unexpectedly',
    'rpc failed',
    'transfer closed',
    'gnutls',
    'ssl_error',
    'unable to access',
    'fatal: unable to access',
    '403',
    '429',
    '500',
    '502',
    '503',
  ];
  return transientPatterns.some(p => msg.includes(p));
}

// ─── Git 网络优化 ───────────────────────────────────────────────

/**
 * 生成 Git 网络优化配置命令列表
 * 这些参数对慢速 / 不稳定网络有明显改善
 */
export function buildOptimizedGitConfig() {
  return [
    // 增大 POST 缓冲区（对大推送有用）
    { key: 'http.postBuffer',      value: '524288000',  desc: 'POST 缓冲区 → 500MB' },
    // 低网速检测：低于 1KB/s 持续 600 秒才放弃
    { key: 'http.lowSpeedLimit',   value: '1000',       desc: '低网速阀值 → 1000 B/s' },
    { key: 'http.lowSpeedTime',    value: '600',        desc: '低网速容忍 → 600 秒' },
    // 强制 HTTP/1.1（HTTP/2 在某些代理/NAT 下不稳定）
    { key: 'http.version',         value: 'HTTP/1.1',   desc: 'HTTP 协议 → HTTP/1.1' },
    // 开启长连接 keep-alive 复用
    { key: 'http.keepAlive',       value: 'true',       desc: 'HTTP Keep-Alive → 开启' },
    // 关闭 SSL 验证（仅在代理/镜像环境需要）
    { key: 'http.sslVerify',       value: 'true',       desc: 'SSL 验证 → 保持开启（不改）' },
    // 启用并发索引预加载
    { key: 'core.preloadindex',    value: 'true',       desc: '并发索引预加载 → 开启' },
    // 压缩级别
    { key: 'core.compression',     value: '0',          desc: '压缩级别 → 0（自动）' },
  ];
}

// ─── 辅助函数 ───────────────────────────────────────────────────

/** Promise 化的 sleep */
function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * 测量 ping 延迟（简易版，仅 TCP 三次握手）
 */
export async function pingHost(host = 'github.com', port = 443) {
  const result = await checkTcpConnect(host, port);
  return result;
}

/**
 * 快速一键连通性检测（适合 prompt/UI 提示）
 * @returns {{ ok: boolean, latencyMs: number, details: string }}
 */
export async function checkConnectivity() {
  const start = performance.now();
  try {
    const ips = await resolveHost('github.com');
    const tcp = await checkTcpConnect('github.com', 443);
    if (!tcp.reachable) {
      return { ok: false, latencyMs: null, details: `DNS 解析成功 (${ips.join(',')}) 但 TCP 连接失败: ${tcp.error}` };
    }
    const total = Math.round(performance.now() - start);
    return { ok: true, latencyMs: total, details: `ping ${total}ms, IP: ${ips.join(', ')}` };
  } catch (err) {
    return { ok: false, latencyMs: null, details: `连通性检测失败: ${err.message}` };
  }
}

export default {
  GITHUB_ENDPOINTS,
  resolveHost,
  checkTcpConnect,
  checkHttpsLatency,
  checkEndpoint,
  diagnoseAll,
  pickBestEndpoint,
  detectProxy,
  buildGitProxyCommands,
  buildGitProxyClearCommands,
  withRetry,
  isTransientError,
  buildOptimizedGitConfig,
  pingHost,
  checkConnectivity,
};
