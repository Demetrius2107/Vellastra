import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import chalk from 'chalk';
import inquirer from 'inquirer';
import { saveConfig } from '../lib/api-client.js';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const CONFIG_PATH = path.resolve(__dirname, '../config/default.json');

/**
 * blog-cli init
 * 交互式初始化配置：API 地址 + Token
 */
export async function initCommand() {
  console.log(chalk.cyan('\n📝 初始化 blog-cli 配置\n'));

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
  console.log(chalk.green('\n✅ 配置已保存到：'), CONFIG_PATH);
}
