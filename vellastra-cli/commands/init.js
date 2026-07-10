import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import chalk from 'chalk';
import inquirer from 'inquirer';
import { saveConfig } from '../lib/api-client.js';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const CONFIG_PATH = path.resolve(__dirname, '../config/default.json');

/**
 * vellastra-cli init
 * 交互式初始化配置：API 地址 + Token
 */
export async function initCommand() {
  console.log(chalk.cyan('\n📝 初始化 vellastra-cli 配置\n'));

  const current = JSON.parse(fs.readFileSync(CONFIG_PATH, 'utf-8'));

  const answers = await inquirer.prompt([
    {
      type: 'input',
      name: 'apiBaseUrl',
      message: '后端 API 地址：',
      default: current.apiBaseUrl,
    },
    {
      type: 'password',
      name: 'token',
      message: '认证 Token（从后台获取）：',
    },
    {
      type: 'input',
      name: 'defaultAuthor',
      message: '默认作者名：',
      default: current.defaultAuthor,
    },
  ]);

  saveConfig(answers);

  // 脱敏显示 Token
  const maskedToken = answers.token
    ? answers.token.substring(0, 8) + '****' + answers.token.slice(-4)
    : '(未设置)';

  console.log(chalk.green('\n✅ 配置已保存'));
  console.log('');
  console.log(chalk.bold('  配置摘要:'));
  console.log(`  ${chalk.yellow('▪')} API 地址:  ${chalk.white(answers.apiBaseUrl)}`);
  console.log(`  ${chalk.yellow('▪')} Token:     ${chalk.white(maskedToken)}`);
  console.log(`  ${chalk.yellow('▪')} 默认作者:  ${chalk.white(answers.defaultAuthor || '(未设置)')}`);
  console.log(`  ${chalk.yellow('▪')} 保存路径:  ${chalk.dim(CONFIG_PATH)}`);
  console.log('');
}
