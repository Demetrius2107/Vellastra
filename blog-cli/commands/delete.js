import chalk from 'chalk';
import ora from 'ora';
import { deleteArticle } from '../lib/api-client.js';

/**
 * blog-cli delete <id>
 * 删除文章（已发布的文章不可删除）
 */
export async function deleteCommand(id) {
  const spinner = ora(`删除文章 ID: ${id}`).start();

  try {
    const result = await deleteArticle(id);
    spinner.stop();

    const now = new Date().toLocaleString('zh-CN');

    console.log('');
    console.log(chalk.green('  ✅ 删除成功'));
    console.log(chalk.bold('  删除详情:'));
    console.log(`  ${chalk.yellow('▪')} ID:     ${chalk.cyan(id)}`);
    console.log(`  ${chalk.yellow('▪')} 时间:   ${chalk.white(now)}`);
    console.log('');
  } catch (err) {
    spinner.fail(chalk.red(`❌ 删除失败: ${err.message}`));
  }
}
