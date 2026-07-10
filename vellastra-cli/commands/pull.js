import fs from 'fs';
import path from 'path';
import chalk from 'chalk';
import ora from 'ora';
import { getArticle, getConfig } from '../lib/api-client.js';

/**
 * vellastra-cli pull <id>
 * 从后台拉取文章到本地 Markdown 文件
 */
export async function pullCommand(id, options) {
  const spinner = ora(`拉取文章 ID: ${id}`).start();

  try {
    const result = await getArticle(id);
    const article = result.data;

    // 组装 frontmatter
    const frontmatter = [
      '---',
      `title: "${article.title}"`,
      `date: ${new Date(article.createTime).toISOString().split('T')[0]}`,
      article.categoryId ? `category: ${article.categoryId}` : '',
      article.tags ? `tags: [${article.tags}]` : '',
      article.summary ? `summary: "${article.summary}"` : '',
      article.coverImage ? `coverImage: "${article.coverImage}"` : '',
      '---',
      '',
    ].filter(Boolean).join('\n');

    const markdown = frontmatter + article.content;

    // 写入文件
    const outputDir = path.resolve(options.output);
    if (!fs.existsSync(outputDir)) {
      fs.mkdirSync(outputDir, { recursive: true });
    }

    const fileName = `${article.id}-${article.title.replace(/[/\\?%*:|"<>]/g, '_')}.md`;
    const filePath = path.join(outputDir, fileName);
    fs.writeFileSync(filePath, markdown, 'utf-8');

    // 获取文件大小
    const fileStat = fs.statSync(filePath);
    const fileSizeKB = (fileStat.size / 1024).toFixed(1);
    const wordCount = article.content.length;

    spinner.stop();

    console.log('');
    console.log(chalk.green('  ✅ 拉取成功'));
    console.log(chalk.bold('  拉取详情:'));
    console.log(`  ${chalk.yellow('▪')} 标题:   ${chalk.white(article.title)}`);
    console.log(`  ${chalk.yellow('▪')} ID:     ${chalk.cyan(article.id)}`);
    console.log(`  ${chalk.yellow('▪')} 字数:   ${chalk.white(wordCount.toLocaleString())} 字`);
    console.log(`  ${chalk.yellow('▪')} 大小:   ${chalk.white(fileSizeKB)} KB`);
    if (article.tags && article.tags.length > 0) {
      const tags = Array.isArray(article.tags) ? article.tags.join(', ') : article.tags;
      console.log(`  ${chalk.yellow('▪')} 标签:   ${chalk.white(tags)}`);
    }
    if (article.summary) {
      const summaryPreview = article.summary.length > 60
        ? article.summary.substring(0, 60) + '...'
        : article.summary;
      console.log(`  ${chalk.yellow('▪')} 摘要:   ${chalk.dim(summaryPreview)}`);
    }
    console.log(`  ${chalk.yellow('▪')} 保存到: ${chalk.dim(filePath)}`);
    console.log('');
  } catch (err) {
    spinner.fail(chalk.red(`❌ 拉取失败: ${err.message}`));
  }
}
