<p align="center">
  <img src="https://img.shields.io/badge/Java-17-blue?logo=openjdk" alt="Java 17"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?logo=springboot" alt="Spring Boot 3.2"/>
  <img src="https://img.shields.io/badge/MySQL-5.7%20%7C%208.0-orange?logo=mysql" alt="MySQL"/>
  <img src="https://img.shields.io/badge/DDD-Architecture-ff69b4" alt="DDD"/>
  <img src="https://img.shields.io/badge/license-MIT-blue" alt="MIT"/>
</p>

<h1 align="center">✨ Vellastra 内容系统</h1>

<p align="center">
  <strong>Vellastra = vellus（覆盖万物的毯子）+ astra（群星）</strong><br>
  星辰如毡毯覆盖原野 —— 星垂平野阔，月涌大江流<br>
  一套业务与架构同步演进的后端实战项目
</p>

<p align="center">
  <a href="#三阶段演进">三阶段演进</a> ·
  <a href="#模块说明">模块说明</a> ·
  <a href="#快速开始">快速开始</a> ·
  <a href="#项目结构">项目结构</a> ·
  <a href="#开发路线">开发路线</a> ·
  <a href="docs/Vellastra%20完整设计文档（合并版）.md">完整文档</a>
</p>

---

## 📖 项目简介

Vellastra 内容系统是一套以**个人博客 → CMS 内容管理 → 全场景 UGC 知识社区**为业务主线的后端项目，同步完成**单体架构 → 模块化单体 → 微服务分布式架构**的技术升级。

**核心原则**：渐进式无重构。每一阶段完全复用上一阶段的代码与表结构，仅做拓展与升级，无需推翻重写。

### 命名由来

> 出自杜甫《旅夜书怀》："星垂平野阔，月涌大江流。"
>
> *vellus*（覆盖万物的毡毯）+ *astra*（群星）= **Vellastra**，取"星辰如毯，覆盖原野"之意，既体现内容的广度与深度，也传达脚踏实地的工程精神。

---

## 🏗 三阶段演进

| 阶段 | 业务形态 | 架构模式 | 核心技能 | 详细文档 |
|------|----------|----------|----------|----------|
| **阶段一** 🚀 | 个人博客 + CLI + 静态站 | Spring Boot 单体 | 后端基础、RBAC、缓存、文件存储 | [PRD V1](docs/PRD_V1.md) |
| **阶段二** 📊 | 多角色 CMS + 轻量社区 | 模块化单体 + 中间件 | 模块化拆分、MQ、ES、读写分离 | [PRD V2~V4](docs/PRD_V4.md) |
| **阶段三** 🌐 | 类知乎全场景 UGC 社区 | Spring Cloud 微服务 | 分布式事务、服务治理、分库分表 | [PRD V5~V6](docs/PRD_V6.md) |

> 📘 **当前你在这里**：**阶段一**（个人博客），正在搭建核心文章系统与 CLI 工具。
>
> 完整三阶段接口设计见 [`docs/Vellastra 完整设计文档（合并版）.md`](docs/Vellastra%20完整设计文档（合并版）.md)

---

## 📦 模块说明

### 阶段一（当前）

| 模块 | 端口 | 说明 | 状态 |
|------|------|------|------|
| `vellastra-auth` | 8081 | 登录/注册/JWT/RBAC | ✅ |
| `vellastra-user` | 8082 | 用户 CRUD、角色分配 | ✅ |
| `vellastra-article` | 8083 | 文章 CRUD、状态机、标签、置顶、浏览/点赞 | ✅ |
| `vellastra-category` | 8084 | 分类树形 CRUD（最多 3 级） | ✅ |
| `vellastra-comment` | 8085 | 评论/回复/审核（楼中楼） | ✅ |
| `vellastra-file` | — | 文件上传/删除/列表 | ✅ |
| `vellastra-gateway` | 8080 | API 网关、路由转发、JWT 鉴权 | ✅ |
| `vellastra-common` | — | 公共模块（异常、响应、工具） | ✅ |

### 阶段二（规划中）

| 模块 | 说明 |
|------|------|
| `vellastra-publish` | 发布引擎、触发静态站构建 |
| `vellastra-column` | 专栏/专题系统 |
| `vellastra-newsletter` | 邮件订阅 |
| `vellastra-version` | 文章版本管理与对比 |
| `vellastra-recycle` | 内容回收站 |
| `vellastra-page` | 自定义页面 |
| `vellastra-analytics` | 运营数据平台 |
| `vellastra-migration` | 内容迁移工具 |
| `vellastra-interactive` | 投票/问卷互动组件 |
| `vellastra-site` | 多站点管理 |
| `vellastra-watermark` | 内容水印与版权 |

### 阶段三（远期规划）

21 个微服务，覆盖 Feed 推荐流、问答系统、互动通知、排行榜、内容审核、AI 辅助、多平台分发、OAuth、开放 API、SEO、插件系统等。详见[完整设计文档](docs/Vellastra%20完整设计文档（合并版）.md)。

---

## 🛠 技术栈

### 阶段一

| 层级 | 技术 | 版本 |
|------|------|------|
| 框架 | Spring Boot | 3.2.5 |
| ORM | MyBatis-Plus | 3.5.6 |
| 数据库 | MySQL | 5.7 / 8.0 |
| 连接池 | HikariCP | 默认 |
| 缓存 | Redis | 7.0.x 单实例 |
| 鉴权 | JJWT | 0.12.5 |
| 接口文档 | Knife4j (OpenAPI 3) | 4.4.x |
| 构建工具 | Maven | 3.8+ |
| 工具类 | Hutool | 5.8.27 |
| 对象映射 | MapStruct | 1.5.5 |
| 代码风格 | Lombok | 1.18.32 |

### 阶段二新增

| 组件 | 用途 |
|------|------|
| ShardingSphere-JDBC 5.4.x | 读写分离 |
| Caffeine 3.1.x + Redis | 多级缓存 |
| RabbitMQ 3.12.x | 异步消息 |
| Elasticsearch 7.17.x | 全文搜索 |
| XXL-Job 2.4.x | 定时任务 |
| Prometheus + Grafana | 监控 |

### 阶段三新增

| 组件 | 用途 |
|------|------|
| Spring Cloud Alibaba 2023.0.1.0 | 微服务框架 |
| Nacos 2.3.x | 注册配置中心 |
| Spring Cloud Gateway | 服务网关 |
| Sentinel 1.8.7 | 限流熔断 |
| Seata 1.7.1 | 分布式事务 |
| Redisson 3.27.x | 分布式锁 |
| RocketMQ 4.9.x | 事务消息 |
| SkyWalking 9.7.x | 链路追踪 |

---

## 🚀 快速开始

### 1. 环境要求

| 依赖 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | 必需 |
| MySQL | 5.7+ / 8.0 | 必需 |
| Redis | 7.0.x | 可选（阶段一非必需，用于缓存加速） |
| Maven | 3.8+ | 必需 |

### 2. 初始化数据库

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS blog_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 导入表结构
mysql -u root -p blog_db < data/sql/blog_schema.sql
```

### 3. 修改配置

各模块的 `application.yml` 中需修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password    # ← 改为你的密码
```

### 4. 启动服务

按依赖顺序启动：

```bash
# 1. 启动网关（端口 8080）
cd blog-gateway && mvn spring-boot:run

# 2. 启动认证服务（端口 8081）
cd blog-auth && mvn spring-boot:run

# 3. 启动用户服务（端口 8082）
cd blog-user && mvn spring-boot:run

# 4. 启动文章服务（端口 8083）
cd blog-article && mvn spring-boot:run
```

### 5. 验证

```bash
# 登录
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 创建文章（替换 {token} 为上一步返回的 token）
curl -X POST http://localhost:8080/api/article \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{"title":"Hello Vellastra","content":"# 第一篇文章","categoryId":1,"status":0}'
```

> **提示**：所有请求通过网关 `:8080` 统一入口进入，网关会自动路由到对应微服务。

---

## 📐 项目结构

### DDD 四层架构

每个业务模块遵循领域驱动设计（DDD）分层：

```
vellastra-article/
├── interfaces/               # 接口层
│   ├── facade/               #   REST 控制器
│   └── dto/                  #   请求/响应对象
├── application/              # 应用层（事务编排、用例编排）
│   └── *ApplicationService.java
├── domain/                   # 领域层（核心业务逻辑）
│   ├── article/              #   聚合
│   │   ├── entity/           #     领域实体
│   │   ├── repository/       #     仓储接口（端口）
│   │   └── valueobject/      #     值对象/枚举
│   └── service/              #   领域服务
└── infrastructure/           # 基础设施层（技术实现）
    └── persistence/          #   持久化
        ├── mapper/           #     MyBatis Mapper
        ├── po/               #     持久化对象
        └── converter/        #     PO ↔ Domain 转换
```

### 接口规范

- 统一前缀：`/api/`
- 统一响应：`Result<T>` 包裹
- 分页响应：`PageResult<T>`
- 认证方式：JWT Bearer Token（请求头 `Authorization`）
- 用户标识：网关解析 JWT 后注入请求头 `X-User-Id`

---

## 🗺 开发路线

### 阶段一：个人博客（~8 个工作日）

```
Phase 1.1  环境搭建 + 公共模块             4h
Phase 1.2  认证鉴权（登录/注册/JWT/RBAC）   12h
Phase 1.3  用户管理                        8h
Phase 1.4  文章核心（CRUD/状态机/搜索）      14h
Phase 1.5  分类 + 标签 + 文件上传           8h
Phase 1.6  系统配置 + 日志 + 仪表盘         8h
Phase 1.7  CLI 工具 + 静态站               12h
```

### 阶段二：CMS + 内容生态（~6 个工作日）

```
Phase 2.1  评论系统（楼中楼/审核）           8h
Phase 2.2  专栏 + 版本管理 + 回收站 + 页面   12h
Phase 2.3  运营数据 + 内容迁移 + 互动组件    10h
Phase 2.4  多站点 + 水印 + 发布引擎         8h
Phase 2.5  中间件集成（Redis/MQ/ES）        12h
```

### 阶段三：UGC 社区（~30 周）

微服务拆分、Feed 推荐、问答、搜索、AI、开放平台等。

---

## 🔗 相关文档

| 文档 | 说明 |
|------|------|
| [完整设计文档（合并版）](docs/Vellastra%20完整设计文档（合并版）.md) | 三阶段完整接口设计、数据库设计、CLI 规范 |
| [PRD V1](docs/PRD_V1.md) | 个人博客（阶段一） |
| [PRD V2](docs/PRD_V2.md) | 通用 CMS（阶段二基础） |
| [PRD V3](docs/PRD_V3.md) | UGC 社区（阶段三基础） |
| [PRD V4](docs/PRD_V4.md) | 内容生态运营 |
| [PRD V5](docs/PRD_V5.md) | 智能化与开放平台 |
| [PRD V6](docs/PRD_V6.md) | 智能 Agent 协作（远期展望） |
| [二期规划](docs/PHASE2_ARCHITECTURE.md) | 本地写作→后台同步→GitHub Pages 发布 |
| [API 文档](docs/API_DOCUMENT.md) | 接口详情 |
| [数据库脚本](data/sql/blog_schema.sql) | 建表 SQL |

---

## 📄 协议

MIT License

---

<p align="center">
  <sub>Built with ❤️ by wanqiu</sub>
</p>