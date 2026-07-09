# blog-cli

**前沿观瞻博客** 命令行工具 — 在本地编写 Markdown 文章，一键推送、发布、拉取到后端服务。

---

## 目录

- [安装](#安装)
- [快速开始](#快速开始)
- [CLI 功能介绍](#cli-功能介绍)
  - [`blog-cli init` — 初始化配置](#blog-cli-init--初始化配置)
  - [`blog-cli push` — 推送文章](#blog-cli-push--推送文章)
  - [`blog-cli publish` — 发布文章](#blog-cli-publish--发布文章)
  - [`blog-cli pull` — 拉取文章](#blog-cli-pull--拉取文章)
- [Markdown 规范](#markdown-规范)
- [图片自动上传](#图片自动上传)
- [配置文件](#配置文件)
- [常见问题](#常见问题)

---

## 安装

### 前置条件

- **Node.js** >= 18

### 全局安装

```bash
cd blog-cli
npm install
npm link
```

安装后即可全局使用 `blog-cli` 命令：

```bash
blog-cli --help
```

### 验证安装

```bash
blog-cli --version
```

输出效果：

```
╔══════════════════════════════════════════════╗
║  前沿观瞻博客 - 命令行工具                    ║
╠══════════════════════════════════════════════╣
║  版本: 1.0.0                                 ║
║  名称: blog-cli                              ║
║  描述: 本地 Markdown 推送 / 管理              ║
║  Node: v20.20.2                              ║
║  平台: win32                                 ║
╚══════════════════════════════════════════════╝
```

---

## 快速开始

### 1. 初始化配置

```bash
blog-cli init
```

按提示输入：
- **后端 API 地址** — 默认 `http://localhost:8080`
- **认证 Token** — 从博客管理后台获取的 JWT Token
- **默认作者名**

配置会保存在 `config/default.json`。

### 2. 推送一篇文章

```bash
# 推送单篇
blog-cli push ./articles/my-post.md

# 批量推送目录下所有 .md 文件
blog-cli push ./articles --batch
```

### 3. 发布文章

```bash
blog-cli publish 42
```

### 4. 拉取文章到本地

```bash
blog-cli pull 42 -o ./pulled
```

---

## CLI 功能介绍

### `blog-cli init` — 初始化配置

交互式配置向导，设置 API 地址与认证信息。

```bash
blog-cli init
```

配置项：

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `apiBaseUrl` | 后端 API 网关地址 | `http://localhost:8080` |
| `token` | JWT 认证 Token | `""` |
| `defaultAuthor` | 默认作者名 | `""` |

**响应示例**

```text
✅ 配置已保存

  配置摘要:
  ▪ API 地址:  http://localhost:8080
  ▪ Token:     eyJhbGci****YxMzk
  ▪ 默认作者:  wanqiu
  ▪ 保存路径:  .../blog-cli/config/default.json
```

> Token 中间部分自动用 `****` 脱敏显示，不会完整暴露。

---

### `blog-cli push` — 推送文章

将本地 Markdown 文件推送到后端，支持单篇推送和批量推送。

```bash
blog-cli push [target] [options]
```

**参数**

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `target` | 文件路径或目录路径 | `.`（当前目录） |

**选项**

| 选项 | 说明 |
|------|------|
| `-b, --batch` | 批量推送目录下所有 `.md` 文件 |

**工作流程**

```
① 解析 Markdown ──→ ② 提取 Frontmatter 元数据
                         │
                         ▼
                 ③ 扫描正文中的本地图片
                         │
                         ▼
                 ④ 上传图片（实时进度条）
                         │
                         ▼
                 ⑤ 替换图片链接为 CDN URL
                         │
                         ▼
                 ⑥ 调用 API 创建文章（草稿）
```

**图片上传进度条**

当文章中包含本地图片时，CLI 会展示实时上传进度：

```text
📷 发现 2 张本地图片，开始上传...

  📤 hero.png [████████████████████] 100% | 245/245 KB
  ✅ hero.png 上传完成

  📤 icon.svg [████████████████████] 100% | 12/12 KB
  ✅ icon.svg 上传完成
```

**响应示例**

推送完成后展示完整文章详情：

```text
  ✅ 推送成功

  文章详情:
  ▪ 标题:   我的第一篇文章
  ▪ ID:     42
  ▪ 字数:   2,380 字
  ▪ 图片:   2 张
  ▪ 状态:   草稿
  ▪ 标签:   技术, 前端
  ▪ 来源:   D:/articles/my-post.md
```

---

### `blog-cli publish` — 发布文章

将草稿文章发布为已发布状态。

```bash
blog-cli publish <id>
```

**参数**

| 参数 | 说明 |
|------|------|
| `id` | 文章 ID（必填） |

该命令调用后端 `PATCH /api/article/{id}/publish` 接口。

**响应示例**

```text
  ✅ 发布成功

  发布详情:
  ▪ 文章:   我的第一篇文章
  ▪ ID:     42
  ▪ 状态:   草稿 → 已发布
  ▪ 时间:   2026/7/9 18:30:15
```

---

### `blog-cli pull` — 拉取文章

从后端拉取文章保存为本地 Markdown 文件（含 Frontmatter 头）。

```bash
blog-cli pull <id> [options]
```

**参数**

| 参数 | 说明 |
|------|------|
| `id` | 文章 ID（必填） |

**选项**

| 选项 | 说明 | 默认值 |
|------|------|--------|
| `-o, --output <dir>` | 保存目录 | `./pulled` |

生成的文件名格式：`{id}-{标题}.md`（特殊字符会被替换为 `_`）。

**响应示例**

```text
  ✅ 拉取成功

  拉取详情:
  ▪ 标题:   我的第一篇文章
  ▪ ID:     42
  ▪ 字数:   2,380 字
  ▪ 大小:   4.8 KB
  ▪ 标签:   技术, 前端
  ▪ 摘要:   这是一篇示例文章的摘要...
  ▪ 保存到: D:/pulled/42-我的第一篇文章.md
```

---

### `blog-cli delete` — 删除文章

从后台删除指定文章（已发布的文章不可删除）。

```bash
blog-cli delete <id>
```

**参数**

| 参数 | 说明 |
|------|------|
| `id` | 文章 ID（必填） |

**响应示例**

```text
  ✅ 删除成功

  删除详情:
  ▪ ID:     42
  ▪ 时间:   2026/7/9 18:30:15
```

---

### `blog-cli preview` — 本地预览

在本地将 Markdown 文件渲染为 HTML，并在浏览器中打开预览排版效果。

```bash
blog-cli preview <file>
```

**参数**

| 参数 | 说明 |
|------|------|
| `file` | `.md` 文件路径（必填） |

**工作流程**

```
① 解析 Frontmatter ──→ ② marked 渲染 Markdown → HTML
                              │
                              ▼
                      ③ 生成精美 HTML 页面
                              │
                              ▼
                      ④ 自动打开浏览器预览
```

**预览页面特性**

- 自动提取文章标题、日期、标签、摘要并展示在页面顶部
- Markdown 正文渲染为优雅排版的 HTML（含代码高亮、引用块、表格、图片等）
- 页面顶部有「本地预览」横幅标识，区分于线上文章
- 预览文件保存在 `blog-cli/.preview/` 目录（已加入 `.gitignore`）

**响应示例**

```text
  ✅ 预览已生成

  预览详情:
  ▪ 标题:   我的第一篇文章
  ▪ 日期:   2026/7/9
  ▪ 标签:   技术, 前端
  ▪ 字数:   2,380 字
  ▪ 大小:   8.2 KB
  ▪ 保存到: D:/blog-cli/.preview/我的第一篇文章.html
```

> 浏览器会自动打开生成的 HTML 文件，无需手动操作。

---

## Markdown 规范

推送的 Markdown 文件需在文件头部包含 YAML Frontmatter 元数据：

```markdown
---
title: 我的第一篇文章
date: 2026-07-09
tags: [技术,前端]
category: 1
summary: 这是一篇示例文章的摘要
coverImage: https://example.com/cover.jpg
---

这里是文章正文...
```

**Frontmatter 字段说明**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `title` | string | ✅ | 文章标题 |
| `date` | date | ❌ | 创建日期 |
| `tags` | array | ❌ | 标签列表 |
| `category` | string/number | ❌ | 分类 ID |
| `summary` | string | ❌ | 文章摘要 |
| `coverImage` / `cover_image` | string | ❌ | 封面图 URL |

> **注意**：若 `title` 缺失，该文件会被跳过。

---

## 图片自动上传

推送时，CLI 会自动扫描正文中的本地图片引用并上传至后端文件服务：

```markdown
![架构图](./images/architecture.png)
![截图](assets/screenshot.jpg)
```

- ✅ 支持相对路径图片（相对于 `.md` 文件所在目录）
- ✅ 支持嵌套子目录中的图片
- ✅ 上传完成后自动将本地路径替换为后端返回的 CDN URL
- ❌ 忽略 `http://` / `https://` 开头的远程图片链接
- ❌ 忽略不存在的本地图片路径

---

## 配置文件

配置存储在 `config/default.json`，格式如下：

```json
{
  "apiBaseUrl": "http://localhost:8080",
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "defaultAuthor": "wanqiu",
  "outputDir": "./pulled"
}
```

可通过 `blog-cli init` 交互式修改，也可直接编辑 JSON 文件。

---

## 常见问题

### Q: 推送时提示 "路径不存在"

确保 `target` 参数指向正确的文件或目录路径，支持绝对路径和相对路径。

### Q: 上传图片进度条卡在 0%

常见原因：
- 后端文件服务未启动或路由未配置
- Token 过期或无效
- 网络不通

### Q: 推送成功但文章内容中没有图片

检查图片路径是否在 Markdown 正文中以 `![](path)` 格式引用，且图片文件确实存在于本地。

### Q: `npm link` 后找不到 `blog-cli` 命令

```bash
# 查看 npm 全局 bin 路径
npm config get prefix
# 确保该路径在系统的 PATH 环境变量中
```

---

## 依赖

| 包 | 用途 |
|----|------|
| `commander` | CLI 命令解析 |
| `axios` | HTTP 请求 |
| `chalk` | 终端彩色输出 |
| `gray-matter` | 解析 Markdown Frontmatter |
| `inquirer` | 交互式提示 |
| `ora` | 旋转动画（文章推送阶段） |
| `cli-progress` | 实时进度条（图片上传阶段） |
| `form-data` | multipart 表单上传 |
