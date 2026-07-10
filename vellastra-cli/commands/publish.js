import chalk from 'chalk';
import ora from 'ora';
import { publishArticle } from '../lib/api-client.js';

/**
 * vellastra-cli publish <id>
 * 触发文章发布（草稿 → 已发布）
 */
export async function publishCommand(id) {
  const spinner = ora(`发布文章 ID: ${id}`).start();

  try {
    const result = await publishArticle(id);
    if (result.code === 200) {
      spinner.stop();
      const now = new Date().toLocaleString('zh-CN');
      const articleTitle = result.data?.title || result.data || id;

      console.log('');
      console.log(chalk.green('  ✅ 发布成功'));
      console.log(chalk.bold('  发布详情:'));
      console.log(`  ${chalk.yellow('▪')} 文章:   ${chalk.white(articleTitle)}`);
      console.log(`  ${chalk.yellow('▪')} ID:     ${chalk.cyan(id)}`);
      console.log(`  ${chalk.yellow('▪')} 状态:   ${chalk.green('草稿 → 已发布')}`);
      console.log(`  ${chalk.yellow('▪')} 时间:   ${chalk.white(now)}`);
      console.log('');
    } else {
      spinner.fail(chalk.red(`❌ 发布失败: ${result.message}`));
    }
  } catch (err) {
    spinner.fail(chalk.red(`❌ 发布失败: ${err.message}`));
  }
}
