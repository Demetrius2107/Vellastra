#!/usr/bin/env node

import { Command } from 'commander';
import chalk from 'chalk';
import { fileURLToPath } from 'url';
import { initCommand } from '../commands/init.js';
import { pushCommand } from '../commands/push.js';
import { publishCommand } from '../commands/publish.js';
import { pullCommand } from '../commands/pull.js';
import { deleteCommand } from '../commands/delete.js';
import { previewCommand } from '../commands/preview.js';
import {
  netTest,
  netDiagnose,
  netConfig,
  netProxy,
  netRetry,
} from '../commands/net.js';

const pkg = { version: '1.0.0', name: 'vellastra' };

const program = new Command();

const versionBanner = `
${chalk.cyan('╔══════════════════════════════════════════════╗')}
${chalk.cyan('║')}  ${chalk.bold('前沿观瞻博客 - 命令行工具')}             ${chalk.cyan('║')}
${chalk.cyan('╠══════════════════════════════════════════════╣')}
${chalk.cyan('║')}  ${chalk.yellow('版本')}: ${chalk.white(pkg.version)}                                    ${chalk.cyan('║')}
${chalk.cyan('║')}  ${chalk.yellow('名称')}: ${chalk.white('vellastra')}                                      ${chalk.cyan('║')}
${chalk.cyan('║')}  ${chalk.yellow('描述')}: ${chalk.white('本地 Markdown 推送 / 管理')}                 ${chalk.cyan('║')}
${chalk.cyan('║')}  ${chalk.yellow('Node')}: ${chalk.white(process.version)}                                 ${chalk.cyan('║')}
${chalk.cyan('║')}  ${chalk.yellow('平台')}: ${chalk.white(process.platform)}                                 ${chalk.cyan('║')}
${chalk.cyan('╚══════════════════════════════════════════════╝')}
`;

program
  .name(pkg.name)
  .description('前沿观瞻博客命令行工具 - 本地 Markdown 推送/管理')
  .version(versionBanner, '-v, --version', '显示版本信息');

// ── 美化 help 输出顶部提示 ──────────────────────────────────────
program.addHelpText('beforeAll', chalk.cyan.bold('\n📋 所有可用命令:\n'));

// ── help 输出底部增加使用指引 ──────────────────────────────────
program.addHelpText('afterAll', `
${chalk.cyan('💡 常用操作速查:')}
  ${chalk.white('  vellastra init')}              首次使用，配置后端地址和 Token
  ${chalk.white('  vellastra push ./doc/*.md')}   推送本地文章
  ${chalk.white('  vellastra publish <id>')}      将草稿发布上线
  ${chalk.white('  vellastra pull <id>')}         拉取远程文章到本地
  ${chalk.white('  vellastra net test')}          检测 GitHub 连通性
  ${chalk.white('  vellastra net config --apply')} ${chalk.dim('← 一键优化 Git 网络参数（推荐）')}
  ${chalk.white('  vellastra net proxy --apply')}  ${chalk.dim('← 如果走代理/VPN 才需要')}
${chalk.cyan('────────────────────────────────────────────')}
`);

program
  .command('init')
  .description('初始化配置（API 地址 / Token）')
  .action(initCommand);

program
  .command('push')
  .description('推送单篇文章或批量推送目录')
  .argument('[target]', '文件路径或目录路径', '.')
  .option('-b, --batch', '批量推送目录下所有 .md 文件')
  .action(pushCommand);

program
  .command('publish')
  .description('触发文章发布（草稿 → 已发布）')
  .argument('<id>', '文章 ID')
  .action(publishCommand);

program
  .command('pull')
  .description('从后台拉取文章到本地')
  .argument('<id>', '文章 ID')
  .option('-o, --output <dir>', '输出目录', './pulled')
  .action(pullCommand);

program
  .command('delete')
  .description('删除文章（已发布文章不可删除）')
  .argument('<id>', '文章 ID')
  .action(deleteCommand);

program
  .command('preview')
  .description('本地预览 Markdown 文章排版效果')
  .argument('<file>', '.md 文件路径')
  .action(previewCommand);

// ── net: GitHub 网络工具箱 ──────────────────────────────────────
const net = program.command('net')
  .description('🌐 GitHub 网络工具箱 - 连通性检测 / 代理 / 重试 / Git 配置优化');

net.command('test')
  .description('快速连通性检测')
  .option('-v, --verbose', '详细输出（显示仓库信息）')
  .action(netTest);

net.command('diagnose')
  .description('全链路端点诊断（含延迟排序）')
  .action(netDiagnose);

net.command('config')
  .description('Git 网络优化配置（预览 / 应用 / 查看）')
  .option('--apply', '一键应用 Git 网络优化参数（无需代理即可提升）')
  .option('--show', '查看当前 Git 网络相关配置')
  .action(netConfig);

net.command('proxy')
  .description('Git 代理管理（查看 / 设置 / 清除）')
  .option('--apply [url]', '设置代理 URL（不传则从环境变量自动检测）')
  .option('--clear', '清除 Git 代理配置')
  .action(netProxy);

net.command('retry')
  .description('测试重试机制')
  .option('--target <url>', '重试测试目标 URL', 'https://github.com')
  .option('--count <n>', '最大重试次数', parseInt, 3)
  .action(netRetry);

program.parse(process.argv);
