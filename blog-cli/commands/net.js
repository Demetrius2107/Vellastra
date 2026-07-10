/**
 * commands/net.js — blog net 指令
 * =================================
 * 作为 blog 的子命令组，提供一站式 GitHub 网络检测、诊断与优化。
 *
 * 用法:
 *   blog net test               — 快速连通性检测
 *   blog net diagnose           — 全链路端点诊断（含延迟排序）
 *   blog net config             — 查看/应用 Git 网络优化配置
 *   blog net proxy              — 查看/设置 Git 代理
 *   blog net retry              — 测试重试机制
 */

import chalk from 'chalk';
import ora from 'ora';

import {
  checkConnectivity,
  diagnoseAll,
  detectProxy,
  buildOptimizedGitConfig,
  buildGitProxyCommands,
  buildGitProxyClearCommands,
  pingHost,
  withRetry,
} from '../lib/git-net.js';

import { runGit, getCurrentBranch, isGitRepo, getRemoteUrl, sshUrlToHttps } from '../lib/git-wrapper.js';

// ─── test ────────────────────────────────────────────────────────

export async function netTest(options) {
  console.log(chalk.cyan.bold('\n🔍 GitHub 连通性快速检测\n'));

  const spinner = ora('检测中...').start();

  const result = await checkConnectivity();

  spinner.stop();

  if (result.ok) {
    console.log(`  ${chalk.green('✅')} 连通正常`);
    console.log(`  ${chalk.yellow('▪')} 延迟: ${chalk.white(result.latencyMs + ' ms')}`);
    console.log(`  ${chalk.yellow('▪')} 详情: ${chalk.dim(result.details)}`);
  } else {
    console.log(`  ${chalk.red('❌')} 连通失败`);
    console.log(`  ${chalk.yellow('▪')} 原因: ${chalk.red(result.details)}`);
    console.log('');
    console.log(chalk.yellow('  💡 建议:'));
    console.log('   1. 检查网络连接 / 确认 VPN 是否开启');
    console.log('   2. 运行 blog net diagnose 查看完整端点诊断');
    console.log(`   3. ${chalk.white('blog net config --apply')}  → 一键优化 Git 参数 ${chalk.green('(无需代理，推荐先试)')}`);
    console.log(`   4. ${chalk.white('blog net proxy --apply')}  → 自动检测系统代理并配置`);
  }

  if (options.verbose) {
    const cwd = process.cwd();
    const isRepo = isGitRepo(cwd);
    console.log('');
    console.log(chalk.cyan.bold('📋 当前仓库信息'));
    console.log(`  ${chalk.yellow('▪')} 工作目录: ${chalk.dim(cwd)}`);
    console.log(`  ${chalk.yellow('▪')} Git 仓库: ${isRepo ? chalk.green('是') : chalk.red('否')}`);
    if (isRepo) {
      console.log(`  ${chalk.yellow('▪')} 当前分支: ${chalk.white(getCurrentBranch(cwd))}`);
      const remoteUrl = getRemoteUrl('origin', cwd);
      if (remoteUrl) {
        console.log(`  ${chalk.yellow('▪')} Remote URL: ${chalk.dim(remoteUrl)}`);
        const httpsUrl = remoteUrl.includes('git@') ? sshUrlToHttps(remoteUrl) : null;
        if (httpsUrl) {
          console.log(`  ${chalk.yellow('▪')} HTTPS 替代: ${chalk.dim(httpsUrl)}`);
        }
      }
    }
  }

  console.log('');
}

// ─── diagnose ────────────────────────────────────────────────────

export async function netDiagnose() {
  console.log(chalk.cyan.bold('\n🩺 GitHub 全链路端点的诊断\n'));

  const spinner = ora(`正在检测 ${chalk.bold('6')} 个端点...`).start();
  const results = await diagnoseAll();
  spinner.stop();

  console.log('  ┌─────────┬──────────────────────────────┬──────────┬────────┬────────┐');
  console.log('  │ 状态    │ 端点                          │ DNS      │ TCP    │ HTTPS  │');
  console.log('  ├─────────┼──────────────────────────────┼──────────┼────────┼────────┤');

  for (const r of results) {
    const statusIcon = r.overall ? chalk.green('✓') : chalk.red('✗');
    const label = r.label.padEnd(28).slice(0, 28);
    const dnsStatus = r.dns?.ok ? chalk.green('✓') : chalk.red('✗');
    const tcpStatus = r.tcp?.reachable
      ? chalk.green(`${String(r.tcp.latencyMs).padStart(3)}ms`)
      : chalk.red('✗');
    const httpsStatus = r.https?.ok
      ? chalk.green(`${String(r.https.latencyMs).padStart(3)}ms`)
      : r.https?.note ? chalk.dim('—') : chalk.red('✗');

    console.log(`  │ ${statusIcon}    │ ${chalk.white(label)} │ ${dnsStatus}    │ ${tcpStatus} │ ${httpsStatus} │`);
  }

  console.log('  └─────────┴──────────────────────────────┴──────────┴────────┴────────┘');

  const best = results.find(r => r.overall);
  console.log('');
  if (best) {
    const bestLatency = best.https?.latencyMs ?? best.tcp?.latencyMs ?? '?';
    console.log(`  ${chalk.green('🏆 推荐:')} ${chalk.bold(best.label)} — 延迟 ${chalk.green(bestLatency)}ms`);

    if (best.label.includes('镜像')) {
      console.log(`  ${chalk.yellow('💡 提示:')} 当前网络环境到 GitHub 官方较慢，建议使用镜像站点。`);
    }
  } else {
    console.log(`  ${chalk.red('❌ 所有端点均不可达')}`);
    console.log('');
    console.log(chalk.yellow('  💡 建议:'));
    console.log('   1. 检查本地网络连接 / 确认 VPN 是否开启');
    console.log(`   2. ${chalk.white('blog net config --apply')}  → 一键优化 Git 参数 ${chalk.green('(无需代理，推荐先试)')}`);
    console.log(`   3. ${chalk.white('blog net proxy --apply')}  → 自动检测系统代理并配置`);
    console.log('   4. 检查防火墙 / DNS 设置');
  }

  const proxy = detectProxy();
  if (proxy.http || proxy.https) {
    console.log('');
    console.log(chalk.cyan('  📡 检测到系统代理:'));
    if (proxy.http)  console.log(`    HTTP:  ${chalk.dim(proxy.http)}`);
    if (proxy.https) console.log(`    HTTPS: ${chalk.dim(proxy.https)}`);
    if (proxy.all)   console.log(`    ALL:   ${chalk.dim(proxy.all)}`);
    console.log(`    ${chalk.yellow('💡 可通过')} ${chalk.white('blog net proxy --apply')} 将代理应用到 Git`);
  }

  console.log('');
}

// ─── config ──────────────────────────────────────────────────────

export async function netConfig(options) {
  if (options.apply) {
    await applyOptimizedConfig();
  } else if (options.show) {
    showCurrentGitConfig();
  } else {
    console.log(chalk.cyan.bold('\n⚙️  Git 网络优化配置\n'));
    console.log(`  ${chalk.dim('使用 --apply 应用，--show 查看当前配置')}\n`);

    const configs = buildOptimizedGitConfig();
    console.log('  ┌────────┬────────────────────────┬──────────────────────┐');
    console.log('  │ #      │ 配置项                  │ 推荐值               │');
    console.log('  ├────────┼────────────────────────┼──────────────────────┤');

    for (let i = 0; i < configs.length; i++) {
      const cfg = configs[i];
      const num = chalk.dim(String(i + 1).padStart(2));
      const key = chalk.white(cfg.key.padEnd(22).slice(0, 22));
      const val = chalk.green(cfg.value.padEnd(18).slice(0, 18));
      console.log(`  │ ${num}    │ ${key} │ ${val} │`);
    }

    console.log('  └────────┴────────────────────────┴──────────────────────┘');
    console.log('');
    console.log(`  ${chalk.dim('说明:')} 以上配置对慢速/不稳定网络有明显改善`);
    console.log(`  ${chalk.dim('执行')} ${chalk.white('blog net config --apply')} ${chalk.dim('一键应用')}`);
    console.log('');
  }
}

async function applyOptimizedConfig() {
  const spinner = ora('正在应用 Git 网络优化配置...').start();
  const configs = buildOptimizedGitConfig();
  let success = 0;
  let failed = 0;

  for (const cfg of configs) {
    const result = runGit(`config --global ${cfg.key} "${cfg.value}"`, { silent: true });
    if (result.code === 0) {
      success++;
    } else {
      failed++;
    }
  }

  spinner.stop();
  console.log(`  ${chalk.green('✅')} 应用完成: ${chalk.green(success + ' 项成功')}${failed > 0 ? chalk.red(', ' + failed + ' 项失败') : ''}`);
  console.log('');
  console.log(`  ${chalk.yellow('📋')} 已应用配置摘要:`);

  for (const cfg of configs) {
    console.log(`    ${chalk.green('✔')} ${chalk.white(cfg.key)} = ${chalk.green(cfg.value)}  ${chalk.dim('(' + cfg.desc + ')')}`);
  }
  console.log('');
  console.log(`  ${chalk.dim('💡 可以随时通过 git config --global --unset <key> 撤销')}`);
  console.log('');
}

function showCurrentGitConfig() {
  console.log(chalk.cyan.bold('\n📋 当前 Git 网络相关配置\n'));

  const keys = [
    'http.postBuffer', 'http.lowSpeedLimit', 'http.lowSpeedTime',
    'http.version', 'http.keepAlive', 'http.proxy', 'https.proxy',
    'core.compression', 'core.preloadindex',
  ];

  let hasAny = false;
  for (const key of keys) {
    const result = runGit(`config --global ${key}`, { silent: true });
    if (result.code === 0 && result.stdout) {
      console.log(`  ${chalk.yellow('▪')} ${chalk.white(key)} = ${chalk.green(result.stdout)}`);
      hasAny = true;
    }
  }

  if (!hasAny) {
    console.log(`  ${chalk.dim('未设置任何 Git 网络参数')}`);
  }
  console.log('');
}

// ─── proxy ───────────────────────────────────────────────────────

export async function netProxy(options) {
  if (options.clear) {
    await clearProxy();
  } else if (options.apply !== undefined) {
    await applyProxy(options.apply);
  } else {
    showProxyStatus();
  }
}

function showProxyStatus() {
  console.log(chalk.cyan.bold('\n📡 Git 代理状态\n'));

  const envProxy = detectProxy();
  console.log(`  ${chalk.bold('系统环境变量:')}`);
  console.log(`    HTTP_PROXY:  ${envProxy.http ? chalk.green(envProxy.http) : chalk.dim('(未设置)')}`);
  console.log(`    HTTPS_PROXY: ${envProxy.https ? chalk.green(envProxy.https) : chalk.dim('(未设置)')}`);
  console.log(`    ALL_PROXY:   ${envProxy.all ? chalk.green(envProxy.all) : chalk.dim('(未设置)')}`);
  console.log(`    来源:        ${chalk.dim(envProxy.source)}`);
  console.log('');

  console.log(`  ${chalk.bold('Git 配置:')}`);
  const gitHttpProxy = runGit('config --global http.proxy', { silent: true });
  const gitHttpsProxy = runGit('config --global https.proxy', { silent: true });
  console.log(`    http.proxy:  ${gitHttpProxy.code === 0 && gitHttpProxy.stdout ? chalk.green(gitHttpProxy.stdout) : chalk.dim('(未设置)')}`);
  console.log(`    https.proxy: ${gitHttpsProxy.code === 0 && gitHttpsProxy.stdout ? chalk.green(gitHttpsProxy.stdout) : chalk.dim('(未设置)')}`);
  console.log('');

  console.log(`  ${chalk.dim('设置:')}   ${chalk.white('blog net proxy --apply http://127.0.0.1:7890')}`);
  console.log(`  ${chalk.dim('清除:')}   ${chalk.white('blog net proxy --clear')}`);
  console.log('');
}

async function applyProxy(proxyUrl) {
  if (!proxyUrl || proxyUrl === true) {
    const envProxy = detectProxy();
    proxyUrl = envProxy.https || envProxy.http || envProxy.all;
    if (!proxyUrl) {
      console.log(chalk.red('❌ 未检测到系统代理，请手动提供 URL'));
      console.log(`  ${chalk.dim('用法:')} ${chalk.white('blog net proxy --apply http://127.0.0.1:7890')}`);
      return;
    }
  }

  const spinner = ora(`正在设置 Git 代理: ${proxyUrl}`).start();
  const commands = buildGitProxyCommands(proxyUrl);
  let allOk = true;

  for (const cmd of commands) {
    try {
      runGit(`config --global ${cmd.replace('git config --global ', '')}`, { silent: true });
    } catch {
      allOk = false;
    }
  }

  spinner.stop();
  if (allOk) {
    console.log(`  ${chalk.green('✅')} Git 代理已设置为: ${chalk.white(proxyUrl)}`);
    console.log(`  ${chalk.dim('可通过')} ${chalk.white('blog net proxy --clear')} ${chalk.dim('清除')}`);
  } else {
    console.log(`  ${chalk.red('⚠️')} 部分代理配置可能失败`);
  }
  console.log('');
}

async function clearProxy() {
  const spinner = ora('正在清除 Git 代理配置...').start();
  const commands = buildGitProxyClearCommands();
  let allOk = true;

  for (const cmd of commands) {
    try {
      runGit(`config --global --unset ${cmd.replace('git config --global --unset ', '')}`, { silent: true });
    } catch {
      allOk = false;
    }
  }

  spinner.stop();
  if (allOk) {
    console.log(`  ${chalk.green('✅')} Git 代理配置已清除`);
  } else {
    console.log(`  ${chalk.yellow('⚠️')} 清除时可能有部分配置不存在（可忽略）`);
  }
  console.log('');
}

// ─── retry ───────────────────────────────────────────────────────

export async function netRetry(options) {
  console.log(chalk.cyan.bold('\n🔄 重试机制测试\n'));

  const target = options.target || 'https://github.com';
  const count = options.count || 3;

  console.log(`  目标: ${chalk.white(target)}`);
  console.log(`  最大重试次数: ${chalk.white(count)}\n`);

  const spinner = ora('正在测试...').start();
  let attemptNum = 0;

  try {
    const result = await withRetry(async () => {
      attemptNum++;
      const tcp = await pingHost('github.com', 443);
      if (!tcp.reachable) {
        throw new Error(`TCP 连接失败: ${tcp.error}`);
      }
      return tcp;
    }, {
      maxRetries: count,
      onRetry: (attempt, delay, err) => {
        spinner.text = `第 ${attempt} 次重试 (等待 ${delay}ms)... 上次错误: ${err.message}`;
      },
    });

    spinner.stop();
    console.log(`  ${chalk.green('✅')} 重试测试成功`);
    console.log(`  ${chalk.yellow('▪')} 实际延迟: ${chalk.white(result.latencyMs + 'ms')}`);
    console.log(`  ${chalk.yellow('▪')} 总尝试次数: ${chalk.white(attemptNum)}`);
    console.log('');
  } catch (err) {
    spinner.stop();
    console.log(`  ${chalk.red('❌')} 重试测试失败，已耗尽 ${count} 次重试`);
    console.log(`  ${chalk.yellow('▪')} 最终错误: ${chalk.red(err.message)}`);
    console.log('');
  }
}
