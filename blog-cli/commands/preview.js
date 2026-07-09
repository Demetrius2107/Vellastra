import fs from 'fs';
import path from 'path';
import chalk from 'chalk';
import { execSync } from 'child_process';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

/**
 * blog-cli preview <file>
 * 本地预览 Markdown 文章效果（生成 HTML 并在浏览器打开）
 */
export async function previewCommand(filePath) {
  const resolvedPath = path.resolve(filePath);

  if (!fs.existsSync(resolvedPath)) {
    console.error(chalk.red(`❌ 文件不存在: ${resolvedPath}`));
    process.exit(1);
  }

  if (!resolvedPath.endsWith('.md')) {
    console.error(chalk.red('❌ 请提供 .md 文件'));
    process.exit(1);
  }

  try {
    console.log(chalk.cyan('\n📄 正在生成预览...\n'));

    // 动态导入 marked
    const { marked } = await import('marked');

    // 读取并解析 Markdown
    const raw = fs.readFileSync(resolvedPath, 'utf-8');
    const { frontmatter, content } = parseFrontmatter(raw);

    // 将 Markdown 正文转为 HTML
    const bodyHtml = marked.parse(content);

    // 组装 HTML 页面
    const articleTitle = frontmatter.title || path.basename(resolvedPath, '.md');
    const articleDate = frontmatter.date
      ? new Date(frontmatter.date).toLocaleDateString('zh-CN')
      : '';
    const tags = Array.isArray(frontmatter.tags) ? frontmatter.tags : [];

    const html = buildPreviewHtml({
      title: articleTitle,
      date: articleDate,
      tags,
      summary: frontmatter.summary || '',
      bodyHtml,
    });

    // 写入临时 HTML 文件
    const outputDir = path.resolve(__dirname, '../.preview');
    if (!fs.existsSync(outputDir)) {
      fs.mkdirSync(outputDir, { recursive: true });
    }
    const htmlFile = path.join(outputDir, `${path.basename(resolvedPath, '.md')}.html`);
    fs.writeFileSync(htmlFile, html, 'utf-8');

    const fileSizeKB = (fs.statSync(htmlFile).size / 1024).toFixed(1);

    // 统计信息
    const wordCount = content.length;

    console.log(chalk.green('  ✅ 预览已生成'));
    console.log(chalk.bold('  预览详情:'));
    console.log(`  ${chalk.yellow('▪')} 标题:   ${chalk.white(articleTitle)}`);
    if (articleDate) console.log(`  ${chalk.yellow('▪')} 日期:   ${chalk.white(articleDate)}`);
    if (tags.length > 0) console.log(`  ${chalk.yellow('▪')} 标签:   ${chalk.white(tags.join(', '))}`);
    console.log(`  ${chalk.yellow('▪')} 字数:   ${chalk.white(wordCount.toLocaleString())} 字`);
    console.log(`  ${chalk.yellow('▪')} 大小:   ${chalk.white(fileSizeKB)} KB`);
    console.log(`  ${chalk.yellow('▪')} 保存到: ${chalk.dim(htmlFile)}`);
    console.log('');

    // 自动在浏览器打开
    try {
      const platform = process.platform;
      if (platform === 'win32') {
        execSync(`start "" "${htmlFile}"`, { stdio: 'ignore' });
      } else if (platform === 'darwin') {
        execSync(`open "${htmlFile}"`, { stdio: 'ignore' });
      } else {
        execSync(`xdg-open "${htmlFile}"`, { stdio: 'ignore' });
      }
      console.log(chalk.dim('  浏览器已自动打开预览页面\n'));
    } catch {
      console.log(chalk.yellow(`  请手动在浏览器打开: ${htmlFile}\n`));
    }
  } catch (err) {
    console.error(chalk.red(`❌ 预览生成失败: ${err.message}`));
    process.exit(1);
  }
}

/**
 * 简易 frontmatter 解析（不依赖 gray-matter，预览命令轻量化）
 */
function parseFrontmatter(raw) {
  const frontmatter = {};
  let content = raw;

  const match = raw.match(/^---\n([\s\S]*?)\n---\n/);
  if (match) {
    const yamlBlock = match[1];
    content = raw.slice(match[0].length);

    // 逐行解析简单 key: value
    for (const line of yamlBlock.split('\n')) {
      const kvMatch = line.match(/^\s*(\w+)\s*:\s*(.+)$/);
      if (kvMatch) {
        let key = kvMatch[1];
        let value = kvMatch[2].trim();

        // 去掉引号
        if ((value.startsWith('"') && value.endsWith('"')) ||
            (value.startsWith("'") && value.endsWith("'"))) {
          value = value.slice(1, -1);
        }

        // 解析数组 [a, b, c]
        if (value.startsWith('[') && value.endsWith(']')) {
          value = value.slice(1, -1).split(',').map(v => v.trim().replace(/['"]/g, ''));
        }

        frontmatter[key] = value;
      }
    }
  }

  return { frontmatter, content };
}

/**
 * 生成预览 HTML
 */
function buildPreviewHtml({ title, date, tags, summary, bodyHtml }) {
  const tagsHtml = tags.length > 0
    ? tags.map(t => `<span class="tag">${t}</span>`).join('')
    : '';

  return `<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${escapeHtml(title)} — 预览</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Noto Sans SC", "PingFang SC", "Microsoft YaHei", sans-serif;
      color: #1a1a2e;
      background: #f5f6fa;
      line-height: 1.8;
    }
    .preview-banner {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #fff;
      padding: 12px 24px;
      text-align: center;
      font-size: 14px;
      letter-spacing: 1px;
    }
    .preview-banner span { opacity: 0.8; }
    .container {
      max-width: 800px;
      margin: 40px auto;
      padding: 0 20px;
    }
    .article-card {
      background: #fff;
      border-radius: 12px;
      padding: 48px;
      box-shadow: 0 2px 20px rgba(0,0,0,0.08);
    }
    .article-title {
      font-size: 28px;
      font-weight: 700;
      color: #1a1a2e;
      margin-bottom: 12px;
      line-height: 1.4;
    }
    .article-meta {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      align-items: center;
      margin-bottom: 24px;
      font-size: 14px;
      color: #888;
    }
    .article-meta .date { color: #888; }
    .tag {
      display: inline-block;
      background: #eef0ff;
      color: #667eea;
      padding: 2px 10px;
      border-radius: 12px;
      font-size: 12px;
    }
    .summary {
      background: #f8f9ff;
      border-left: 3px solid #667eea;
      padding: 12px 16px;
      margin-bottom: 28px;
      font-size: 14px;
      color: #666;
      border-radius: 0 6px 6px 0;
    }
    .divider {
      border: none;
      border-top: 1px solid #eee;
      margin: 28px 0;
    }
    /* Markdown 正文样式 */
    .article-body h1 { font-size: 24px; margin: 28px 0 12px; color: #1a1a2e; }
    .article-body h2 { font-size: 20px; margin: 24px 0 10px; color: #1a1a2e; }
    .article-body h3 { font-size: 17px; margin: 20px 0 8px; color: #1a1a2e; }
    .article-body p { margin-bottom: 14px; color: #333; }
    .article-body ul, .article-body ol { margin: 0 0 14px 20px; color: #333; }
    .article-body li { margin-bottom: 4px; }
    .article-body code {
      background: #f0f0f5;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 13px;
      color: #e74c3c;
      font-family: "Fira Code", "JetBrains Mono", Consolas, monospace;
    }
    .article-body pre {
      background: #1e1e2e;
      color: #cdd6f4;
      padding: 16px;
      border-radius: 8px;
      overflow-x: auto;
      margin-bottom: 16px;
      font-size: 13px;
      line-height: 1.6;
    }
    .article-body pre code {
      background: none;
      color: inherit;
      padding: 0;
      font-size: inherit;
    }
    .article-body blockquote {
      border-left: 4px solid #667eea;
      padding: 8px 16px;
      margin: 0 0 14px;
      color: #666;
      background: #f8f9ff;
      border-radius: 0 6px 6px 0;
    }
    .article-body img {
      max-width: 100%;
      border-radius: 8px;
      margin: 12px 0;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }
    .article-body a { color: #667eea; text-decoration: none; }
    .article-body a:hover { text-decoration: underline; }
    .article-body table {
      width: 100%;
      border-collapse: collapse;
      margin-bottom: 14px;
    }
    .article-body th, .article-body td {
      border: 1px solid #e0e0e0;
      padding: 8px 12px;
      text-align: left;
    }
    .article-body th { background: #f5f6fa; font-weight: 600; }
    .preview-footer {
      text-align: center;
      padding: 20px;
      color: #aaa;
      font-size: 12px;
    }
  </style>
</head>
<body>
  <div class="preview-banner"><span>📄 本地预览 · 此页面为临时生成，仅用于查看文章排版效果</span></div>
  <div class="container">
    <article class="article-card">
      <h1 class="article-title">${escapeHtml(title)}</h1>
      <div class="article-meta">
        ${date ? `<span class="date">📅 ${escapeHtml(date)}</span>` : ''}
        ${tagsHtml ? `<span>🏷 ${tagsHtml}</span>` : ''}
      </div>
      ${summary ? `<div class="summary">${escapeHtml(summary)}</div>` : ''}
      <hr class="divider">
      <div class="article-body">${bodyHtml}</div>
    </article>
    <div class="preview-footer">
      由 blog-cli preview 生成 · ${new Date().toLocaleString('zh-CN')}
    </div>
  </div>
</body>
</html>`;
}

function escapeHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}
