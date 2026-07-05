# V2 — 通用 CMS 系统

> 目标：一套可售卖给小团队/企业的通用内容管理系统。
> 用户规模：数十~数百人。
> 原则：从 V1 扩展，不重写。

---

## 一、V2 在 V1 基础上新增的模块

```
V1（个人博客）                  V2（通用 CMS）
─────────────────              ─────────────────
blog-article     ───→  扩展为多租户能力
blog-category    ───→  支持多级分类
blog-auth        ───→  完整 RBAC 权限体系
blog-user        ───→  用户管理 + 角色管理
blog-comment     ───→  新增评论模块
blog-file        ───→  新增文件管理模块
blog-gateway     ───→  新增 API 网关
blog-frontend    ───→  完整管理后台（Vue3）
blog-site        ───→  （保留，可选）
                       blog-publish（发布引擎）
                       blog-dashboard（数据统计）
                       blog-system（系统配置）
```

## 二、新增模块详解

### 2.1 blog-comment（评论系统）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /comment | 评论列表（分页，按文章/状态筛选） |
| POST | /comment | 发表评论 |
| POST | /comment/reply | 回复评论 |
| DELETE | /comment/{id} | 删除评论 |
| PATCH | /comment/{id}/audit | 审核（通过/拒绝） |

字段：content / articleId / parentId / replyToId / status / ipAddress

### 2.2 blog-file（文件管理）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /file/upload | 单文件上传 |
| POST | /file/upload-batch | 批量上传 |
| GET | /file/{id} | 查看文件信息 |
| DELETE | /file/{id} | 删除文件 |

支持：图片/文档/附件，自动缩略图，访问 URL 鉴权

### 2.3 blog-publish（发布引擎）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /publish/{articleId} | 发布单篇文章 |
| POST | /publish/batch | 批量发布 |
| POST | /publish/full | 全量重建 |
| GET | /export/articles | 导出已发布文章 JSON |
| GET | /export/site-config | 导出站点配置 |

### 2.4 blog-dashboard（仪表盘）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /dashboard/overview | 数据概览（文章数/用户数/浏览量） |
| GET | /dashboard/trend | 趋势图（近 N 天发文/访问） |
| GET | /dashboard/hot-articles | 热门文章 TOP10 |
| GET | /dashboard/latest-comments | 最新评论 |
| GET | /dashboard/category-stats | 分类统计 |

### 2.5 blog-system（系统配置）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /system/config/{key} | 获取配置项 |
| PUT | /system/config/{key} | 更新配置项 |
| GET | /system/configs | 获取所有配置 |

配置项：站点名称、描述、友链、SEO 信息、默认设置

## 三、V2 必须改动的现有模块

### 3.1 blog-auth → 完整 RBAC

| 表 | 说明 |
|---|---|
| t_user | V1 已有 |
| t_role | 已有 |
| t_menu | 已有 |
| t_user_role | 已有 |
| t_role_menu | 已有 |

需要补：
- `POST /auth/user/role` — 分配角色
- `POST /auth/role` — CRUD 角色
- `POST /auth/menu` — CRUD 菜单权限

### 3.2 blog-gateway（新增）

- 统一入口：`http://localhost:8080/api/**`
- 路由转发到各个微服务
- 全局 JWT 鉴权过滤器
- 请求日志记录
- 限流（令牌桶）

### 3.3 blog-article 扩展

- 草稿自动保存（定时同步）
- 文章版本历史
- 定时发布
- 密码保护文章
- Markdown / 富文本双模式

## 四、V2 前端（Vue3 管理后台）

| 页面 | 说明 |
|------|------|
| 登录/注册 | JWT + RBAC |
| 仪表盘 | 数据卡片 + 趋势图 |
| 文章管理 | 列表/编辑/发布/批量操作 |
| 分类管理 | 树形拖拽排序 |
| 评论管理 | 列表/审核/回复 |
| 用户管理 | 列表/角色分配/禁用 |
| 文件管理 | 上传/预览/删除 |
| 系统配置 | 站点设置 |
| 操作日志 | 审计记录 |

## 五、V2 技术栈扩展

| 组件 | 用途 |
|------|------|
| Redis | 缓存热点文章、token 黑名单、会话管理 |
| RabbitMQ / RocketMQ | 异步通知（发布触发构建、导入导出） |
| ELK (EFK) | 日志采集与查询 |
| Prometheus + Grafana | 服务监控 |
| Knife4j / Swagger | 接口文档（已完成） |
| Caffeine | 本地缓存（token 校验）|

## 六、V2 不做

| 功能 | 原因 |
|------|------|
| 推荐算法 | V3 的事 |
| Feed 流 | V3 的事 |
| 问答系统 | V3 的事 |
| IM 即时通讯 | 不是 CMS 的范畴 |
| 多语言国际化 | 后续可选 |
