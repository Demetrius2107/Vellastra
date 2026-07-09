# V4 — 内容生态与运营增强

> 目标：在 V3 社区互动基础上，增强内容组织能力、运营工具和数据洞察。
> 定位：从"能发文章"到"能运营好内容"。
> 原则：在 V3 功能基础上追加，不破坏已有模块。

---

## 一、V4 在 V3 基础上新增的能力

```
V3（UGC 社区）                      V4（内容生态运营）
─────────────────                   ─────────────────
文章 + 问答            ───→   专栏/专题系统（系列内容组织）
简单分类标签            ───→   内容关联推荐 + 智能聚合
基础统计               ───→   运营数据平台 + 用户行为分析
评论互动               ───→   投票/问卷/互动组件
文件上传               ───→   内容搬运/迁移工具
                                ── 新增模块 ──
                                专栏/专题系统
                                Newsletter 邮件订阅
                                文章版本管理
                                内容回收站
                                自定义页面
                                运营数据平台
                                内容迁移工具
                                互动组件（投票/问卷）
                                多站点管理
                                内容水印与版权保护
```

---

## 二、新增模块详解

### 2.1 专栏/专题系统 (P0)

将文章组织成系列内容，支持专题聚合页。

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| POST | /column | 创建专栏 | P0 |
| PUT | /column/{id} | 编辑专栏 | P0 |
| GET | /column/{id} | 专栏详情（含文章列表） | P0 |
| GET | /column | 专栏列表（分页/按分类/按热度） | P0 |
| DELETE | /column/{id} | 删除专栏 | P1 |
| POST | /column/{id}/article | 向专栏添加文章 | P0 |
| DELETE | /column/{id}/article/{articleId} | 从专栏移除文章 | P1 |
| PUT | /column/{id}/article/sort | 排序调整 | P1 |
| GET | /column/{id}/articles | 专栏内文章列表（有序） | P0 |
| PUT | /column/{id}/cover | 更新专栏封面 | P2 |

**数据库设计：**

```
t_column
├── id / title / description / cover_url
├── author_id / status (DRAFT / PUBLISHED)
├── article_count / view_count
├── sort_order
└── create_time / update_time

t_column_article
├── id / column_id / article_id
├── sort_order
└── create_time
```

### 2.2 Newsletter 邮件订阅 (P1)

支持用户订阅 Newsletter，定期发送精选内容推送。

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| POST | /newsletter/subscribe | 订阅 Newsletter | P0 |
| POST | /newsletter/unsubscribe | 取消订阅 | P0 |
| GET | /newsletter/status | 查看订阅状态 | P0 |
| POST | /newsletter/send | 手动发送一期 | P1 |
| POST | /newsletter/schedule | 设置定时发送计划 | P1 |
| GET | /newsletter/history | 历史发送记录 | P1 |
| GET | /newsletter/stats | 订阅统计（打开率/退订率） | P2 |
| PUT | /newsletter/template | 编辑邮件模板 | P2 |

**业务规则：**
- 支持每日/每周/自定义频率
- AI 自动摘要本周热门文章
- 支持自定义邮件内容（手动编辑）
- 退订链接自动附加在邮件底部
- 发送前自动预览

**数据库设计：**

```
t_newsletter_subscriber
├── id / email / user_id (可选关联登录用户)
├── frequency (DAILY / WEEKLY / MONTHLY)
├── subscribe_time / unsubscribe_time
├── status (ACTIVE / UNSUBSCRIBED / BOUNCED)
└── token (退订令牌)

t_newsletter_send_log
├── id / subject / content
├── recipient_count / open_count / click_count
├── send_time / status (PENDING / SENDING / SUCCEEDED / FAILED)
└── create_time
```

### 2.3 文章版本管理 (P1)

每次保存自动生成版本快照，支持版本对比和回滚。

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /article/{id}/versions | 版本列表 |
| GET | /article/{id}/versions/{versionId} | 查看某版本内容 |
| POST | /article/{id}/versions | 手动创建版本快照 |
| POST | /article/{id}/rollback/{versionId} | 回滚到指定版本 |
| GET | /article/{id}/versions/diff?from=&to= | 版本对比（差异高亮） |
| DELETE | /article/{id}/versions/{versionId} | 删除版本（保留最近 5 个） |

**业务规则：**
- 自动保存：编辑时每 5 分钟自动创建版本
- 版本保留：默认保留最近 20 个版本，超出自动清理
- 版本对比：左侧旧版/右侧新版，差异行高亮
- 回滚操作：回滚时自动创建当前版本的备份版本

### 2.4 内容回收站 (P1)

软删除的内容进入回收站，支持恢复和彻底删除。

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /recycle-bin | 回收站列表（含文章/评论/分类等） |
| POST | /recycle-bin/{id}/restore | 恢复内容 |
| DELETE | /recycle-bin/{id} | 彻底删除 |
| DELETE | /recycle-bin/empty | 清空回收站 |
| GET | /recycle-bin/settings | 回收站设置（自动清理天数） |
| PUT | /recycle-bin/settings | 更新设置 |

**业务规则：**
- 文章/评论/分类等删除后进入回收站保留 30 天
- 到期自动彻底删除
- 恢复时还原关联关系（如文章的分类/标签）
- 彻底删除不可恢复

### 2.5 自定义页面 (P1)

支持创建独立页面（关于/友链/导航/项目页等）。

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /page | 页面列表 |
| POST | /page | 创建页面 |
| GET | /page/{id} | 页面详情 |
| PUT | /page/{id} | 编辑页面 |
| DELETE | /page/{id} | 删除页面 |

**业务规则：**
- 支持 Markdown 编辑
- 支持自定义 URL slug（如 /about /links）
- 可设置是否显示在导航栏
- 可选择页面模板（空白/带侧边栏/全宽）

### 2.6 运营数据平台 (P1)

在 V2 dashboard 基础上扩展为完整的数据分析平台。

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| GET | /analytics/overview | 运营总览（日/周/月同比） | P0 |
| GET | /analytics/traffic | 流量分析（PV/UV/IP/来源） | P0 |
| GET | /analytics/content | 内容分析（阅读排名/转化率） | P0 |
| GET | /analytics/user | 用户分析（新增/活跃/留存） | P1 |
| GET | /analytics/retention | 用户留存分析（次日/7日/30日） | P1 |
| GET | /analytics/conversion | 转化漏斗（浏览→评论→关注） | P1 |
| GET | /analytics/export | 导出数据报表（CSV/Excel） | P2 |
| GET | /analytics/realtime | 实时数据（当前在线/实时PV） | P2 |

**数据来源：**
- 前端埋点（页面浏览/点击/停留时长）
- 服务端日志（API 调用/响应时间）
- 数据库聚合（内容/用户统计数据）

### 2.7 内容迁移工具 (P2)

支持从其他平台批量导入/同步内容。

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /migration/import | 导入内容（支持 Markdown/HTML/Word） |
| POST | /migration/import/url | 从 URL 抓取并导入 |
| GET | /migration/tasks | 导入任务列表 |
| GET | /migration/tasks/{id} | 导入任务详情（进度/结果） |
| POST | /migration/platform | 绑定第三方平台（CSDN/掘金/知乎等） |
| POST | /migration/sync | 手动触发从绑定平台同步 |
| GET | /migration/export | 导出全部内容（JSON/Markdown 压缩包） |

**支持导入格式：**
- Markdown 文件批量导入（解析 frontmatter）
- 从 URL 抓取（提取正文 + 元信息）
- Word 文档（`.docx` 转 Markdown）
- 其他博客平台的导出文件

### 2.8 互动组件 (P2)

在文章/问答中嵌入投票、问卷等互动元素。

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /interactive/poll | 创建投票 |
| GET | /interactive/poll/{id} | 投票详情 + 结果 |
| POST | /interactive/poll/{id}/vote | 参与投票 |
| POST | /interactive/quiz | 创建问卷 |
| POST | /interactive/quiz/{id}/submit | 提交问卷 |
| GET | /interactive/quiz/{id}/results | 问卷统计结果 |

**投票类型：**
- 单选/多选
- 限时投票
- 匿名/实名投票
- 结果可见时间设置（投票后/结束后）

### 2.9 多站点管理 (P2)

一个后台管理多个独立博客站点。

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /site | 创建子站点 |
| GET | /site | 站点列表 |
| GET | /site/{id} | 站点详情 |
| PUT | /site/{id} | 编辑站点配置（域名/主题/SEO） |
| DELETE | /site/{id} | 删除站点 |
| PUT | /site/{id}/domain | 绑定自定义域名 |

**业务规则：**
- 每个站点独立域名/子域名
- 站点间内容隔离（文章/分类/用户独立）
- 支持站点级角色权限
- 主站点可查看所有子站点概览数据

### 2.10 内容水印与版权保护 (P2)

| 方法 | 路径 | 说明 |
|------|------|------|
| PUT | /watermark/config | 配置水印（文字/图片/位置/透明度） |
| GET | /watermark/config | 查看水印配置 |
| POST | /copyright/register | 登记原创声明 |
| GET | /copyright/articles | 原创声明文章列表 |

---

## 三、V4 数据库变更

### 新增表

```
用户域:  t_newsletter_subscriber, t_newsletter_send_log
内容域:  t_column, t_column_article, t_article_version, t_page
运营域:  t_analytics_event, t_migration_task
互动域:  t_poll, t_poll_option, t_poll_vote, t_quiz, t_quiz_question, t_quiz_answer
站点域:  t_site, t_site_domain
版权域:  t_copyright_record, t_watermark_config
```

### 现有表变更

```sql
-- t_article 扩展字段
ALTER TABLE t_article ADD COLUMN version_count INT DEFAULT 0;
ALTER TABLE t_article ADD COLUMN column_count INT DEFAULT 0;
ALTER TABLE t_article ADD COLUMN is_original TINYINT(1) DEFAULT 1;
ALTER TABLE t_article ADD COLUMN deleted_at DATETIME NULL; -- 回收站时间

-- t_category 扩展
ALTER TABLE t_category ADD COLUMN site_id BIGINT NULL;
ALTER TABLE t_category ADD COLUMN cover_url VARCHAR(500) NULL;
```

---

## 四、V4 不做的

| 功能 | 原因 |
|------|------|
| 知识付费/专栏售卖 | 非博客管理后台的范畴 |
| AI 写作 | V5 再做 |
| 即时通讯 | 不是 CMS 的功能 |
| 多语言国际化 | V5 再做 |
| 钉钉/飞书集成 | 非通用需求 |
| 视频上传与转码 | 博客以图文为主 |
