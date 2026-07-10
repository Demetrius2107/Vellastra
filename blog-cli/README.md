# blog

**前沿观瞻博客** 命令行工具 — 在本地编写 Markdown 文章，一键推送、发布、拉取到后端服务，
同时内置 GitHub 网络工具箱，提升 `git push/pull/merge` 在慢速/不稳定网络下的成功率。

---

## 目录

- [安装](#安装)
- [快速开始](#快速开始)
- [CLI 功能介绍](#cli-功能介绍)
  - [`blog init` — 初始化配置](#blog-init--初始化配置)
  - [`blog push` — 推送文章](#blog-push--推送文章)
  - [`blog publish` — 发布文章](#blog-publish--发布文章)
  - [`blog pull` — 拉取文章](#blog-pull--拉取文章)
  - [`blog delete` — 删除文章](#blog-delete--删除文章)
  - [`blog preview` — 本地预览](#blog-preview--本地预览)
  - [`blog net` — GitHub 网络工具箱](#blog-net--github-网络工具箱)
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

安装后即可全局使用 `blog` 命令：

```bash
blog --help
```

### 验证安装

```bash
blog --version
```

输出效果：

```
╔══════════════════════════════════════════════╗
║  前沿观瞻博客 - 命令行工具                    ║
╠══════════════════════════════════════════════╣
║  版本: 1.0.0                                 ║
║  名称: blog                                  ║
║  描述: 本地 Markdown 推送 / 管理              ║
║  Node: v20.20.2                              ║
║  平台: win32                                 ║
╚══════════════════════════════════════════════╝
```

---

## 快速开始

### 1. 初始化配置

```bash
blog init
```

按提示输入：
- **后端 API 地址** — 默认 `http://localhost:8080`
- **认证 Token** — 从博客管理后台获取的 JWT Token
- **默认作者名**

配置会保存在 `config/default.json`。

### 2. 推送一篇文章

```bash
# 推送单篇
blog push ./articles/my-post.md

# 批量推送目录下所有 .md 文件
blog push ./articles
```

### 3. 发布文章

```bash
blog publish 42
```

### 4. 拉取文章到本地

```bash
blog pull 42 -o ./pulled
```

---

## CLI 功能介绍

### `blog init` — 初始化配置

交互式配置向导，设置 API 地址与认证信息。

```bash
blog init
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

### `blog push` — 推送文章

将本地 Markdown 文件推送到后端，支持单篇推送和批量推送。

```bash
blog push [target] [options]
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

### `blog publish` — 发布文章

将草稿文章发布为已发布状态。

```bash
blog publish <id>
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

### `blog pull` — 拉取文章

从后端拉取文章保存为本地 Markdown 文件（含 Frontmatter 头）。

```bash
blog pull <id> [options]
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

### `blog delete` — 删除文章

从后台删除指定文章（已发布的文章不可删除）。

```bash
blog delete <id>
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

### `blog preview` — 本地预览

在本地将 Markdown 文件渲染为 HTML，并在浏览器中打开预览排版效果。

```bash
blog preview <file>
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

### `blog net` — GitHub 网络工具箱

内置网络诊断与 Git 优化工具，解决 GitHub 访问慢、连接不稳定导致的 `push/pull/merge` 失败问题。

**不需要代理也能提升** — `blog net config --apply` 一键优化 Git 参数即可生效。

```bash
blog net <subcommand> [options]
```

| 子命令 | 说明 |
|--------|------|
| `test` | 快速连通性检测 |
| `diagnose` | 全链路端点诊断 |
| `config` | Git 网络参数优化 |
| `proxy` | Git 代理管理 |
| `retry` | 测试重试机制 |

---

#### `blog net test` — 连通性检测

一键检测到 GitHub 的 DNS 解析 + TCP 连接延迟，快速判断网络是否正常。

```bash
blog net test
blog net test -v    # 详细模式：额外显示当前 Git 仓库信息
```

**响应示例（正常）**

```text
🔍 GitHub 连通性快速检测

  ✅ 连通正常
  ▪ 延迟: 230 ms
  ▪ 详情: ping 230ms, IP: 20.205.243.166
```

**响应示例（失败 + 改善建议）**

```text
  ❌ 连通失败
  ▪ 原因: 连通性检测失败: DNS 解析超时 (5000ms)

  💡 建议:
   1. 检查网络连接 / 确认 VPN 是否开启
   2. 运行 blog net diagnose 查看完整端点诊断
   3. blog net config --apply  → 一键优化 Git 参数 (无需代理，推荐先试)
   4. blog net proxy --apply  → 自动检测系统代理并配置
```

---

#### `blog net diagnose` — 全链路端点诊断

同时检测 6 个 GitHub 相关端点的 DNS / TCP / HTTPS 状态，按延迟升序排列，**自动推荐最佳端点**。

```bash
blog net diagnose
```

检测的端点：

| 端点 | 说明 |
|------|------|
| `HTTPS (github.com)` | GitHub 官方 |
| `SSH (github.com)` | GitHub SSH |
| `API (api.github.com)` | GitHub API |
| `hub.fastgit.xyz` | 国内镜像 |
| `github.com.cnpmjs.org` | 国内镜像 |

**响应示例**

```text
🩺 GitHub 全链路端点的诊断

  ┌─────────┬──────────────────────────────┬──────────┬────────┬────────┐
  │ 状态    │ 端点                          │ DNS      │ TCP    │ HTTPS  │
  ├─────────┼──────────────────────────────┼──────────┼────────┼────────┤
  │ ✓       │ HTTPS (github.com)           │ ✓        │ 230ms  │ 240ms  │
  │ ✓       │ SSH (github.com)             │ ✓        │ 225ms  │ —      │
  │ ✓       │ API (api.github.com)         │ ✓        │ 235ms  │ 248ms  │
  │ ✗       │ 镜像 (hub.fastgit.xyz)        │ ✗        │ ✗      │ ✗      │
  │ ✗       │ 镜像 (github.com.cnpmjs.org)  │ ✗        │ ✗      │ ✗      │
  └─────────┴──────────────────────────────┴──────────┴────────┴────────┘

  🏆 推荐: SSH (github.com) — 延迟 225ms
```

---

#### `blog net config` — Git 网络参数优化

**最核心的改善手段**。一键应用 8 项 Git 网络参数优化，无需配置代理，对慢速/不稳定网络有明显提升。

```bash
blog net config              # 预览推荐配置（不应用）
blog net config --apply      # 一键应用参数（推荐）
blog net config --show       # 查看当前 Git 网络配置
```

**`--apply` 应用的参数**

| 配置项 | 值 | 作用 |
|--------|----|------|
| `http.postBuffer` | 524288000 | POST 缓冲区 → 500MB，防止大推送中途断开 |
| `http.lowSpeedLimit` | 1000 | 低于 1KB/s 才视为低速 |
| `http.lowSpeedTime` | 600 | 持续 600 秒低速才放弃 |
| `http.version` | HTTP/1.1 | 强制 HTTP/1.1（避免 HTTP/2 在某些代理下的问题）|
| `http.keepAlive` | true | 复用 TCP 连接 |
| `http.sslVerify` | true | 保持 SSL 验证 |
| `core.preloadindex` | true | 并发索引预加载 |
| `core.compression` | 0 | 压缩级别自动 |

**响应示例**

```text
⚙️  Git 网络优化配置

  使用 --apply 应用，--show 查看当前配置

  ┌────────┬────────────────────────┬──────────────────────┐
  │ #      │ 配置项                  │ 推荐值               │
  ├────────┼────────────────────────┼──────────────────────┤
  │  1     │ http.postBuffer        │ 524288000          │
  │  2     │ http.lowSpeedLimit     │ 1000               │
  │  3     │ http.lowSpeedTime      │ 600                │
  │  4     │ http.version           │ HTTP/1.1           │
  │  5     │ http.keepAlive         │ true               │
  │  6     │ http.sslVerify         │ true               │
  │  7     │ core.preloadindex      │ true               │
  │  8     │ core.compression       │ 0                  │
  └────────┴────────────────────────┴──────────────────────┘

  说明: 以上配置对慢速/不稳定网络有明显改善
  执行 blog net config --apply 一键应用
```

---

#### `blog net proxy` — Git 代理管理

为 Git 设置/清除 HTTP 代理。（**仅在需要代理/VPN 才能访问 GitHub 时使用**）

```bash
blog net proxy                          # 查看当前代理状态
blog net proxy --apply                  # 从环境变量自动检测并设置
blog net proxy --apply http://127.0.0.1:7890  # 手动指定代理地址
blog net proxy --clear                  # 清除 Git 代理配置
```

**响应示例**

```text
📡 Git 代理状态

  系统环境变量:
    HTTP_PROXY:  http://127.0.0.1:7890
    HTTPS_PROXY: https://127.0.0.1:7890
    ALL_PROXY:   (未设置)
    来源:        环境变量

  Git 配置:
    http.proxy:  http://127.0.0.1:7890
    https.proxy: https://127.0.0.1:7890

  设置:   blog net proxy --apply http://127.0.0.1:7890
  清除:   blog net proxy --clear
```

> 支持从环境变量 `HTTP_PROXY` / `HTTPS_PROXY` / `ALL_PROXY` 自动检测代理地址。

---

#### `blog net retry` — 测试重试机制

验证指数退避重试是否正常工作。（只读测试，不会修改任何配置）

```bash
blog net retry                  # 默认 3 次重试
blog net retry --count 5        # 自定义最大重试次数
blog net retry --target https://github.com  # 自定义测试目标
```

**重试策略**

| 参数 | 值 |
|------|-----|
| 最大重试次数 | 3 次（可配置） |
| 基础延迟 | 1s |
| 退避倍率 | ×2（1s → 2s → 4s） |
| 最大延迟 | 10s |
| Jitter | 20%（随机偏移，避免惊群） |

**响应示例（失败时）**

```text
🔄 重试机制测试

  目标: https://github.com
  最大重试次数: 3

  ❌ 重试测试失败，已耗尽 3 次重试
  ▪ 最终错误: TCP 连接失败: 连接超时 (5000ms)
```

---

#### 应用场景速查

| 场景 | 推荐操作 |
|------|---------|
| `git push` 经常 `timeout` / `RPC failed` | `blog net config --apply` |
| `git clone` 很慢或中途断开 | `blog net config --apply` |
| 开了 VPN 但 Git 不走代理 | `blog net proxy --apply` |
| 不知道是否连得上 GitHub | `blog net test` |
| 想知道哪个 GitHub 端点最快 | `blog net diagnose` |

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

可通过 `blog init` 交互式修改，也可直接编辑 JSON 文件。

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

### Q: `npm link` 后找不到 `blog` 命令

```bash
# 查看 npm 全局 bin 路径
npm config get prefix
# 确保该路径在系统的 PATH 环境变量中
```

### Q: `git push` 经常报 `RPC failed` / `timeout`

```bash
blog net config --apply
```

一键优化 Git 网络参数即可改善。

### Q: 开了 VPN 但 Git 还是连不上 GitHub

```bash
blog net proxy --apply
```

从环境变量自动检测代理并配置到 Git。

### Q: 如何知道当前网络能不能连上 GitHub？

```bash
blog net test        # 快速检测
blog net diagnose    # 全链路诊断
```

---

## 项目模块

```
blog-cli/
├── bin/
│   └── blog-cli.js          # CLI 入口
├── commands/
│   ├── init.js              # blog init
│   ├── push.js              # blog push
│   ├── publish.js           # blog publish
│   ├── pull.js              # blog pull
│   ├── delete.js            # blog delete
│   ├── preview.js           # blog preview
│   └── net.js               # blog net (5 个子命令)
├── lib/
│   ├── api-client.js        # 后端 API 封装 (axios)
│   ├── markdown-parser.js   # Markdown 解析器
│   ├── git-net.js           # GitHub 网络层 (DNS/TCP/HTTPS/代理/重试)
│   └── git-wrapper.js       # Git 命令包装器 (超时/自动重试)
├── config/
│   └── default.json         # 持久化配置
└── package.json
```

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

> `lib/git-net.js` 和 `lib/git-wrapper.js` 为纯自实现（仅使用 Node.js 内置模块），无额外第三方依赖。
