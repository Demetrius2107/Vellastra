#!/usr/bin/env node

import { Command } from 'commander';
import chalk from 'chalk';
import { initCommand } from '../commands/init.js';
import { pushCommand } from '../commands/push.js';
import { publishCommand } from '../commands/publish.js';
import { pullCommand } from '../commands/pull.js';

const pkg = { version: '1.0.0', name: 'blog-cli' };

const program = new Command();

program
  .name(pkg.name)
  .description('前沿观瞻博客命令行工具 - 本地 Markdown 推送/管理')
  .version(pkg.version);

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
  .description('触发文章发布')
  .argument('<id>', '文章 ID')
  .action(publishCommand);

program
  .command('pull')
  .description('从后台拉取文章到本地')
  .argument('<id>', '文章 ID')
  .option('-o, --output <dir>', '输出目录', './pulled')
  .action(pullCommand);

program.parse(process.argv);
