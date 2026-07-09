import fs from 'fs';
import path from 'path';
import chalk from 'chalk';
import ora from 'ora';
import { getArticle, getConfig } from '../lib/api-client.js';

/**
 * blog-cli pull <id>
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

    spinner.succeed(chalk.green(`✅ 已保存到: ${filePath}`));
  } catch (err) {
    spinner.fail(chalk.red(`❌ 拉取失败: ${err.message}`));
  }
}
