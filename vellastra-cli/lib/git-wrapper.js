/**
 * git-wrapper.js — Git 命令包装层
 * ==============================
 * 在 child_process.exec / execSync 之上提供：
 *   - 超时控制
 *   - 重试 + 指数退避（复用 git-net 的 withRetry）
 *   - 错误分类（transient / permanent）
 *   - 进度输出过滤
 *   - git push / pull / merge 专用包装
 *
 * 用法:
 *   import { gitPush, gitPull, gitMerge, runGit } from '../lib/git-wrapper.js';
 *
 *   const out = await gitPush({ remote: 'origin', branch: 'main' });
 *   // 自动重试 3 次，遇网络错误指数退避
 */

import { execSync, exec } from 'child_process';
import chalk from 'chalk';
import { withRetry, isTransientError } from './git-net.js';

// ─── 配置 ───────────────────────────────────────────────────────

const DEFAULT_TIMEOUT = 120_000; // 2 分钟
const LARGE_PUSH_TIMEOUT = 600_000; // 10 分钟

// ─── 核心执行器 ──────────────────────────────────────────────────

/**
 * 同步执行 git 命令，带超时控制
 *
 * @param {string}   cmd       - git 子命令及参数（不含 "git" 前缀）
 * @param {object}   [options]
 * @param {number}   [options.timeout=120000]
 * @param {string}   [options.cwd]          - git 仓库工作目录
 * @param {boolean}  [options.largePush=false] - 是否大推送（使用更长超时）
 * @param {boolean}  [options.silent=false]    - 静默模式（不打印 stderr）
 * @returns {{ stdout: string, stderr: string, code: number }}
 */
export function runGit(cmd, options = {}) {
  const {
    timeout = DEFAULT_TIMEOUT,
    cwd = undefined,
    largePush = false,
    silent = false,
  } = options;

  const effectiveTimeout = largePush ? LARGE_PUSH_TIMEOUT : timeout;
  const fullCmd = `git ${cmd}`;

  try {
    const stdout = execSync(fullCmd, {
      encoding: 'utf-8',
      timeout: effectiveTimeout,
      cwd,
      maxBuffer: 50 * 1024 * 1024, // 50MB
      windowsHide: true,
    });
    return { stdout: stdout.trim(), stderr: '', code: 0 };
  } catch (err) {
    const stderr = err.stderr?.toString().trim() || '';
    const stdout = err.stdout?.toString().trim() || '';
    if (!silent && stderr) {
      // git 的 progress 输出走 stderr，非错误不打印
      if (err.status === 0) {
        return { stdout, stderr, code: 0 };
      }
    }
    return { stdout, stderr, code: err.status || 1 };
  }
}

/**
 * 异步执行 git 命令（用于长时间操作，stream 输出）
 * 支持进度回调
 *
 * @param {string}   cmd        - git 子命令及参数
 * @param {object}   [options]
 * @param {number}   [options.timeout=120000]
 * @param {string}   [options.cwd]
 * @param {Function} [options.onProgress] - (line: string) => void
 * @returns {Promise<{ stdout: string, stderr: string, code: number }>}
 */
export function runGitAsync(cmd, options = {}) {
  const { timeout = DEFAULT_TIMEOUT, cwd = undefined, onProgress = null } = options;
  const fullCmd = `git ${cmd}`;

  return new Promise((resolve) => {
    const child = exec(fullCmd, {
      encoding: 'utf-8',
      timeout,
      cwd,
      maxBuffer: 50 * 1024 * 1024,
      windowsHide: true,
    }, (err, stdout, stderr) => {
      resolve({
        stdout: (stdout || '').trim(),
        stderr: (stderr || '').trim(),
        code: err ? (err.status || 1) : 0,
      });
    });

    // 实时输出进度
    if (onProgress && child.stdout) {
      child.stdout.on('data', (data) => {
        const lines = data.split('\n').filter(Boolean);
        for (const line of lines) onProgress(line);
      });
    }
    if (onProgress && child.stderr) {
      // git 的进度信息通常走 stderr（如 "Enumerating objects: 50%"）
      child.stderr.on('data', (data) => {
        const line = data.toString().trim();
        if (line && !line.startsWith('fatal:') && !line.startsWith('error:')) {
          onProgress(data.toString().replace(/\r?\n/g, ''));
        }
      });
    }
  });
}

// ─── 专用命令包装 ───────────────────────────────────────────────

/**
 * git push 包装（含自动重试）
 *
 * @param {object}   opts
 * @param {string}   [opts.remote='origin']
 * @param {string}   [opts.branch]         - 默认为当前分支
 * @param {boolean}  [opts.force=false]
 * @param {boolean}  [opts.setUpstream=false]
 * @param {boolean}  [opts.tags=false]
 * @param {object}   [opts.retryOptions]   - 传给 withRetry
 * @param {string}   [opts.cwd]
 * @param {Function} [opts.onRetry]        - (attempt, delay, err) => void
 * @returns {Promise<{ stdout: string, code: number }>}
 */
export async function gitPush(opts = {}) {
  const {
    remote = 'origin',
    branch = '',
    force = false,
    setUpstream = false,
    tags = false,
    retryOptions = {},
    cwd = undefined,
    onRetry = undefined,
  } = opts;

  // 构建命令
  const parts = ['push', '-v'];
  if (force) parts.push('--force');
  if (setUpstream) parts.push('--set-upstream');
  if (tags) parts.push('--tags');

  parts.push(remote);
  if (branch) parts.push(branch);

  const cmd = parts.join(' ');

  // 是否大推送（包含 tags 或 force 视为大操作）
  const isLarge = tags || force;

  return withRetry(async () => {
    const result = runGit(cmd, { cwd, largePush: isLarge });
    if (result.code !== 0) {
      const errorMsg = result.stderr || result.stdout;
      const err = new Error(errorMsg);
      err.code = result.code;
      err.isTransient = isTransientError(errorMsg);
      throw err;
    }
    return result;
  }, {
    ...retryOptions,
    onRetry: (attempt, delay, err) => {
      if (onRetry) onRetry(attempt, delay, err);
    },
  });
}

/**
 * git pull 包装（含自动重试）
 *
 * @param {object}   opts
 * @param {string}   [opts.remote='origin']
 * @param {string}   [opts.branch]         - 默认为当前分支
 * @param {boolean}  [opts.rebase=false]   - --rebase
 * @param {boolean}  [opts.ffOnly=false]   - --ff-only
 * @param {object}   [opts.retryOptions]
 * @param {string}   [opts.cwd]
 * @param {Function} [opts.onRetry]
 * @returns {Promise<{ stdout: string, code: number }>}
 */
export async function gitPull(opts = {}) {
  const {
    remote = 'origin',
    branch = '',
    rebase = false,
    ffOnly = false,
    retryOptions = {},
    cwd = undefined,
    onRetry = undefined,
  } = opts;

  const parts = ['pull', '-v'];
  if (rebase) parts.push('--rebase');
  if (ffOnly) parts.push('--ff-only');

  parts.push(remote);
  if (branch) parts.push(branch);

  const cmd = parts.join(' ');

  return withRetry(async () => {
    const result = runGit(cmd, { cwd });
    if (result.code !== 0) {
      const errorMsg = result.stderr || result.stdout;
      const err = new Error(errorMsg);
      err.code = result.code;
      err.isTransient = isTransientError(errorMsg);
      throw err;
    }
    return result;
  }, {
    ...retryOptions,
    onRetry: (attempt, delay, err) => {
      if (onRetry) onRetry(attempt, delay, err);
    },
  });
}

/**
 * git merge 包装
 *
 * @param {object}   opts
 * @param {string}   opts.branch            - 要合并的分支名
 * @param {boolean}  [opts.noFastForward=false]
 * @param {boolean}  [opts.squash=false]
 * @param {string}   [opts.message]         - merge commit message
 * @param {string}   [opts.cwd]
 * @returns {{ stdout: string, code: number }}
 */
export function gitMerge(opts = {}) {
  const {
    branch,
    noFastForward = false,
    squash = false,
    message = undefined,
    cwd = undefined,
  } = opts;

  if (!branch) throw new Error('gitMerge: branch 参数不能为空');

  const parts = ['merge'];
  if (noFastForward) parts.push('--no-ff');
  if (squash) parts.push('--squash');
  if (message) parts.push(`-m "${message.replace(/"/g, '\\"')}"`);

  parts.push(branch);

  return runGit(parts.join(' '), { cwd });
}

// ─── Git 远程管理 ───────────────────────────────────────────────

/**
 * 添加/修改远程仓库地址
 */
export function setRemoteUrl(remote, url, cwd) {
  return runGit(`remote set-url ${remote} "${url}"`, { cwd });
}

/**
 * 获取远程仓库地址
 */
export function getRemoteUrl(remote = 'origin', cwd) {
  const result = runGit(`remote get-url ${remote}`, { cwd, silent: true });
  return result.code === 0 ? result.stdout : null;
}

/**
 * 获取当前分支名
 */
export function getCurrentBranch(cwd) {
  const result = runGit('rev-parse --abbrev-ref HEAD', { cwd, silent: true });
  return result.code === 0 ? result.stdout : 'HEAD';
}

/**
 * 检测当前目录是否为 git 仓库
 */
export function isGitRepo(dir) {
  try {
    const result = runGit('rev-parse --git-dir', { cwd: dir, silent: true });
    return result.code === 0;
  } catch {
    return false;
  }
}

/**
 * 将 SSH 格式仓库地址转为 HTTPS（用于网络较差的场景）
 * git@github.com:user/repo.git → https://github.com/user/repo.git
 */
export function sshUrlToHttps(sshUrl) {
  const match = sshUrl.match(/git@([^:]+):(.+)\.git$/);
  if (match) {
    return `https://${match[1]}/${match[2]}.git`;
  }
  return sshUrl; // 不是 SSH 格式则原样返回
}

export default {
  runGit,
  runGitAsync,
  gitPush,
  gitPull,
  gitMerge,
  setRemoteUrl,
  getRemoteUrl,
  getCurrentBranch,
  isGitRepo,
  sshUrlToHttps,
};
