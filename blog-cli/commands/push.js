import fs from 'fs';
import path from 'path';
import chalk from 'chalk';
import ora from 'ora';
import { parseMarkdown, scanLocalImages } from '../lib/markdown-parser.js';
import { createArticle, updateArticle, uploadImage } from '../lib/api-client.js';

/**
 * blog-cli push [target]
 * 推送单篇文章或批量推送目录下所有 .md 文件
 */
export async function pushCommand(target, options) {
  const targetPath = path.resolve(target);

  if (!fs.existsSync(targetPath)) {
    console.error(chalk.red(`❌ 路径不存在: ${targetPath}`));
    process.exit(1);
  }

  const stats = fs.statSync(targetPath);

  if (stats.isFile() && targetPath.endsWith('.md')) {
    await pushSingleFile(targetPath);
  } else if (stats.isDirectory()) {
    const files = fs.readdirSync(targetPath).filter(f => f.endsWith('.md'));
    if (files.length === 0) {
      console.log(chalk.yellow('⚠️  目录中没有 .md 文件'));
      return;
    }
    console.log(chalk.cyan(`\n📂 发现 ${files.length} 篇 Markdown，开始批量推送...\n`));
    for (const file of files) {
      await pushSingleFile(path.join(targetPath, file));
    }
  } else {
    console.error(chalk.red('❌ 不支持的格式，请提供 .md 文件或目录'));
    process.exit(1);
  }
}

/**
 * 推送单篇文章
 */
async function pushSingleFile(filePath) {
  const spinner = ora(`正在处理: ${path.basename(filePath)}`).start();

  try {
    // ① 解析 Markdown
    const { frontmatter, content } = parseMarkdown(filePath);
    if (!frontmatter.title) {
      spinner.fail(chalk.yellow(`跳过 ${path.basename(filePath)}：缺少 title`));
      return;
    }

    // ② 扫描并上传本地图片
    const baseDir = path.dirname(filePath);
    const localImages = scanLocalImages(content, baseDir);
    let processedContent = content;

    for (const img of localImages) {
      spinner.text = `上传图片: ${path.basename(img.original)}`;
      const result = await uploadImage(img.original); // 后续替换为 absolute
      if (result && result.data) {
        processedContent = processedContent.replace(img.original, result.data.url);
      }
    }

    // ③ 推送文章
    spinner.text = '推送文章...';
    const articleData = {
      title: frontmatter.title,
      content: processedContent,
      summary: frontmatter.summary,
      coverImage: frontmatter.coverImage,
      categoryId: frontmatter.category,
      tags: frontmatter.tags,
      status: 0, // 草稿
    };

    const result = await createArticle(articleData);
    spinner.succeed(chalk.green(`✅ ${frontmatter.title} (ID: ${result.data})`));
  } catch (err) {
    spinner.fail(chalk.red(`❌ ${path.basename(filePath)}: ${err.message}`));
  }
}
