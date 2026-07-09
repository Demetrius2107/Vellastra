import chalk from 'chalk';
import ora from 'ora';
import { publishArticle } from '../lib/api-client.js';

/**
 * blog-cli publish <id>
 * 触发文章发布（草稿 → 已发布）
 */
export async function publishCommand(id) {
  const spinner = ora(`发布文章 ID: ${id}`).start();

  try {
    const result = await publishArticle(id);
    if (result.code === 200) {
      spinner.succeed(chalk.green(`✅ 文章 ${id} 发布成功`));
    } else {
      spinner.fail(chalk.red(`❌ 发布失败: ${result.message}`));
    }
  } catch (err) {
    spinner.fail(chalk.red(`❌ 发布失败: ${err.message}`));
  }
}
