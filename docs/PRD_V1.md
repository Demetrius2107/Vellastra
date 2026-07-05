# V1 — 个人博客系统

> 目标：自己写文章、存到后台、发布到 GitHub Pages 静态站。
> 用户规模：你一个人。
> 原则：能用就行。

---

## 一、系统架构

```
本地 VS Code 写 Markdown
        │ blog-cli push
        ▼
Spring Boot 后台 (REST API)
        │ 存储 + 发布
        ▼
MySQL (文章 / 分类 / 用户)
        │ 触发构建
        ▼
GitHub Actions → VitePress 构建 → GitHub Pages
```

## 二、模块清单

| 模块 | 状态 | 说明 |
|------|------|------|
| blog-article | ✅ 已完成 | CRUD + 发布/下架/置顶/浏览/点赞 |
| blog-category | ✅ 已完成 | 树形 CRUD |
| blog-auth | ✅ 已完成 | login / register / logout / refresh |
| blog-user | ✅ 基础完成 | 查看 / 更新资料 |
| blog-comment | ❌ V1 不需要 | V1 是静态站，没有评论区 |
| blog-gateway | ❌ V1 不需要 | 单机直连即可 |
| blog-frontend | ❌ 待开发 | Vue3 管理后台 |
| blog-cli | ❌ 待开发 | Node.js 命令行推送工具 |
| blog-site | ❌ 待开发 | VitePress 静态博客站 |

## 三、接口清单（只列 V1 需要）

### 3.1 文章管理

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| POST | /article | 创建文章 | P0 |
| PUT | /article/{id} | 更新文章 | P0 |
| DELETE | /article/{id} | 删除文章 | P0 |
| GET | /article/{id} | 查看文章 | P0 |
| GET | /article | 文章列表(分页+搜索) | P0 |
| PATCH | /article/{id}/publish | 发布(草稿→已发布) | P0 |
| PATCH | /article/{id}/withdraw | 撤回(已发布→下架) | P1 |
| PATCH | /article/{id}/top | 置顶/取消 | P1 |
| POST | /article/{id}/view | 浏览计数 | P1 |
| POST | /article/{id}/like | 点赞/取消 | P1 |
| GET | /article/latest | 最新文章 | P1 |
| POST | /article/batch | 批量操作 | P2 |

### 3.2 分类管理

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| GET | /category/tree | 分类树 | P0 |
| GET | /category/{id} | 查看分类 | P0 |
| POST | /category | 新增分类 | P0 |
| PUT | /category/{id} | 更新分类 | P0 |
| DELETE | /category/{id} | 删除分类 | P0 |

### 3.3 认证管理

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| POST | /auth/login | 登录 | P0 |
| POST | /auth/register | 注册 | P0 |
| POST | /auth/logout | 登出 | P0 |
| POST | /auth/refresh | 刷新 token | P0 |

### 3.4 用户管理

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| GET | /user/{id} | 查看用户 | P0 |
| PUT | /user/{id} | 更新资料 | P1 |
| GET | /user/info | 当前用户 | P0 |

## 四、V1 新增项目

### 4.1 blog-cli（Node.js）

```bash
blog-cli init                # 初始化配置（API地址/token）
blog-cli push hello.md       # 推送单篇文章
blog-cli push ./posts/       # 批量推送
blog-cli publish <id>        # 触发发布
blog-cli pull <id>           # 从后台拉取文章到本地
blog-cli preview             # 本地预览效果
```

**核心流程：**
1. 解析 Markdown 文件头（frontmatter: title/date/tags/category）
2. 扫描正文中的本地图片路径，自动上传到后台
3. 调用 POST /article 创建/更新文章
4. 返回文章 ID

### 4.2 blog-site（VitePress）

- 独立的 Git 仓库
- 构建时从后台 API 拉取已发布的文章
- 生成纯静态 HTML
- GitHub Actions 自动部署到 GitHub Pages

### 4.3 GitHub Actions Pipeline

```yaml
触发方式: 后台点击"发布" → API 触发 / CLI publish
构建: VitePress 构建
部署: push 到 gh-pages 分支
域名: username.github.io 或自定义域名
```

## 五、V1 不做的

| 功能 | 原因 |
|------|------|
| 评论系统 | 静态站无后端，评论用第三方（Giscus） |
| RBAC 权限 | 只有你一个人，不需要角色管理 |
| 文件管理模块 | 图片直接上传到后台 t_file 表 |
| 多语言 | 用不到 |
| Feed 推荐流 | V3 的事 |
| 数据监控 | 个人博客看日志就够了 |
| 网关 | 单机直连 |

## 六、V1 开发路线

```
Phase 1（当前）: 后台 API 全部完成
     ├── blog-article ✅
     ├── blog-category ✅
     └── blog-auth ✅

Phase 2（下一步）: CLI + 静态站
     ├── blog-cli push 命令
     ├── VitePress 项目初始化
     └── GitHub Actions 配置

Phase 3（最后）: Vue3 管理后台
     ├── 文章列表/编辑页面
     ├── 分类管理页面
     └── 发布按钮集成
```
