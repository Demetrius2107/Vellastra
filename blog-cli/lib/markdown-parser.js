import fs from 'fs';
import matter from 'gray-matter';

/**
 * 解析 Markdown 文件，提取 frontmatter 和正文
 *
 * @param {string} filePath .md 文件路径
 * @returns {{ frontmatter: object, content: string, raw: string }}
 */
export function parseMarkdown(filePath) {
  const raw = fs.readFileSync(filePath, 'utf-8');
  const { data, content } = matter(raw);

  return {
    frontmatter: {
      title: data.title || '',
      date: data.date ? new Date(data.date).toISOString() : null,
      tags: Array.isArray(data.tags) ? data.tags : [],
      category: data.category || '',
      summary: data.summary || '',
      coverImage: data.coverImage || data.cover_image || '',
    },
    content,
    raw,
  };
}

/**
 * 扫描正文中的本地图片路径
 *
 * @param {string} content Markdown 正文
 * @param {string} baseDir .md 文件所在目录
 * @returns {string[]} 本地图片绝对路径列表
 */
export function scanLocalImages(content, baseDir) {
  const pattern = /!\[.*?\]\(([^)]+)\)/g;
  const paths = [];
  let match;
  while ((match = pattern.exec(content)) !== null) {
    const imgPath = match[1];
    // 只处理本地相对路径，忽略 http(s) 开头的
    if (!/^https?:\/\//.test(imgPath)) {
      const absolute = path.resolve(baseDir, imgPath);
      if (fs.existsSync(absolute)) {
        paths.push({ original: imgPath, absolute });
      }
    }
  }
  return paths;
}

import path from 'path';
