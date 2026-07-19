# Vellastra 内容系统（Vellastra Content System）完整设计文档

> **文档版本**: v2.0 合并版  
> **文档定位**: 综合 PRD V1~V6 + 豆包全量设计文档，形成三阶段渐进式最终落地版  
> **核心原则**: 渐进式无重构，每一阶段完全复用上一阶段代码与表结构，仅做拓展与升级  
> **基础环境**: JDK 17 LTS + MySQL 5.7 / 8.0 + Spring Boot 3.2.x  
> **文档日期**: 2026-07-10

---

## 文档结构

```
第一部分  项目概述与三阶段演进
第二部分  阶段一：个人博客（单体架构）
第三部分  阶段二：CMS + 内容生态（模块化单体）
第四部分  阶段三：UGC 知识社区（微服务架构）
第五部分  CLI 工具设计
第六部分  完整数据库设计汇总
第七部分  V6 展望：智能 Agent 协作（可选）
```

---

## 第一部分：项目概述与三阶段演进

### 1.1 项目定位

Vellastra 内容系统是一套**业务与架构同步演进**的后端实战项目，以「个人博客 → CMS 内容管理 → 全场景 UGC 知识社区」的业务迭代为载体，同步完成「单体架构 → 模块化单体 → 微服务分布式架构」的技术升级。

**命名含义**: Vellastra = *vellus*（覆盖万物的毯子）+ *astra*（群星），取"星辰如毡毯覆盖原野"之意，对应杜甫《旅夜书怀》"星垂平野阔，月涌大江流"的意境。

### 1.2 三阶段演进路线

| 阶段 | 业务形态 | 架构模式 | 核心成长目标 | 对应 PRD |
|------|----------|----------|-------------|----------|
| **阶段一** | 个人博客 | Spring Boot 单体 | 掌握后端基础、RBAC 权限、缓存、文件存储 | V1 |
| **阶段二** | 多角色 CMS + 轻社区 | 模块化单体 + 中间件 | 掌握模块化拆分、MQ、ES、读写分离、缓存一致性 | V2~V4 |
| **阶段三** | 类知乎全场景 UGC 社区 | Spring Cloud 微服务 | 掌握分布式事务、分库分表、服务治理、推荐/广告系统 | V5~V6 展望 |

### 1.3 三阶段技术栈选型

#### 阶段一：单体博客

| 分类 | 技术选型 | 版本 | 说明 |
|------|----------|------|------|
| 核心框架 | Spring Boot | 3.2.x | 适配 JDK 17 |
| ORM | MyBatis-Plus | 3.5.5+ | 简化单表 CRUD |
| 数据库 | MySQL | 5.7 / 8.0 | 核心存储 |
| 连接池 | HikariCP | 默认 | 性能最优 |
| 缓存 | Redis | 7.0.x 单实例 | 缓存热点数据 + Token |
| 对象存储 | MinIO / 本地存储 | — | 文件/图片存储 |
| 接口文档 | Knife4j | 4.4.x | Swagger 增强版 |
| 权限令牌 | JJWT | 0.12.x | JWT 生成与校验 |
| 工具类 | Hutool | 5.8.x | 通用工具 |
| 参数校验 | Spring Validation | — | 原生校验 |

#### 阶段二：CMS + 轻社区（新增中间件）

| 分类 | 技术选型 | 说明 |
|------|----------|------|
| 读写分离 | ShardingSphere-JDBC 5.4.x | 轻量客户端分片 |
| 多级缓存 | Caffeine 3.1.x + Redis | 本地 + 远程二级缓存 |
| 消息队列 | RabbitMQ 3.12.x | 异步解耦、削峰填谷 |
| 全文检索 | Elasticsearch 7.17.x | 全站搜索 |
| 定时任务 | XXL-Job 2.4.x | 分布式定时任务 |
| 监控 | Actuator + Prometheus + Grafana | 可观测性入门 |

#### 阶段三：UGC 微服务社区（新增分布式组件）

| 分类 | 技术选型 | 说明 |
|------|----------|------|
| 微服务框架 | Spring Cloud Alibaba 2023.0.1.0 | 注册配置中心 Nacos |
| 网关 | Spring Cloud Gateway | 路由 + 鉴权 + 限流 |
| 熔断限流 | Sentinel 1.8.7 | 接口限流 + 熔断降级 |
| 分布式事务 | Seata 1.7.1 | AT / TCC / SAGA |
| 分布式锁 | Redisson 3.27.x | 可重入锁 + 看门狗 |
| 分布式 ID | 雪花算法 + 号段模式 | 全局唯一 ID |
| 分库分表 | ShardingSphere-JDBC | 垂直分库 + 水平分表 |
| 消息队列升级 | RocketMQ 4.9.x | 事务消息 + 海量堆积 |
| 链路追踪 | SkyWalking 9.7.x | 全链路追踪 |
| 推荐系统 | Kafka + Flink + Spark | 实时 + 离线计算 |

---

## 第二部分：阶段一 — 个人博客（单体架构）

> 目标：自己写文章、存到后台、发布到 GitHub Pages 静态站。  
> 用户规模：你一个人（后续可扩展）。  
> 架构：Spring Boot 单体，所有模块打成一个 Jar 包运行。

### 2.1 模块划分

```
vellastra-blog/                    ← 根项目（单体）
├── vellastra-common               ← 公共模块（异常、响应、工具）
├── vellastra-auth                 ← 认证鉴权（登录/注册/JWT/RBAC）
├── vellastra-user                 ← 用户管理（CRUD/角色分配）
├── vellastra-article              ← 文章管理（CRUD/状态机/标签）
├── vellastra-category             ← 分类管理（树形 CRUD）
├── vellastra-file                 ← 文件管理（上传/存储/删除）
└── vellastra-bootstrap            ← 启动器（聚合所有模块，一个 Spring Boot 入口）
```

### 2.2 数据库设计（阶段一）

#### 2.2.1 sys_user（用户表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| username | varchar(32) | 是 | 用户名，唯一索引 |
| password | varchar(128) | 是 | BCrypt 加密密码 |
| nickname | varchar(32) | 否 | 昵称 |
| avatar | varchar(255) | 否 | 头像 URL |
| email | varchar(64) | 否 | 邮箱 |
| phone | varchar(16) | 否 | 手机号 |
| role | tinyint | 是 | 角色：1=超级管理员 2=博主 3=访客 |
| status | tinyint | 是 | 状态：1=正常 0=禁用 |
| last_login_time | datetime | 否 | 最后登录时间 |
| last_login_ip | varchar(32) | 否 | 最后登录 IP |
| is_deleted | tinyint | 是 | 逻辑删除：0=否 1=是 |
| create_time | datetime | 是 | 创建时间 |
| update_time | datetime | 是 | 更新时间 |

#### 2.2.2 blog_article（文章表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| title | varchar(128) | 是 | 文章标题 |
| summary | varchar(255) | 否 | 摘要 |
| content | longtext | 是 | Markdown 正文 |
| content_html | longtext | 是 | 渲染后 HTML |
| cover_image | varchar(255) | 否 | 封面图 URL |
| category_id | bigint | 否 | 分类 ID |
| status | tinyint | 是 | 0=草稿 1=已发布 2=下架 |
| is_top | tinyint | 是 | 是否置顶：0=否 1=是 |
| view_count | int | 是 | 浏览量，默认 0 |
| like_count | int | 是 | 点赞数，默认 0 |
| comment_count | int | 是 | 评论数，默认 0 |
| publish_time | datetime | 否 | 发布时间 |
| author_id | bigint | 是 | 作者用户 ID |
| source | varchar(16) | 否 | 来源：local/web |
| content_hash | varchar(64) | 否 | 内容哈希（CLI 检测变更用） |
| is_deleted | tinyint | 是 | 逻辑删除 |
| create_time | datetime | 是 | 创建时间 |
| update_time | datetime | 是 | 更新时间 |

#### 2.2.3 blog_category（分类表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| name | varchar(32) | 是 | 分类名称 |
| parent_id | bigint | 是 | 父分类 ID，0 为一级 |
| sort | int | 是 | 排序权重，越小越靠前 |
| description | varchar(255) | 否 | 分类描述 |
| slug | varchar(64) | 否 | URL 别名，唯一 |
| article_count | int | 是 | 文章数量（冗余） |
| is_deleted | tinyint | 是 | 逻辑删除 |
| create_time | datetime | 是 | 创建时间 |
| update_time | datetime | 是 | 更新时间 |

#### 2.2.4 blog_tag（标签表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| name | varchar(32) | 是 | 标签名称，唯一索引 |
| use_count | int | 是 | 使用次数 |
| is_deleted | tinyint | 是 | 逻辑删除 |
| create_time | datetime | 是 | 创建时间 |
| update_time | datetime | 是 | 更新时间 |

#### 2.2.5 blog_article_tag（文章标签关联表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| article_id | bigint | 是 | 文章 ID，联合索引 |
| tag_id | bigint | 是 | 标签 ID，联合索引 |

#### 2.2.6 sys_file（文件表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| user_id | bigint | 是 | 上传用户 ID |
| file_name | varchar(128) | 是 | 原始文件名 |
| file_path | varchar(255) | 是 | 存储路径 |
| file_size | bigint | 是 | 文件大小（字节） |
| file_type | varchar(16) | 是 | image / video / document |
| mime_type | varchar(64) | 是 | MIME 类型 |
| storage_type | tinyint | 是 | 1=本地 2=MinIO 3=OSS |
| biz_type | varchar(32) | 否 | 业务类型：article_cover / content_image |
| url | varchar(255) | 是 | 可访问 URL |
| is_deleted | tinyint | 是 | 逻辑删除 |
| create_time | datetime | 是 | 创建时间 |
| update_time | datetime | 是 | 更新时间 |

#### 2.2.7 sys_config（系统配置表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| config_key | varchar(64) | 是 | 配置键，唯一索引 |
| config_value | text | 是 | 配置值 |
| group_name | varchar(32) | 否 | 配置分组：site / seo / social |
| description | varchar(255) | 否 | 配置说明 |
| create_time | datetime | 是 | 创建时间 |
| update_time | datetime | 是 | 更新时间 |

#### 2.2.8 sys_operation_log（操作日志表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| user_id | bigint | 否 | 操作人 ID |
| username | varchar(32) | 否 | 操作人用户名 |
| module | varchar(32) | 是 | 操作模块：article/user/auth |
| operation | varchar(32) | 是 | 操作类型：create/update/delete |
| target_id | bigint | 否 | 操作目标 ID |
| target_type | varchar(32) | 否 | 操作目标类型 |
| request_url | varchar(255) | 否 | 请求 URL |
| request_params | text | 否 | 请求参数 |
| response_data | text | 否 | 响应数据 |
| ip_address | varchar(32) | 否 | 请求 IP |
| duration | int | 否 | 耗时（毫秒） |
| status | tinyint | 是 | 0=失败 1=成功 |
| error_msg | varchar(500) | 否 | 错误信息 |
| create_time | datetime | 是 | 创建时间 |

#### 2.2.9 sys_login_log（登录日志表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| username | varchar(32) | 是 | 用户名 |
| ip_address | varchar(32) | 否 | 登录 IP |
| browser | varchar(64) | 否 | 浏览器信息 |
| os | varchar(32) | 否 | 操作系统 |
| status | tinyint | 是 | 0=失败 1=成功 |
| message | varchar(255) | 否 | 提示信息 |
| create_time | datetime | 是 | 创建时间 |

### 2.3 接口清单（阶段一）

> 统一规范：管理端前缀 `/api/`，统一返回 `Result<T>` 格式，分页返回 `PageResult<T>`。

#### 2.3.1 认证接口 — AuthController

##### POST /api/auth/login — 用户登录

| 参数 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| username | string | 是 | 3~32 位字母数字组合 | 用户名 |
| password | string | 是 | 6~64 位 | 密码（明文，HTTPS 传输） |

**响应**：`Result<TokenVO>`

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "expireIn": 7200,
    "userId": 1,
    "username": "admin",
    "roles": ["SUPER_ADMIN"]
  }
}
```

**业务逻辑**：
1. 根据 username 查询用户
2. 校验密码（BCryptPasswordEncoder.matches）
3. 校验用户状态（status=1 且 is_deleted=0）
4. 更新 last_login_time 和 last_login_ip
5. 生成 JWT Token（payload: sub=userId, username, roles）
6. 写入登录日志（成功/失败均记录）
7. 连续失败 3 次以上，下次登录需验证码（预留）

**异常场景**：
- 400：用户名或密码为空
- 401：用户名不存在或密码错误
- 403：账号已被禁用

---

##### POST /api/auth/register — 用户注册

| 参数 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| username | string | 是 | 3~32 位，字母数字 | 用户名，唯一 |
| password | string | 是 | 6~64 位 | 密码 |
| email | string | 是 | 邮箱格式 | 邮箱，唯一 |
| nickname | string | 否 | 1~32 位 | 昵称 |

**响应**：`Result<Void>`（201 Created）

**业务逻辑**：
1. 校验 username 和 email 唯一性
2. 密码 BCrypt 加密
3. 默认角色 = 访客（role=3）
4. status = 1（正常）
5. 插入用户记录

---

##### POST /api/auth/refresh — 刷新 Token

| 参数 | 位置 | 说明 |
|------|------|------|
| Authorization | Header | Bearer {旧 Token} |

**业务逻辑**：
- 仅在 Token 过期前 30 分钟内允许刷新
- 返回新 Token，旧 Token 加入黑名单（Redis，TTL=剩余有效期）

---

##### POST /api/auth/logout — 退出登录

| 参数 | 位置 | 说明 |
|------|------|------|
| Authorization | Header | Bearer {Token} |

**业务逻辑**：将当前 Token 加入黑名单（Redis），TTL 设为 Token 剩余有效期。

---

#### 2.3.2 用户接口 — UserController

##### GET /api/user/info — 当前用户信息

**请求头**：`X-User-Id`（网关解析 JWT 后注入）

**响应**：`Result<UserVO>`

```json
{
  "id": 1,
  "username": "admin",
  "nickname": "管理员",
  "avatar": "https://...",
  "email": "admin@example.com",
  "role": 1,
  "roleName": "超级管理员",
  "status": 1,
  "createTime": "2026-01-01 00:00:00"
}
```

**注意事项**：返回数据**不包含密码字段**。

---

##### PUT /api/user/info — 修改个人信息

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| nickname | string | 否 | 昵称 |
| avatar | string | 否 | 头像 URL |
| email | string | 否 | 邮箱 |

---

##### PUT /api/user/password — 修改密码

| 参数 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| oldPassword | string | 是 | 6~64 位 | 旧密码 |
| newPassword | string | 是 | 6~64 位 | 新密码（与旧密码不同） |

**业务逻辑**：校验旧密码 → BCrypt 加密新密码 → 更新。

---

##### GET /api/user/list — 用户列表（管理员）

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | int | 否 | 页码，默认 1 |
| size | int | 否 | 每页条数，默认 10 |
| keyword | string | 否 | 模糊搜索 username/nickname/email |
| status | int | 否 | 筛选状态：1=正常 0=禁用 |

---

##### GET /api/user/{id} — 用户详情

**返回**：`Result<UserVO>`（含角色列表）

---

##### POST /api/user — 新增用户（管理员）

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |
| email | string | 是 | 邮箱 |
| nickname | string | 否 | 昵称 |
| role | int | 否 | 角色，默认 3（访客） |

---

##### PUT /api/user/{id} — 编辑用户

| 参数 | 说明 |
|------|------|
| nickname | 可修改 |
| email | 可修改 |
| avatar | 可修改 |
| status | 可修改 |

---

##### DELETE /api/user/{id} — 删除用户（逻辑删除）

**业务逻辑**：`is_deleted = 1`，不可删除自己。

---

##### PATCH /api/user/{id}/status — 启用/禁用

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | int | 是 | 0=禁用 1=启用 |

---

##### PUT /api/user/{id}/reset-password — 重置密码

**业务逻辑**：重置为默认密码 `123456`（BCrypt 加密），不可重置自己。

---

##### PUT /api/user/{id}/roles — 分配角色

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleIds | array | 是 | [roleId1, roleId2] |

---

#### 2.3.3 文章接口 — ArticleController

##### POST /api/article — 创建文章

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | string | 是 | 1~128 位 |
| content | string | 是 | Markdown 正文 |
| contentHtml | string | 否 | 渲染后 HTML（可选，后端也可渲染） |
| summary | string | 否 | 摘要，最多 255 位 |
| coverImage | string | 否 | 封面图 URL |
| categoryId | long | 否 | 分类 ID |
| tagIds | array | 否 | [1, 2, 3] 标签 ID 列表 |
| status | int | 否 | 默认 0=草稿 |

**业务逻辑**：
1. 自动填充 authorId（从请求头 `X-User-Id`）
2. 如果 categoryId 不为空，校验分类存在
3. 如果 tagIds 不为空，校验标签存在，并写入 t_article_tag
4. status 默认 DRAFT（0）
5. 如果 categoryId 不为空，`category.article_count + 1`
6. 生成 content_html（Markdown 转 HTML）
7. 记录操作日志

**响应**：`Result<ArticleVO>`

---

##### PUT /api/article/{id} — 更新文章

| 参数 | 说明 |
|------|------|
| title | 可修改 |
| content | 可修改 |
| summary | 可修改 |
| coverImage | 可修改 |
| categoryId | 可修改（变更时联动更新 article_count） |
| tagIds | 可修改（全量替换：先删旧关联，再插入新关联） |

**权限校验**：仅作者或管理员可操作。

---

##### DELETE /api/article/{id} — 删除文章

**业务逻辑**：
- 已发布文章不可删除（需先下架）
- 逻辑删除：`is_deleted = 1`
- 联动更新 `category.article_count - 1`
- 清理关联的 t_article_tag

---

##### GET /api/article/{id} — 查看文章详情

**返回**：`Result<ArticleVO>`（含作者名、分类名、标签列表）

---

##### GET /api/article — 文章列表

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | int | 否 | 页码，默认 1 |
| size | int | 否 | 每页条数，默认 10 |
| categoryId | long | 否 | 分类筛选 |
| status | int | 否 | 状态筛选 |
| keyword | string | 否 | 模糊搜索 title+content |
| sort | string | 否 | 排序：latest / hot / top |

**排序策略**：
- `is_top = 1` 的记录永远排在最前
- 默认：按 `publish_time DESC`
- `hot`：按 `view_count DESC`

---

##### POST /api/article/{id}/publish — 发布文章

**状态流转**：DRAFT(0) → PUBLISHED(1)

**业务逻辑**：
- 校验文章状态必须为 DRAFT
- 设置 `publish_time = now()`
- 如果文章已有分类，`category.article_count + 1`

---

##### POST /api/article/{id}/withdraw — 撤回发布

**状态流转**：PUBLISHED(1) → DRAFT(0)

---

##### PATCH /api/article/{id}/top — 置顶/取消

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| top | int | 是 | 0=取消 1=置顶 |

---

##### POST /api/article/{id}/view — 浏览计数

**防刷策略**：
- 同一 IP + 同一文章，5 分钟内不重复计数
- 实现：Redis key `view:ip:{articleId}:{ip}`，TTL=5min
- 最终：`UPDATE article SET view_count = view_count + 1 WHERE id = ?`

---

##### POST /api/article/{id}/like — 点赞/取消

**业务逻辑**：
- 查 t_article_like：userId + articleId 是否已存在
- 未点赞 → 插入记录，`article.like_count + 1`
- 已点赞 → 删除记录，`article.like_count - 1`
- toggle 模式，同一接口重复调用 = 取消

---

#### 2.3.4 分类接口 — CategoryController

##### GET /api/category/tree — 分类树

**返回**：递归树形结构

```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "技术",
      "slug": "tech",
      "articleCount": 12,
      "children": [
        {
          "id": 2,
          "name": "Java",
          "parentId": 1,
          "articleCount": 5,
          "children": []
        }
      ]
    }
  ]
}
```

**实现**：查全表 → 内存递归构建树（最多 3 级）。

---

##### GET /api/category/{id} — 分类详情

---

##### POST /api/category — 新增分类

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | string | 是 | 分类名称 |
| parentId | long | 否 | 父分类 ID，0=一级 |
| slug | string | 否 | URL 别名，唯一 |
| sort | int | 否 | 排序权重 |
| description | string | 否 | 描述 |

**校验**：
- 如果指定 parentId，校验父分类存在
- 最多支持 3 级（parentId 递归深度校验）
- slug 唯一性校验

---

##### PUT /api/category/{id} — 编辑分类

---

##### DELETE /api/category/{id} — 删除分类

**校验**：
- 无子分类（parentId 不能指向该分类）
- 无关联文章（article_count = 0）

---

##### PATCH /api/category/sort — 批量排序

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | array | 是 | 按顺序排列的 [id1, id2, id3] |

**业务逻辑**：按数组顺序设置每项的 sort 值（0, 1, 2...）。

---

#### 2.3.5 标签接口 — TagController

##### GET /api/tag — 标签列表

| 参数 | 说明 |
|------|------|
| current / size | 分页 |
| keyword | 模糊搜索 |

---

##### POST /api/tag — 新增标签

| 参数 | 校验 |
|------|------|
| name | 唯一，1~32 位 |

---

##### PUT /api/tag/{id} — 编辑标签

---

##### DELETE /api/tag/{id} — 删除标签

**校验**：无关联文章（t_article_tag 中无记录）

---

##### GET /api/tag/hot — 热门标签

| 参数 | 说明 |
|------|------|
| limit | 默认 10 |

**排序**：按 `use_count DESC`

---

#### 2.3.6 文件接口 — FileController

##### POST /api/file/upload/image — 上传图片

**请求**：`multipart/form-data`

| 参数 | 校验 |
|------|------|
| file | 类型：jpg/png/gif/webp；大小 ≤ 5MB |

**响应**：

```json
{
  "code": 200,
  "data": {
    "id": 1,
    "url": "http://localhost:8080/uploads/2026/07/10/uuid.jpg",
    "fileName": "photo.jpg",
    "fileSize": 102400
  }
}
```

**存储路径**：`uploads/{yyyy/MM/dd}/{uuid}.{ext}`

---

##### POST /api/file/upload/file — 上传文件

| 参数 | 校验 |
|------|------|
| file | 扩展名白名单；大小 ≤ 20MB |

---

##### GET /api/file/list — 文件列表

| 参数 | 说明 |
|------|------|
| bizType | 业务类型筛选 |
| current / size | 分页 |

---

##### DELETE /api/file/{id} — 删除文件

**业务逻辑**：删除数据库记录 + 同时删除物理文件。

---

#### 2.3.7 系统配置接口 — SystemConfigController

##### GET /api/system/config/{key} — 获取配置项

##### PUT /api/system/config/{key} — 更新配置项

##### GET /api/system/configs — 获取所有配置（按分组返回）

---

#### 2.3.8 仪表盘接口 — DashboardController

##### GET /api/dashboard/overview — 数据概览

```json
{
  "articleCount": 42,
  "userCount": 10,
  "commentCount": 128,
  "totalViews": 15000,
  "todayViews": 230
}
```

---

##### GET /api/dashboard/trend — 趋势图

| 参数 | 说明 |
|------|------|
| days | 近 N 天，默认 7 |
| metric | article / view |

---

##### GET /api/dashboard/hot-articles — 热门文章 TOP10

**排序**：`ORDER BY view_count DESC LIMIT 10`

---

##### GET /api/dashboard/latest-comments — 最新评论 TOP10

---

##### GET /api/dashboard/category-stats — 分类统计

```json
[
  { "name": "技术", "count": 20, "percentage": 47.6 },
  { "name": "生活", "count": 15, "percentage": 35.7 }
]
```

---

#### 2.3.9 日志接口 — LogController

##### GET /api/log/operation — 操作日志列表

| 参数 | 说明 |
|------|------|
| module | 模块筛选 |
| operator | 操作人筛选 |
| dateRange | 日期范围 |

---

##### GET /api/log/login — 登录日志列表

| 参数 | 说明 |
|------|------|
| username | 用户名筛选 |
| status | 成功/失败筛选 |
| dateRange | 日期范围 |

### 2.4 开发路线（阶段一）

```
Phase 1.1（环境搭建）： 4h
  ├── 初始化项目结构（Maven 多模块）
  ├── 配置 MySQL + Redis + 基础依赖
  └── 定义 Result/PageResult/GlobalExceptionHandler

Phase 1.2（认证鉴权）： 12h
  ├── BCrypt 密码加密
  ├── JWT Token 完整链路
  ├── 登录/注册/刷新/登出
  └── RBAC 权限模型

Phase 1.3（用户管理）： 8h
  ├── 用户 CRUD + 分页搜索
  ├── 角色分配
  ├── 密码修改/重置
  └── 当前用户信息

Phase 1.4（文章核心）： 14h
  ├── 文章 CRUD + 状态机
  ├── 文章列表（分页+搜索+排序）
  ├── 置顶 + 浏览计数 + 点赞
  └── 标签关联

Phase 1.5（分类+标签+文件）： 8h
  ├── 分类树形 CRUD
  ├── 标签 CRUD + 热门标签
  └── 文件上传/删除

Phase 1.6（系统配置+日志+仪表盘）： 8h
  ├── 系统配置 CRUD
  ├── AOP 操作日志 + 登录日志
  └── 数据仪表盘

Phase 1.7（CLI 工具 + 静态站）： 12h
  ├── blog-cli push/pull/publish/preview
  ├── VitePress 静态站
  └── GitHub Actions 部署

总计：~66h（约 8 个工作日）

---

## 第三部分：阶段二 — CMS + 内容生态（模块化单体）

> 目标：从个人博客扩展为多角色 CMS 内容管理系统，增加社区互动能力。  
> 用户规模：数十~数百人。  
> 架构：模块化单体 + 中间件（Redis / RabbitMQ / ES / XXL-Job），所有模块仍打包为一个 Jar。

### 3.1 新增模块

```
vellastra-blog/
├── vellastra-comment          ← 评论系统（新增）
├── vellastra-publish          ← 发布引擎（新增，触发静态站构建）
├── vellastra-column           ← 专栏/专题系统（新增）
├── vellastra-newsletter       ← Newsletter 邮件订阅（新增）
├── vellastra-version          ← 文章版本管理（新增）
├── vellastra-recycle          ← 内容回收站（新增）
├── vellastra-page             ← 自定义页面（新增）
├── vellastra-analytics        ← 运营数据平台（新增，替代 V1 简单仪表盘）
├── vellastra-migration        ← 内容迁移工具（新增）
├── vellastra-interactive      ← 互动组件（投票/问卷，新增）
├── vellastra-site             ← 多站点管理（新增）
├── vellastra-watermark        ← 内容水印与版权（新增）
├── vellastra-common           ← 公共模块（已有，扩展）
├── vellastra-auth             ← 认证鉴权（已有，扩展 RBAC 权限粒度）
├── vellastra-user             ← 用户管理（已有，扩展用户等级）
├── vellastra-article          ← 文章管理（已有，扩展版本/回收站字段）
├── vellastra-category         ← 分类管理（已有，扩展 site_id）
├── vellastra-file             ← 文件管理（已有，扩展批量上传）
└── vellastra-bootstrap        ← 启动器（聚合所有模块）
```

### 3.2 新增数据库表

#### 3.2.1 blog_comment（评论表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| article_id | bigint | 是 | 文章 ID |
| user_id | bigint | 是 | 评论用户 ID |
| parent_id | bigint | 是 | 父评论 ID，0=一级评论 |
| reply_user_id | bigint | 否 | 被回复用户 ID |
| content | varchar(1024) | 是 | 评论内容 |
| status | tinyint | 是 | 0=待审核 1=已通过 2=已拒绝 |
| like_count | int | 是 | 点赞数 |
| is_deleted | tinyint | 是 | 逻辑删除 |
| create_time | datetime | 是 | 创建时间 |
| update_time | datetime | 是 | 更新时间 |

#### 3.2.2 cms_column（专栏表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| title | varchar(128) | 是 | 专栏标题 |
| description | varchar(255) | 否 | 专栏描述 |
| cover_url | varchar(255) | 否 | 封面图 |
| author_id | bigint | 是 | 创建者 ID |
| status | tinyint | 是 | 0=草稿 1=已发布 |
| article_count | int | 是 | 文章数量 |
| view_count | int | 是 | 浏览数 |
| sort_order | int | 是 | 排序 |
| is_deleted | tinyint | 是 | 逻辑删除 |
| create_time | datetime | 是 | 创建时间 |
| update_time | datetime | 是 | 更新时间 |

#### 3.2.3 cms_column_article（专栏文章关联表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| column_id | bigint | 是 | 专栏 ID |
| article_id | bigint | 是 | 文章 ID |
| sort_order | int | 是 | 排序 |
| create_time | datetime | 是 | 创建时间 |

#### 3.2.4 cms_article_version（文章版本表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| article_id | bigint | 是 | 文章 ID |
| version | int | 是 | 版本号 |
| title | varchar(128) | 是 | 版本标题 |
| content | longtext | 是 | 版本内容 |
| summary | varchar(255) | 否 | 版本摘要 |
| operator_id | bigint | 否 | 操作人 ID |
| change_note | varchar(255) | 否 | 变更说明 |
| is_deleted | tinyint | 是 | 逻辑删除 |
| create_time | datetime | 是 | 创建时间 |

#### 3.2.5 cms_page（自定义页面表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| title | varchar(128) | 是 | 页面标题 |
| slug | varchar(64) | 是 | URL 别名，唯一 |
| content | longtext | 是 | Markdown 内容 |
| template | varchar(32) | 否 | 页面模板 |
| show_in_nav | tinyint | 是 | 是否显示在导航栏 |
| status | tinyint | 是 | 0=草稿 1=已发布 |
| is_deleted | tinyint | 是 | 逻辑删除 |
| create_time | datetime | 是 | 创建时间 |
| update_time | datetime | 是 | 更新时间 |

#### 3.2.6 cms_recycle_bin（回收站表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| biz_type | varchar(32) | 是 | 业务类型：article/comment/category |
| biz_id | bigint | 是 | 业务数据 ID |
| biz_data | json | 是 | 业务数据快照（完整 JSON） |
| operator_id | bigint | 是 | 操作人 ID |
| expire_time | datetime | 是 | 过期时间 |
| create_time | datetime | 是 | 创建时间 |

#### 3.2.7 cms_newsletter_subscriber（邮件订阅表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| email | varchar(64) | 是 | 邮箱，唯一 |
| user_id | bigint | 否 | 关联用户 ID |
| frequency | tinyint | 是 | 1=每日 2=每周 3=每月 |
| status | tinyint | 是 | 1=活跃 2=已退订 3=已退回 |
| token | varchar(64) | 是 | 退订令牌 |
| subscribe_time | datetime | 是 | 订阅时间 |
| unsubscribe_time | datetime | 否 | 退订时间 |

#### 3.2.8 cms_newsletter_send_log（发送记录表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| subject | varchar(128) | 是 | 邮件主题 |
| content | text | 是 | 邮件内容 |
| recipient_count | int | 是 | 收件人数 |
| open_count | int | 是 | 打开数 |
| click_count | int | 是 | 点击数 |
| status | tinyint | 是 | 0=待发送 1=发送中 2=已成功 3=失败 |
| send_time | datetime | 否 | 发送时间 |
| create_time | datetime | 是 | 创建时间 |

#### 3.2.9 cms_analytics_event（分析事件表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| event_type | varchar(32) | 是 | page_view / article_view / click / stay |
| user_id | bigint | 否 | 用户 ID |
| article_id | bigint | 否 | 文章 ID |
| ip_address | varchar(32) | 否 | IP 地址 |
| user_agent | varchar(500) | 否 | UA 信息 |
| duration | int | 否 | 停留时长（秒） |
| event_time | datetime | 是 | 事件时间 |
| create_time | datetime | 是 | 记录时间 |

#### 3.2.10 cms_site（子站点表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| name | varchar(64) | 是 | 站点名称 |
| domain | varchar(128) | 否 | 绑定域名 |
| subdomain | varchar(64) | 否 | 子域名 |
| theme | varchar(32) | 否 | 当前主题 |
| config_json | json | 否 | 站点配置 |
| status | tinyint | 是 | 0=禁用 1=正常 |
| is_deleted | tinyint | 是 | 逻辑删除 |
| create_time | datetime | 是 | 创建时间 |
| update_time | datetime | 是 | 更新时间 |

#### 3.2.11 cms_poll（投票表）/ cms_poll_option / cms_poll_vote

| 表 | 说明 |
|----|------|
| cms_poll | 投票主体（title/type/start_time/end_time） |
| cms_poll_option | 选项（poll_id/option_text/sort） |
| cms_poll_vote | 投票记录（poll_id/option_id/user_id，唯一约束） |

#### 3.2.12 cms_watermark_config（水印配置表）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | bigint | 是 | 主键 ID |
| type | tinyint | 是 | 1=文字 2=图片 |
| content | varchar(128) | 是 | 水印内容 |
| position | tinyint | 是 | 位置：1~9（九宫格） |
| opacity | float | 是 | 透明度 0.0~1.0 |
| is_enabled | tinyint | 是 | 是否启用 |
| create_time | datetime | 是 | 创建时间 |
| update_time | datetime | 是 | 更新时间 |

### 3.3 新增接口清单（阶段二）

#### 3.3.1 评论接口 — CommentController

##### POST /api/comment — 发表评论

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| articleId | long | 是 | 文章 ID |
| content | string | 是 | 1~1024 位 |
| parentId | long | 否 | 父评论 ID，0=一级评论 |

**业务逻辑**：
1. 校验文章存在且已发布
2. status 默认 PENDING(0)（待审核）
3. 自动从请求头取 userId
4. `article.comment_count + 1`

---

##### POST /api/comment/reply — 回复评论

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| parentId | long | 是 | 父评论 ID |
| replyToId | long | 是 | 被回复的评论 ID |
| content | string | 是 | 回复内容 |

**业务逻辑**：
- 校验 parentId 对应的评论存在
- 自动填充 replyToUserId
- 回复层级最多 3 层（超过时自动转为回复第 3 层）

---

##### DELETE /api/comment/{id} — 删除评论

**业务逻辑**：级联删除子评论（或标记删除后子评论显示"已删除"）。

---

##### PATCH /api/comment/{id}/audit — 审核评论

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | int | 是 | 1=通过 2=拒绝 |

**状态机**：PENDING(0) → APPROVED(1) / REJECTED(2)

---

##### GET /api/comment — 评论列表

| 参数 | 说明 |
|------|------|
| articleId | 按文章筛选 |
| status | 按状态筛选（管理员视角） |
| current / size | 分页 |

**返回**：树形结构，每条顶层评论携带 children（最近 3 条子评论）。

---

#### 3.3.2 专栏接口 — ColumnController

##### POST /api/column — 创建专栏

| 参数 | 必填 | 说明 |
|------|------|------|
| title | 是 | 专栏标题 |
| description | 否 | 描述 |
| cover | 否 | 封面图 |

---

##### PUT /api/column/{id} — 编辑专栏

---

##### GET /api/column/{id} — 专栏详情（含文章列表）

---

##### GET /api/column — 专栏列表

| 参数 | 说明 |
|------|------|
| sort | hot / newest |

---

##### DELETE /api/column/{id} — 删除专栏

**业务逻辑**：仅删除专栏本身，不删除关联文章。

---

##### POST /api/column/{id}/article — 向专栏添加文章

| 参数 | 必填 | 说明 |
|------|------|------|
| articleId | 是 | 文章 ID |

---

##### DELETE /api/column/{id}/article/{articleId} — 从专栏移除文章

---

##### PUT /api/column/{id}/article/sort — 文章排序

| 参数 | 必填 | 说明 |
|------|------|------|
| articleIds | 是 | [id1, id2, id3] 按顺序排列 |

---

#### 3.3.3 版本管理接口 — VersionController

##### GET /api/article/{id}/versions — 版本列表

---

##### GET /api/article/{id}/versions/{versionId} — 查看某版本内容

---

##### POST /api/article/{id}/versions — 手动创建版本快照

---

##### POST /api/article/{id}/rollback/{versionId} — 回滚到指定版本

**业务逻辑**：回滚时自动创建当前版本的备份版本。

---

##### GET /api/article/{id}/versions/diff — 版本对比

| 参数 | 说明 |
|------|------|
| from | 旧版本 ID |
| to | 新版本 ID |

**返回**：两版差异（使用 diff 算法，行级别高亮）。

---

##### DELETE /api/article/{id}/versions/{versionId} — 删除版本

**业务规则**：保留最近 5 个版本，不可删除最后一个版本。

---

#### 3.3.4 回收站接口 — RecycleBinController

##### GET /api/recycle-bin — 回收站列表

| 参数 | 说明 |
|------|------|
| bizType | 业务类型筛选：article / comment / category |
| current / size | 分页 |

---

##### POST /api/recycle-bin/{id}/restore — 恢复内容

**业务逻辑**：还原 biz_data 快照到原表，恢复关联关系。

---

##### DELETE /api/recycle-bin/{id} — 彻底删除

---

##### DELETE /api/recycle-bin/empty — 清空回收站

---

##### GET /api/recycle-bin/settings — 查看设置

##### PUT /api/recycle-bin/settings — 更新设置（自动清理天数，默认 30 天）

---

#### 3.3.5 自定义页面接口 — PageController

##### CRUD 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/page | 页面列表 |
| POST | /api/page | 创建页面 |
| GET | /api/page/{id} | 页面详情 |
| PUT | /api/page/{id} | 编辑页面 |
| DELETE | /api/page/{id} | 删除页面 |

---

#### 3.3.6 运营数据接口 — AnalyticsController

##### GET /api/analytics/overview — 运营总览（日/周/月同比）

##### GET /api/analytics/traffic — 流量分析（PV/UV/IP/来源）

##### GET /api/analytics/content — 内容分析（阅读排名/转化率）

##### GET /api/analytics/user — 用户分析（新增/活跃/留存）

##### GET /api/analytics/retention — 留存分析（次日/7日/30日）

##### GET /api/analytics/conversion — 转化漏斗（浏览→评论→关注）

---

#### 3.3.7 内容迁移接口 — MigrationController

##### POST /api/migration/import — 导入内容

| 参数 | 说明 |
|------|------|
| file | Markdown / HTML / Word 文件 |
| format | md / html / docx |

**业务逻辑**：解析 frontmatter → 提取元信息 → 调用文章创建接口。

---

##### POST /api/migration/import/url — 从 URL 导入

| 参数 | 说明 |
|------|------|
| url | 目标文章 URL |

---

##### GET /api/migration/tasks — 导入任务列表

##### GET /api/migration/tasks/{id} — 任务详情（进度/结果）

---

#### 3.3.8 互动组件接口 — InteractiveController

##### POST /api/interactive/poll — 创建投票

| 参数 | 必填 | 说明 |
|------|------|------|
| title | 是 | 投票标题 |
| options | 是 | ["选项1", "选项2", "选项3"] |
| type | 是 | single / multiple |
| endTime | 否 | 截止时间 |

---

##### POST /api/interactive/poll/{id}/vote — 参与投票

| 参数 | 必填 | 说明 |
|------|------|------|
| optionId | 是 | 选项 ID |

---

##### GET /api/interactive/poll/{id} — 投票详情 + 结果

---

#### 3.3.9 多站点接口 — SiteController

##### POST /api/site — 创建子站点

| 参数 | 必填 | 说明 |
|------|------|------|
| name | 是 | 站点名称 |
| subdomain | 是 | 子域名，唯一 |

---

##### PUT /api/site/{id}/domain — 绑定自定义域名

| 参数 | 必填 | 说明 |
|------|------|------|
| domain | 是 | 自定义域名 |

---

##### GET /api/site — 站点列表

##### DELETE /api/site/{id} — 删除站点

---

#### 3.3.10 水印与版权接口 — WatermarkController

##### PUT /api/watermark/config — 配置水印

| 参数 | 必填 | 说明 |
|------|------|------|
| type | 是 | 1=文字 2=图片 |
| content | 是 | 水印内容/图片 URL |
| position | 是 | 1~9 位置 |
| opacity | 是 | 透明度 |

---

##### GET /api/watermark/config — 查看水印配置

---

#### 3.3.11 发布引擎接口 — PublishController

##### POST /api/publish/{articleId} — 触发单篇文章发布

**业务逻辑**：
1. 校验文章状态 = PUBLISHED
2. 更新文章状态为 DEPLOYING
3. 触发异步构建任务（RabbitMQ 消息）
4. 构建完成后，回调更新状态为 DEPLOYED

---

##### POST /api/publish/batch — 批量发布

##### POST /api/publish/full — 全量重建

##### GET /api/export/articles — 导出文章（给 SSG 消费）

| 参数 | 说明 |
|------|------|
| status | published |

##### GET /api/export/site-config — 导出站点配置

### 3.4 阶段二新增中间件集成

#### 3.4.1 Redis 缓存

| 缓存项 | Key 模式 | TTL | 失效时机 |
|--------|----------|-----|----------|
| 分类树 | cache:category:tree | 30min | 分类增删改时 |
| 热门标签 | cache:tag:hot | 10min | 标签增删改时 |
| 站点配置 | cache:config:{key} | 30min | 配置更新时 |
| 用户权限 | cache:auth:perms:{userId} | 与 Token 同效 | Token 刷新时 |
| 文章详情 | cache:article:{id} | 10min | 文章更新时 |

#### 3.4.2 RabbitMQ 异步消息

| 消息类型 | 场景 | 消费者 |
|----------|------|--------|
| publish.article | 发布文章触发构建 | vellastra-publish |
| export.articles | 导出文章数据给 SSG | vellastra-publish |
| send.email | 发送 Newsletter | vellastra-newsletter |
| sync.analytics | 异步同步分析数据 | vellastra-analytics |

#### 3.4.3 Elasticsearch 全文搜索

**索引映射**：
- article_index：title（text）、content（text）、summary（text）、category_name（keyword）、tags（keyword）、publish_time（date）

**数据同步**：
- 文章增删改时，发送 MQ 消息 → 消费者同步到 ES
- 全量重建：XXL-Job 定时全量同步

### 3.5 开发路线（阶段二）

```
Phase 2.1（评论系统）： 8h
  ├── 评论 CRUD + 楼中楼回复
  ├── 评论审核状态机
  └── 树形评论列表

Phase 2.2（专栏+版本+回收站+页面）： 12h
  ├── 专栏 CRUD + 文章关联
  ├── 版本管理 + 差异对比
  ├── 回收站软删除 + 恢复
  └── 自定义页面 CRUD

Phase 2.3（运营数据+迁移+互动）： 10h
  ├── 运营数据平台（多维度统计）
  ├── 内容迁移工具
  └── 投票/问卷组件

Phase 2.4（多站点+水印+发布引擎）： 8h
  ├── 多站点管理
  ├── 水印配置
  └── 发布引擎 + 构建触发

Phase 2.5（中间件集成）： 12h
  ├── Redis 缓存策略
  ├── RabbitMQ 异步消息
  ├── Elasticsearch 全文搜索
  └── XXL-Job 定时任务

总计：~50h（约 6 个工作日）

---

## 第四部分：阶段三 — UGC 知识社区（微服务架构）

> 目标：从 CMS 升级为类知乎/掘金的全场景 UGC 知识社区。  
> 用户规模：万人以上。  
> 架构：Spring Cloud 微服务分布式架构，按业务领域垂直拆分。

### 4.1 微服务拆分

```
vellastra-gateway              ← API 网关（路由/鉴权/限流/熔断）
vellastra-auth                 ← 认证服务（登录/注册/OAuth/JWT/RBAC）
vellastra-user                 ← 用户服务（用户信息/等级/关注/粉丝）
vellastra-article              ← 文章服务（文章 CRUD/状态机/标签/版本）
vellastra-category             ← 分类服务（分类树形管理）
vellastra-comment              ← 评论服务（评论/回复/审核）
vellastra-file                 ← 文件服务（文件上传/存储/CDN）
vellastra-feed                 ← Feed 推荐流（个性化推荐/最新/热门）
vellastra-question             ← 问答服务（提问/回答/投票/采纳）
vellastra-interaction          ← 互动服务（点赞/收藏/关注/通知）
vellastra-rank                 ← 排行榜服务（日/周/月热度排行）
vellastra-audit                ← 审核服务（内容审核/风控/举报）
vellastra-search               ← 搜索服务（Elasticsearch 全文搜索）
vellastra-ai                   ← AI 服务（智能标签/摘要/配图/写作）
vellastra-distribute           ← 分发服务（多平台内容分发/社交同步）
vellastra-open                 ← 开放 API 服务（API Key / Webhook）
vellastra-seo                  ← SEO 服务（SEO 分析/Sitemap/结构化数据）
vellastra-backup               ← 备份服务（自动备份/还原）
vellastra-plugin               ← 插件服务（插件市场/安装/卸载）
vellastra-notification         ← 通知服务（站内信/邮件/WebSocket 推送）
vellastra-common               ← 公共模块（工具/异常/响应/Feign 接口）
```

### 4.2 新增接口清单（阶段三）

#### 4.2.1 Feed 推荐流 — FeedController

##### GET /api/feed/recommend — 个性化推荐

| 参数 | 说明 |
|------|------|
| current / size | 分页 |

**推荐策略（渐进式）**：
- V3.0：简单时间线 + 热度排序
- V3.1：基于标签的粗推荐（关注标签 → 推荐相关内容）
- V3.2：协同过滤（用户行为相似度）

---

##### GET /api/feed/latest — 最新内容流

##### GET /api/feed/following — 关注的人的内容

##### GET /api/feed/hot — 热门内容

---

#### 4.2.2 问答系统 — QuestionController

##### POST /api/question — 提问

| 参数 | 必填 | 说明 |
|------|------|------|
| title | 是 | 问题标题 |
| content | 是 | 问题描述 |
| tags | 否 | 标签列表 |

---

##### GET /api/question/{id} — 查看问题（含回答列表）

##### GET /api/question — 问题列表（分页+筛选）

##### POST /api/answer — 回答问题

| 参数 | 必填 | 说明 |
|------|------|------|
| questionId | 是 | 问题 ID |
| content | 是 | 回答内容 |

---

##### PUT /api/answer/{id} — 编辑回答

##### POST /api/answer/{id}/accept — 采纳回答（仅提问者可操作）

##### POST /api/answer/{id}/vote — 投票赞同/反对

| 参数 | 必填 | 说明 |
|------|------|------|
| type | 是 | up / down |

---

#### 4.2.3 互动与通知 — InteractionController / NotificationController

##### POST /api/interaction/like — 点赞/取消

| 参数 | 必填 | 说明 |
|------|------|------|
| targetType | 是 | article / comment / answer |
| targetId | 是 | 目标 ID |

---

##### POST /api/interaction/follow — 关注/取消

| 参数 | 说明 |
|------|------|
| targetUserId | 被关注用户 ID |

---

##### POST /api/interaction/bookmark — 收藏/取消

| 参数 | 说明 |
|------|------|
| targetType | article / answer |
| targetId | 目标 ID |

---

##### GET /api/notification — 通知列表

| 参数 | 说明 |
|------|------|
| type | 通知类型筛选 |
| current / size | 分页 |

**通知类型**：
- 赞了你的文章/评论
- 回复了你的评论
- 关注了你
- 回答了你的问题
- 回答被采纳

---

##### PATCH /api/notification/{id}/read — 标记已读

##### POST /api/notification/read-all — 全部标记已读

---

#### 4.2.4 排行榜 — RankController

##### GET /api/rank/daily — 日榜

##### GET /api/rank/weekly — 周榜

##### GET /api/rank/monthly — 月榜

**热度算法（Hacker News 风格）**：

```
score = (点赞数 - 踩数) / (time)^1.5
```

---

#### 4.2.5 内容审核与风控 — AuditController

##### GET /api/audit/pending — 待审核列表

| 参数 | 说明 |
|------|------|
| targetType | article / comment / question / answer |

---

##### POST /api/audit/approve — 通过

| 参数 | 说明 |
|------|------|
| targetType | 目标类型 |
| targetId | 目标 ID |

---

##### POST /api/audit/reject — 拒绝

| 参数 | 说明 |
|------|------|
| targetType | 目标类型 |
| targetId | 目标 ID |
| reason | 拒绝原因 |

---

##### POST /api/audit/report — 用户举报

| 参数 | 必填 | 说明 |
|------|------|------|
| targetType | 是 | 举报对象类型 |
| targetId | 是 | 举报对象 ID |
| reason | 是 | 举报原因：spam/porn/abuse/plagiarism |

---

##### GET /api/audit/reports — 举报列表（管理员）

**审核机制**：
1. 新用户前 5 篇文章需人工审核
2. 信用分达标后自动过审
3. 敏感词命中自动拦截
4. 举报触发重新审核

---

#### 4.2.6 用户等级 — UserLevelController

##### GET /api/user/{id}/profile — 用户主页

**返回**：用户信息 + 文章数 + 回答数 + 获赞数 + 等级

---

##### GET /api/user/{id}/followers — 粉丝列表

##### GET /api/user/{id}/following — 关注列表

##### GET /api/user/{id}/stats — 用户统计

**等级体系**：

| 等级 | 条件 | 权益 |
|------|------|------|
| Lv1 | 注册用户 | 可以发文、评论 |
| Lv2 | 发布 5 篇以上 | 可以创建专栏 |
| Lv3 | 100 赞以上 | 内容优先推荐 |
| Lv4 | 1000 赞以上 | 创作者认证 |
| Lv5 | 10000 赞以上 | 社区 KOL 标识 |

---

#### 4.2.7 全文搜索 — SearchController

##### GET /api/search — 全局搜索

| 参数 | 必填 | 说明 |
|------|------|------|
| keyword | 是 | 搜索关键词 |
| type | 否 | article / question / user |
| current / size | 否 | 分页 |

---

##### GET /api/search/suggestion — 搜索建议

| 参数 | 说明 |
|------|------|
| keyword | 前缀匹配 |

**实现**：Elasticsearch completion suggester。

---

#### 4.2.8 AI 辅助工具 — AIController

##### POST /api/ai/tag — AI 智能打标签

| 参数 | 说明 |
|------|------|
| content | 文章内容 |

**返回**：`["Java", "Spring Boot", "微服务"]`

---

##### POST /api/ai/summary — AI 生成摘要

##### POST /api/ai/category — AI 推荐分类

##### POST /api/ai/keywords — AI 提取关键词

##### POST /api/ai/description — AI 生成 SEO 描述

##### POST /api/ai/image/cover — AI 生成封面图

##### POST /api/ai/image/illustration — AI 生成段落配图

##### POST /api/ai/writer/title — 生成标题候选

##### POST /api/ai/writer/expand — 扩写段落

##### POST /api/ai/writer/rewrite — 改写润色

##### POST /api/ai/writer/continue — 续写

##### POST /api/ai/writer/translate — 翻译

**AI 调用规则**：
- 每次保存文章时自动触发标签/摘要生成
- 用户可编辑 AI 生成的结果
- 按用户/角色设置每日调用额度

---

#### 4.2.9 多平台分发 — DistributeController

##### POST /api/distribute/platform — 绑定外部平台账号

| 参数 | 说明 |
|------|------|
| platform | 平台标识：wechat_mp / zhihu / csdn / juejin / medium |
| accessToken | 平台 API Token |

---

##### POST /api/distribute/publish — 发布到指定平台

| 参数 | 说明 |
|------|------|
| articleId | 文章 ID |
| platforms | ["zhihu", "csdn"] |

---

##### GET /api/distribute/history — 分发记录

##### GET /api/distribute/status/{taskId} — 分发任务状态

---

#### 4.2.10 OAuth 第三方登录 — OAuthController

##### GET /api/oauth/{provider}/authorize — 跳转授权页

**支持平台**：GitHub / Google / 微信 / QQ / Gitee

---

##### GET /api/oauth/{provider}/callback — OAuth 回调

**业务逻辑**：
1. 接收第三方回调 code
2. 换取 access_token
3. 获取第三方用户信息
4. 首次登录自动创建账号
5. 已有账号绑定第三方账号
6. 发放 JWT Token

---

##### POST /api/oauth/bind — 绑定第三方账号到已有账号

##### GET /api/oauth/providers — 支持的第三方登录列表

---

#### 4.2.11 开放 API 与 Webhook — OpenController

##### POST /api/open/api-key — 创建 API Key

| 参数 | 说明 |
|------|------|
| name | 密钥名称 |
| scope | read / write |

---

##### GET /api/open/api-keys — API Key 列表

##### DELETE /api/open/api-key/{id} — 吊销 API Key

##### POST /api/open/webhook — 创建 Webhook

| 参数 | 说明 |
|------|------|
| url | 回调 URL |
| events | 订阅事件列表 |

**Webhook 事件**：

| 事件 | 触发时机 |
|------|----------|
| article.published | 文章发布 |
| article.updated | 文章更新 |
| article.deleted | 文章删除 |
| comment.created | 新评论 |
| site.build | 触发静态站构建 |

---

##### GET /api/open/webhook/{id}/logs — Webhook 调用日志

##### POST /api/open/webhook/{id}/test — 测试 Webhook

**开放 API 端点（公网可访问）**：

| 端点 | 说明 |
|------|------|
| GET /api/open/v1/articles | 公开文章列表 |
| GET /api/open/v1/articles/{id} | 文章详情 |
| GET /api/open/v1/categories | 分类树 |
| GET /api/open/v1/tags | 标签列表 |
| GET /api/open/v1/site/info | 站点信息 |

---

#### 4.2.12 SEO 服务 — SEOController

##### POST /api/seo/analyze/{articleId} — SEO 分析

**分析指标**：
- 标题长度与关键词密度
- 描述完整度
- H1/H2/H3 结构
- Alt 文本覆盖率
- 内链/外链数量
- 可读性评分

---

##### POST /api/seo/sitemap/generate — 生成/更新 Sitemap

##### GET /api/seo/sitemap — 获取 Sitemap URL

##### POST /api/seo/robots/generate — 生成 robots.txt

##### PUT /api/seo/robots — 自定义 robots.txt

---

#### 4.2.13 缓存与 CDN 管理 — CacheController

##### GET /api/admin/cache/status — 缓存状态

##### POST /api/admin/cache/clear — 清除缓存

| 参数 | 说明 |
|------|------|
| type | article / category / all |

---

##### PUT /api/admin/cdn/config — CDN 配置

##### POST /api/admin/cdn/refresh — 刷新 CDN

| 参数 | 说明 |
|------|------|
| type | url / directory / all |

---

#### 4.2.14 备份与还原 — BackupController

##### POST /api/backup/create — 手动备份

##### GET /api/backup/list — 备份列表

##### POST /api/backup/schedule — 设置自动备份计划

**自动备份策略**：
- 每日自动备份，保留最近 7 天
- 每周全量备份，保留最近 4 周
- 每月归档备份，保留最近 6 个月

---

##### POST /api/backup/restore/{id} — 从备份还原

##### DELETE /api/backup/{id} — 删除备份

---

#### 4.2.15 插件系统 — PluginController

##### GET /api/plugin/market — 插件市场列表

##### POST /api/plugin/install — 安装插件

##### POST /api/plugin/uninstall — 卸载插件

##### POST /api/plugin/enable — 启用插件

##### POST /api/plugin/disable — 禁用插件

##### GET /api/plugin/{id}/config — 插件配置

##### PUT /api/plugin/{id}/config — 更新插件配置

**插件能力范围**：
- 文章处理管道（格式化/自动添加脚注）
- Shortcode 扩展（视频嵌入/图表/代码运行）
- 第三方服务集成（GA / 百度统计）
- 内容导入/导出格式扩展

---

### 4.3 阶段三新增基础设施

| 组件 | 用途 |
|------|------|
| Spring Cloud Alibaba + Nacos | 服务注册发现 + 配置中心 |
| Spring Cloud Gateway | 统一网关（路由/鉴权/限流） |
| Sentinel | 接口限流 + 熔断降级 |
| Seata | 分布式事务 |
| Redisson | 分布式锁 |
| RocketMQ | 事务消息 + 海量消息堆积 |
| ShardingSphere-JDBC | 分库分表 |
| SkyWalking | 全链路追踪 |
| Kafka + Flink + Spark | 推荐系统实时/离线计算 |

### 4.4 开发路线（阶段三）

```
Phase 3.1（微服务基础设施）： 4 周
  ├── Nacos 注册配置中心
  ├── Gateway 统一网关
  ├── Sentinel 限流熔断
  ├── 各服务拆分与独立部署
  └── 服务间 Feign 调用

Phase 3.2（社区核心功能）： 8 周
  ├── Feed 推荐流
  ├── 问答系统
  ├── 互动与通知
  ├── 排行榜
  └── 用户等级体系

Phase 3.3（搜索与审核）： 4 周
  ├── Elasticsearch 全文搜索
  ├── 内容审核与风控
  └── 用户举报系统

Phase 3.4（AI 与开放平台）： 8 周
  ├── AI 智能标签/摘要/配图/写作
  ├── 多平台分发
  ├── OAuth 第三方登录
  ├── 开放 API 与 Webhook
  └── SEO 工具

Phase 3.5（运维与生态）： 6 周
  ├── 主题市场
  ├── CDN 与缓存策略
  ├── 备份与还原
  ├── 插件系统
  └── 推荐系统（可选）/ 广告系统（可选）

总计：~30 周（可并行，视团队规模调整）

---

## 第五部分：CLI 工具设计

> blog-cli 是连接本地写作和后台管理的核心工具，作为本地写作的主要入口。

### 5.1 命令清单

```
vellastra-cli init                # 初始化配置（API 地址 / Token）
vellastra-cli push <file|dir>     # 推送单篇文章或批量推送目录
vellastra-cli pull <id>           # 从后台拉取文章到本地
vellastra-cli publish <id>        # 触发发布（构建静态站）
vellastra-cli preview             # 本地预览效果
vellastra-cli sync                # 双向同步检测（显示差异）
vellastra-cli config              # 查看/修改配置
```

### 5.2 核心流程：push

```
1. 解析 Markdown 文件
   ├── 读取 frontmatter（title/date/tags/category/status/slug/cover/summary）
   └── 提取正文内容

2. 扫描本地图片引用
   ├── 正则匹配 ![](path/to/image.jpg)
   ├── 批量上传到后台文件服务
   └── 替换本地路径为远程 URL

3. 检测内容变更
   ├── 计算 content_hash（SHA256）
   ├── 与后台已有记录的 content_hash 对比
   └── 无变更 → 跳过；有变更 → 更新

4. 调用后台 API
   ├── 文章不存在 → POST /api/article（创建）
   └── 文章已存在 → PUT /api/article/{id}（更新）
```

### 5.3 本地目录约定

```
~/vellastra-content/
├── posts/                    # 文章源文件
│   ├── tech/
│   │   └── spring-cloud.md
│   ├── life/
│   │   └── daily-note.md
│   └── _drafts/              # 草稿（不同步到后台）
│       └── unfinished.md
├── images/                   # 本地图片（push 时自动上传）
│   └── 2026/07/
│       └── photo.jpg
└── .vellastra/               # CLI 配置
    └── config.json
```

### 5.4 Markdown Frontmatter 规范

```markdown
---
title: "Spring Cloud Gateway 实战"
date: 2026-07-10
category: tech
tags: [java, spring-cloud, gateway]
status: draft                # draft / published
slug: spring-cloud-gateway   # URL 别名
cover: ./images/cover.jpg     # 本地封面图路径
summary: 本文介绍 Spring Cloud Gateway 的核心概念...
---

正文内容... ![](./images/demo.jpg) 图片会由 CLI 自动上传替换。
```

---

## 第六部分：完整数据库设计汇总

### 6.1 阶段一表（9 张）

| 表名 | 说明 | 所属域 |
|------|------|--------|
| sys_user | 用户表 | 用户域 |
| blog_article | 文章表 | 内容域 |
| blog_category | 分类表 | 内容域 |
| blog_tag | 标签表 | 内容域 |
| blog_article_tag | 文章标签关联 | 内容域 |
| sys_file | 文件表 | 系统域 |
| sys_config | 系统配置表 | 系统域 |
| sys_operation_log | 操作日志表 | 日志域 |
| sys_login_log | 登录日志表 | 日志域 |

### 6.2 阶段二新增表（14 张）

| 表名 | 说明 | 所属域 |
|------|------|--------|
| blog_comment | 评论表 | 交互域 |
| cms_column | 专栏表 | 内容域 |
| cms_column_article | 专栏文章关联 | 内容域 |
| cms_article_version | 文章版本表 | 内容域 |
| cms_page | 自定义页面表 | 内容域 |
| cms_recycle_bin | 回收站表 | 系统域 |
| cms_newsletter_subscriber | 邮件订阅表 | 运营域 |
| cms_newsletter_send_log | 邮件发送记录 | 运营域 |
| cms_analytics_event | 分析事件表 | 运营域 |
| cms_site | 子站点表 | 站点域 |
| cms_poll | 投票表 | 交互域 |
| cms_poll_option | 投票选项表 | 交互域 |
| cms_poll_vote | 投票记录表 | 交互域 |
| cms_watermark_config | 水印配置表 | 系统域 |

### 6.3 阶段三新增表（按微服务拆分）

**用户服务**：
- ugc_user_follow（用户关注表）
- ugc_user_level（用户等级表）
- ugc_user_points（用户积分表）

**内容服务**：
- ugc_question（问答表）
- ugc_answer（回答表）
- ugc_column（专栏表，升级）
- ugc_topic（话题表）
- ugc_topic_relation（话题关联表）

**互动服务**：
- ugc_like（通用点赞表）
- ugc_bookmark（收藏表）
- ugc_report（举报表）
- ugc_notification（通知表）

**审核服务**：
- ugc_audit_record（审核记录表）
- ugc_sensitive_word（敏感词表）

**AI 服务**：
- ai_quota（AI 调用额度表）
- ai_generate_log（AI 生成记录表）

**分发服务**：
- dist_platform_account（平台账号绑定表）
- dist_task（分发任务表）

**开放 API 服务**：
- open_api_key（API Key 表）
- open_webhook（Webhook 配置表）
- open_webhook_log（Webhook 调用日志表）

**SEO 服务**：
- seo_analysis_record（SEO 分析记录表）

**备份服务**：
- sys_backup_record（备份记录表）

**插件服务**：
- plugin_info（插件信息表）
- plugin_config（插件配置表）

### 6.4 星垂野联邦体系表（各阶段通用）

| 表名 | 说明 | 新增阶段 |
|------|------|----------|
| fed_node | 联邦节点表 | 阶段一 |
| fed_sync_record | 同步记录表（含幂等 request_id） | 阶段一 |
| fed_message | 节点间消息表 | 阶段二 |

---

## 第七部分：V6 展望 — 智能 Agent 协作（可选）

> 该阶段为远期规划，仅在阶段三完成后视需求启动。

### 7.1 核心概念

```
Agent = 大语言模型(LLM) + 工具(Tools) + 记忆(Memory) + 权限(Permissions)
```

**Agent 运行模式**：

| 模式 | 说明 | 适用场景 |
|------|------|----------|
| 自动模式 | Agent 自主决策执行 | 内容审核、SEO 监控 |
| 建议模式 | Agent 给出建议，人确认后执行 | 文章发布、内容分发 |
| 协作模式 | 人 + Agent 实时对话 | 写作助手、数据分析 |
| 审批模式 | 敏感操作需审批 | 删除内容、修改配置 |
| 定时模式 | 按计划执行 | 每日简报、周报生成 |

### 7.2 Agent 类型

| Agent | 职责 | 优先级 |
|-------|------|--------|
| 写作 Agent | 选题调研 → 素材收集 → 写作 → 优化 | P0 |
| 审核 Agent | 多维度内容审核（安全/质量/规范） | P0 |
| 运营 Agent | 内容分发 / 社交推广 / Newsletter | P1 |
| 数据分析 Agent | 数据洞察 / 异常检测 / 趋势预测 | P1 |
| 客服 Agent | 自动回复常见问题 / 升级人工 | P2 |

### 7.3 新增服务

| 服务 | 职责 |
|------|------|
| vellastra-agent-runtime | Agent 运行时引擎（LLM 调用、工具执行、记忆管理） |
| vellastra-agent-orchestrator | Agent 编排引擎（工作流调度、状态管理、重试机制） |
| vellastra-agent-market | Agent 市场（模板管理、发布/安装/评分） |

---

## 附录：开发建议与常见问题

### 关于"CRUD 心里没底"

大多数后端开发对 CRUD 的焦虑来源于**不知道"什么样的 CRUD 算合格"**。以下是每个接口的检查清单：

**接口开发自查清单**：

```
□ 1. 参数校验完整（@Valid + 自定义校验）
□ 2. 权限校验（谁可以操作这个接口？）
□ 3. 业务状态校验（状态机是否允许当前操作？）
□ 4. 关联数据完整性（删除时是否有依赖？）
□ 5. 数据一致性（多表操作是否在同一个事务？）
□ 6. 异常处理（全局异常能否兜底？）
□ 7. 日志记录（关键操作是否写入操作日志？）
□ 8. 防刷/防重（浏览计数防刷、幂等性？）
□ 9. 缓存策略（热点数据是否缓存？）
□ 10. 返回格式统一（Result<T> 包裹）
```

**按这个清单逐个检查，每个接口就不会有遗漏。**

### 建议开发顺序

```
第一阶段（现在开始）
  ├── 先做 auth 认证（登录/注册/Token） → 能登录
  ├── 再做 article CRUD（创建/编辑/列表/发布） → 能发文章
  ├── 再做 category + tag → 能分类
  ├── 再做 file 上传 → 能传图
  ├── 再做 user 管理 → 能管人
  └── 最后做 system config + log + dashboard → 完整可用

第二阶段（第一阶段完成后）
  ├── 先做 comment（评论是社区的基础）
  ├── 再做 column + version + recycle + page
  ├── 再做 analytics + migration + interactive
  ├── 再做 multi-site + watermark
  └── 最后集成中间件（Redis / MQ / ES）

第三阶段（第二阶段完成后）
  ├── 按业务领域拆分为独立微服务
  ├── 引入 Nacos + Gateway + Sentinel
  └── 逐步叠加社区功能
```

### 推荐学习路径

| 当前阶段 | 需要掌握的核心知识点 |
|----------|-------------------|
| 阶段一 | Spring Boot 自动配置 / MyBatis-Plus CRUD / JWT 认证 / DDD 分层 / AOP 切面 |
| 阶段二 | Redis 缓存策略 / RabbitMQ 消息模型 / ES 全文索引 / 读写分离 |
| 阶段三 | 服务注册发现 / 网关路由 / 分布式事务 / 分库分表 / 分布式锁 / 服务监控 |

---

> **文档结束**  
> 版本：v2.0 合并版 | 日期：2026-07-10
```
```
```