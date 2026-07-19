# V5 — 智能化与平台开放

> 目标：在 V4 内容生态基础上，引入 AI 辅助能力和开放生态。
> 定位：从"能运营内容"到"更智能、更开放"。
> 原则：在 V4 功能基础上追加，不破坏已有模块。

---

## 一、V5 在 V4 基础上新增的能力

```
V4（内容生态运营）                   V5（智能化与开放平台）
─────────────────                   ─────────────────
专栏/专题             ───→    AI 智能标签 + 自动分类 + 内容摘要
运营数据平台           ───→    AI 辅助写作 + 智能配图 + 标题优化
内容迁移              ───→    多平台一键分发 + 社交同步
自定义页面            ───→    前端主题/模板市场
                                 ── 新增模块 ──
                                 AI 辅助工具（标签/摘要/SEO）
                                 AI 配图与封面生成
                                 AI 写作助手
                                 多平台内容分发
                                 社交账号绑定与同步
                                 第三方登录（OAuth）
                                 开放 API 与 Webhook
                                 模板/主题市场
                                 SEO 增强工具
                                 高级缓存与 CDN
                                 数据备份与还原
                                 插件系统
```

---

## 二、新增模块详解

### 2.1 AI 智能标签与摘要 (P0)

利用 AI 自动为文章生成标签、分类建议和内容摘要。

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| POST | /ai/tag | AI 智能打标签 | P0 |
| POST | /ai/summary | AI 生成文章摘要 | P0 |
| POST | /ai/category | AI 推荐分类 | P0 |
| POST | /ai/keywords | AI 提取关键词 | P0 |
| POST | /ai/description | AI 生成 SEO 描述 | P1 |
| POST | /ai/content/optimize | AI 内容优化建议 | P2 |
| GET | /ai/quota | AI 调用额度查询 | P1 |

**业务规则：**
- 每次保存文章时自动触发标签/摘要生成
- 支持手动重新生成
- 用户可编辑 AI 生成的结果
- 按用户/角色设置每日调用额度

### 2.2 AI 配图与封面生成 (P1)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /ai/image/cover | AI 生成文章封面图 |
| POST | /ai/image/illustration | AI 生成段落配图（基于内容描述） |
| POST | /ai/image/search | AI 推荐免费图库图片（Unsplash/Pexels） |
| POST | /ai/image/alt-text | AI 生成图片 Alt 文本 |

**业务规则：**
- 封面图：基于标题+关键词生成，支持选择风格
- 配图：选中段落后调用生成，可自定义 prompt
- 图库搜索：集成 Unsplash/Pexels API
- 生成的图片自动上传到文件管理

### 2.3 AI 写作助手 (P2)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /ai/writer/title | 基于内容生成 5 个候选标题 |
| POST | /ai/writer/expand | 扩写提纲/段落 |
| POST | /ai/writer/rewrite | 改写/润色（调整语气/风格） |
| POST | /ai/writer/continue | 续写 |
| POST | /ai/writer/translate | 翻译（中英互译） |

**集成方式：** 前端编辑器插件（工具栏按钮），流式 SSE 返回

### 2.4 多平台内容分发 (P1)

将文章一键分发到多个外部平台。

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /distribute/platform | 绑定外部平台账号 |
| GET | /distribute/platforms | 已绑定的平台列表 |
| DELETE | /distribute/platform/{id} | 解绑平台 |
| POST | /distribute/publish | 发布文章到指定平台 |
| POST | /distribute/batch | 批量分发多篇文章 |
| GET | /distribute/history | 分发记录 |
| GET | /distribute/status/{taskId} | 分发任务状态 |

**支持平台（首期）：**

| 平台 | 支持方式 | 优先级 |
|------|----------|--------|
| 微信公众号 | API 发布（素材→草稿→发布） | P0 |
| 知乎专栏 | API 发布文章 | P0 |
| CSDN | API/模拟发布 | P1 |
| 掘金 | API 发布 | P1 |
| Medium | API 发布 | P1 |
| 开源中国 | API 发布 | P2 |
| SegmentFault | API 发布 | P2 |

**业务规则：**
- 内容适配：根据不同平台格式自动调整（Markdown 转换）
- 定时发布：可设置各平台不同发布时间
- 分发状态追踪：成功/失败/部分成功
- 失败重试机制

### 2.5 社交账号绑定与同步 (P1)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /social/bind | 绑定社交账号 |
| DELETE | /social/{id}/unbind | 解绑 |
| GET | /social/accounts | 已绑定的社交账号列表 |
| POST | /social/sync/{platform} | 手动同步（从社交平台拉取内容/互动） |
| POST | /social/auto-sync | 配置自动同步规则 |
| GET | /social/sync-status | 同步状态概览 |

**支持平台：** GitHub、Twitter/X、微博、知乎

### 2.6 第三方登录（OAuth）(P0)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /oauth/{provider}/authorize | 跳转第三方授权页 |
| GET | /oauth/{provider}/callback | OAuth 回调处理 |
| POST | /oauth/bind | 将第三方账号绑定到已有账号 |
| GET | /oauth/providers | 支持的第三方登录列表 |

**支持平台：**

| 平台 | 优先级 |
|------|--------|
| GitHub | P0 |
| Google | P0 |
| 微信扫码 | P1 |
| QQ | P1 |
| Gitee | P1 |
| 微博 | P2 |

**业务规则：**
- 首次 OAuth 登录自动创建账号（用户名取自第三方）
- 已有账号可绑定多个第三方账号
- 绑定后可用任一第三方账号登录
- OAuth 回调自动发放 JWT Token

### 2.7 开放 API 与 Webhook (P1)

对外暴露标准 REST API，支持第三方集成。

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| POST | /open/api-key | 创建 API Key | P0 |
| GET | /open/api-keys | API Key 列表 | P0 |
| DELETE | /open/api-key/{id} | 吊销 API Key | P0 |
| POST | /open/webhook | 创建 Webhook | P1 |
| GET | /open/webhooks | Webhook 列表 | P1 |
| PUT | /open/webhook/{id} | 编辑 Webhook | P1 |
| DELETE | /open/webhook/{id} | 删除 Webhook | P1 |
| GET | /open/webhook/{id}/logs | Webhook 调用日志 | P2 |
| POST | /open/webhook/{id}/test | 测试 Webhook | P2 |

**开放 API 列表：**

| API 分类 | 端点 | 说明 |
|----------|------|------|
| 文章 | `GET /open/api/v1/articles` | 公开文章列表 |
| 文章 | `GET /open/api/v1/articles/{id}` | 文章详情 |
| 分类 | `GET /open/api/v1/categories` | 分类树 |
| 标签 | `GET /open/api/v1/tags` | 标签列表 |
| 站点 | `GET /open/api/v1/site/info` | 站点信息 |

**Webhook 事件：**

| 事件 | 触发时机 | Payload 内容 |
|------|----------|-------------|
| `article.published` | 文章发布 | 文章 ID、标题、URL |
| `article.updated` | 文章更新 | 文章 ID、变更摘要 |
| `article.deleted` | 文章删除 | 文章 ID |
| `comment.created` | 新评论 | 评论 ID、文章 ID、内容 |
| `site.build` | 触发静态站构建 | 站点 ID |

**安全机制：**
- API Key 权限范围（只读/读写）
- Webhook 签名验证（HMAC-SHA256）
- 调用频率限制（默认 1000 次/小时）
- IP 白名单可选

### 2.8 模板/主题市场 (P2)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /theme/market | 主题市场列表 |
| GET | /theme/market/{id} | 主题详情（预览图/功能说明） |
| POST | /theme/install | 安装主题 |
| POST | /theme/switch | 切换当前主题 |
| GET | /theme/installed | 已安装主题列表 |
| DELETE | /theme/{id}/uninstall | 卸载主题 |
| PUT | /theme/{id}/customize | 自定义主题配置（颜色/布局） |
| POST | /theme/upload | 上传自定义主题 |
| PUT | /theme/current/config | 当前主题配置项 |

**主题结构：**
```
my-theme/
├── theme.json          # 主题元信息（名称/版本/作者/截图）
├── preview.png         # 预览图
├── templates/          # 模板文件
│   ├── index.html
│   ├── article.html
│   ├── archive.html
│   └── ...
├── assets/             # 静态资源
│   ├── css/
│   ├── js/
│   └── images/
└── config/             # 可配置项定义
    └── schema.json
```

### 2.9 SEO 增强工具 (P1)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /seo/analyze/{articleId} | AI 分析单篇文章 SEO 质量 |
| POST | /seo/sitemap/generate | 生成/更新 Sitemap（XML） |
| GET | /seo/sitemap | 获取 Sitemap URL |
| POST | /seo/robots/generate | 生成 robots.txt |
| GET | /seo/robots | 查看 robots.txt 内容 |
| PUT | /seo/robots | 自定义 robots.txt |
| POST | /seo/breadcrumb | 自动生成面包屑导航数据 |
| GET | /seo/stats | SEO 状态概览（收录/索引/错误） |

**SEO 分析指标：**
- 标题长度与关键词密度
- 描述完整度
- 标题标签（H1/H2/H3）结构
- Alt 文本覆盖率
- 内链/外链数量
- 页面加载速度建议
- 可读性评分（Flesch 指数）

### 2.10 高级缓存与 CDN (P1)

| 接口 | 说明 |
|------|------|
| GET /admin/cache/status | 缓存状态概览 |
| POST /admin/cache/clear | 清除指定缓存（文章/分类/全站） |
| PUT /admin/cache/config | 缓存策略配置 |
| PUT /admin/cdn/config | CDN 配置（域名/密钥/刷新） |
| POST /admin/cdn/refresh | 刷新 CDN 缓存（按 URL/目录/全站） |

**缓存策略配置：**
```
文章详情页: 缓存 30 分钟（有 Redis 版本号，发布时失效）
分类树: 缓存 1 小时（变更时失效）
站点配置: 缓存 2 小时（变更时失效）
热门文章: 缓存 10 分钟
```

### 2.11 数据备份与还原 (P1)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /backup/create | 创建手动备份 |
| GET | /backup/list | 备份列表 |
| POST | /backup/schedule | 设置自动备份计划 |
| GET | /backup/config | 备份配置 |
| POST | /backup/restore/{id} | 从备份还原 |
| DELETE | /backup/{id} | 删除备份 |
| GET | /backup/download/{id} | 下载备份文件 |

**备份内容：**
- 数据库全量 SQL
- 上传文件（可选）
- 主题/模板文件（可选）
- 站点配置（JSON）

**自动备份策略：**
- 每日自动备份，保留最近 7 天
- 每周全量备份，保留最近 4 周
- 每月归档备份，保留最近 6 个月

### 2.12 插件系统 (P2)

轻量级插件框架，允许社区扩展功能。

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /plugin/market | 插件市场列表 |
| POST | /plugin/install | 安装插件 |
| POST | /plugin/uninstall | 卸载插件 |
| POST | /plugin/enable | 启用插件 |
| POST | /plugin/disable | 禁用插件 |
| GET | /plugin/{id}/config | 插件配置页面 |
| PUT | /plugin/{id}/config | 更新插件配置 |

**插件能力范围：**
- 文章处理管道（内容格式化/自动添加脚注）
- 短代码（shortcode）扩展（视频嵌入/图表/代码运行）
- 第三方服务集成（Google Analytics / 百度统计 / 评论系统）
- 内容导入/导出格式扩展

---

## 三、V5 技术栈扩展

| 组件 | 用途 | 优先级 |
|------|------|--------|
| OpenAI / DeepSeek API | AI 标签/摘要/配图 | P0 |
| Unsplash / Pexels API | 免费图库搜索 | P1 |
| 各平台发布 API | 多平台分发 | P1 |
| Spring Security OAuth2 | 第三方登录 | P0 |
| API Key + HMAC | 开放 API 鉴权 | P1 |
| Redis (缓存增强) | 全站缓存策略 | P1 |
| Quartz / XXL-JOB | 定时备份/SEO 更新 | P1 |
| PF4J / SPI | 插件系统框架 | P2 |

---

## 四、V5 不做的

| 功能 | 原因 |
|------|------|
| 自研大模型 | 调用商用 API 已足够 |
| 移动端 App | 先做 Web 端，API 已预留 |
| 视频直播 | 非博客 CMS 范畴 |
| 知识付费 | 管理后台不需要 |
| 即时通讯 IM | 非博客场景 |
| 区块链/NFT | 与实际需求脱节 |
| 企业版 SaaS | 非个人/小团队 CMS 定位 |

---

## 五、V1 → V5 完整演进全景

```
V1（个人博客）    →   V2（通用 CMS）    →   V3（UGC 社区）    →   V4（内容生态）    →   V5（智能开放）
 1-2 月              3-4 月              6-8 月               7-9 月              8-10 月
 ───────             ───────             ───────              ───────             ───────
 核心文章系统        评论/文件/网关       问答/Feed/搜索       专栏/回收站/分发     AI 辅助/OAuth/插件
 CLI + 静态站       Vue3 管理后台        通知/排行榜/审核      运营数据/互动       开放 API/Webhook
                    RBAC 权限           推荐/用户等级         邮件订阅/多站点      CDN/备份/主题市场
                    仪表盘/日志                              内容迁移/版权         SEO 工具
```

**每个版本追加的新模块数量：**
- V1: 6 个核心模块（文章/分类/认证/用户/CLI/静态站）
- V2: 6 个新模块（评论/文件/发布引擎/仪表盘/系统配置/网关） + 4 个改造
- V3: 7 个新模块（Feed/问答/互动通知/排行榜/审核/用户等级/搜索）
- V4: 10 个新模块（专栏/Newsletter/版本管理/回收站/自定义页面/运营数据/迁移/互动组件/多站点/水印）
- V5: 12 个新模块（AI 标签摘要/AI 配图/AI 写作/多平台分发/社交同步/OAuth/开放 API/Webhook/主题市场/SEO/缓存 CDN/备份/插件）
