<p align="center">
  <img src="https://img.shields.io/badge/Java-17-blue?logo=openjdk" alt="Java 17"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?logo=springboot" alt="Spring Boot 3.2"/>
  <img src="https://img.shields.io/badge/Spring%20Cloud-2023.0.1-blueviolet?logo=spring" alt="Spring Cloud 2023"/>
  <img src="https://img.shields.io/badge/DDD-Architecture-ff69b4" alt="DDD"/>
  <img src="https://img.shields.io/badge/license-MIT-blue" alt="MIT"/>
  <img src="https://img.shields.io/badge/state-development-yellow" alt="State: Development"/>
</p>

<h1 align="center">Vellastra Content System</h1>
<h3 align="center">星垂平野阔 · 月涌大江流</h3>

<p align="center">
  <strong>一套业务与架构同步演进的后端实战系统</strong><br />
  个人博客 → CMS 内容管理 → 全场景 UGC 知识社区
</p>

<p align="center">
  <a href="#项目概述">项目概述</a> ·
  <a href="#三阶段演进路线">三阶段演进</a> ·
  <a href="#系统架构">系统架构</a> ·
  <a href="#模块说明">模块说明</a> ·
  <a href="#技术栈">技术栈</a> ·
  <a href="#快速开始">快速开始</a> ·
  <a href="#项目结构">项目结构</a> ·
  <a href="#开发路线">开发路线</a> ·
  <a href="#相关文档">相关文档</a>
</p>

---

## 项目概述

### 项目定位

Vellastra 内容系统是一套面向个人/团队的内容管理后端解决方案，以「渐进式架构演进」为核心设计理念，在业务迭代的同时完成技术架构的逐步升级。项目拒绝一次性过度设计，每个阶段的功能复杂度与架构难度严格匹配，开发者可以小步快跑式落地，全程无需推翻重构。

### 设计原则

| 原则 | 说明 |
|------|------|
| **渐进式无重构** | 下一阶段完全复用上一阶段的代码与表结构，仅做拓展与升级，核心逻辑无需重写 |
| **生产级细节** | 所有接口与表设计均参考大厂生产规范，覆盖异常处理、边界场景、风控安全等玩具项目缺失的环节 |
| **面试导向** | 每个核心场景都对应后端高频面试考点，项目落地过程同步完成面试考点的实战理解 |
| **联邦特色** | 贯穿全项目的星垂野联邦体系，作为分布式能力的练习载体，从入门到进阶逐步升级 |

### 命名由来

> 出自杜甫《旅夜书怀》："星垂平野阔，月涌大江流。"
>
> *vellus*（覆盖万物的毡毯）+ *astra*（群星）= **Vellastra**，取"星辰如毯，覆盖原野"之意，既体现内容的广度与深度，也传达脚踏实地的工程精神。

---

## 三阶段演进路线

| 阶段 | 业务形态 | 架构模式 | 核心技能目标 | 适用人群 |
|------|----------|----------|--------------|----------|
| **阶段一** 🚀 | 个人博客 + 星垂野联邦 | Spring Boot 单体架构 | 后端基础规范、RBAC 权限、缓存、对象存储、分布式接口通信与幂等性设计 | 初中级后端工程师 |
| **阶段二** 📊 | 多角色 CMS + 轻量文字社区 | 模块化单体 + 中间件拓展 | 复杂业务模块化拆分、读写分离、异步消息队列、全文检索、缓存一致性 | 中高级后端工程师 |
| **阶段三** 🌐 | 类知乎全场景 UGC 知识社区 | Spring Cloud 微服务分布式架构 | 分布式事务、分库分表、服务治理、高可用容灾、推荐/广告中间件设计 | 高级/架构师 |

> 📘 **当前阶段：** 阶段一（个人博客），正在搭建核心文章系统与 CLI 工具。
>
> 完整三阶段接口设计见 [`docs/Vellastra 完整设计文档（合并版）.md`](docs/Vellastra%20完整设计文档（合并版）.md)

---

## 系统架构

### 架构总览

```
┌──────────────────────────────────────────────────────────────────┐
│                         客户端层                                  │
│              前端（Vue3/React） / 移动端 / 第三方                    │
└──────────────────────────┬───────────────────────────────────────┘
                           │ HTTP/HTTPS
┌──────────────────────────▼───────────────────────────────────────┐
│                       API 网关层                                   │
│   vellastra-gateway (:8080)                                       │
│   └─ 路由转发 │ JWT 鉴权 │ 白名单 │ 限流熔断                        │
└───┬───────────┬───────────┬───────────┬───────────┬──────────────┘
    │           │           │           │           │
┌───▼────┐ ┌───▼────┐ ┌───▼────┐ ┌───▼────┐ ┌───▼────┐
│ auth   │ │ user   │ │article │ │category│ │comment │
│ :8081  │ │ :8082  │ │ :8083  │ │ :8084  │ │ :8085  │
└────────┘ └────────┘ └────────┘ └────────┘ └────────┘

┌──────────────────────────────────────────────────────────────────┐
│                       基础设施层                                   │
│   Nacos 2.3.x (:8848) │ MySQL 8.0 (:3306) │ Redis 7.0.x (:6379)│
│   MinIO / 本地存储 │ RabbitMQ 3.12.x │ Elasticsearch 7.17.x     │
└──────────────────────────────────────────────────────────────────┘
```

### 服务职责划分

| 服务 | 端口 | 职责 | 数据库表 |
|------|------|------|----------|
| vellastra-gateway | 8080 | 统一入口、路由转发、JWT 鉴权过滤器 | 无 |
| vellastra-auth | 8081 | 登录/注册/JWT 签发/Token 刷新 | sys_user, t_role, t_menu, t_user_role, t_role_menu |
| vellastra-user | 8082 | 用户信息管理/角色分配 | sys_user |
| vellastra-article | 8083 | 文章全生命周期管理 | blog_article, blog_article_tag, blog_tag |
| vellastra-category | 8084 | 分类树形管理 | blog_category |
| vellastra-comment | 8085 | 评论/回复/审核 | blog_comment |
| vellastra-file | — | 文件上传/删除/列表 | sys_media |
| vellastra-common | — | 公共模块（异常处理、统一响应、工具类、配置） | — |

---

## 模块说明

### 当前阶段（Phase 1 — 个人博客）

| 模块 | 端口 | 状态 | 说明 |
|------|------|------|------|
| `vellastra-common` | — | ✅ 已完成 | 共享底座：异常体系、统一响应体、用户上下文、全局常量、基础配置、工具类、枚举、注解 |
| `vellastra-auth` | 8081 | ✅ 已完成 | 认证鉴权：登录/注册/logout/Token 刷新、BCrypt 密码加密、JWT 签发与校验 |
| `vellastra-user` | 8082 | ✅ 已完成 | 用户管理：CRUD、分页搜索、启用/禁用、重置密码、角色分配 |
| `vellastra-article` | 8083 | ✅ 已完成 | 文章系统：CRUD、状态机（草稿/发布/下架）、置顶、浏览计数防刷、点赞 toggle、批量操作 |
| `vellastra-category` | 8084 | ✅ 已完成 | 分类管理：树形结构 CRUD、三级分类、排序 |
| `vellastra-comment` | 8085 | ✅ 已完成 | 评论系统：发表/回复/楼中楼、审核状态机、分页查询 |
| `vellastra-file` | — | ✅ 已完成 | 文件管理：上传/删除/列表、多存储类型支持 |
| `vellastra-gateway` | 8080 | ✅ 已完成 | API 网关：路由转发、JWT 鉴权过滤器、白名单 |

### 后续阶段（规划中）

| 阶段 | 模块 | 说明 |
|------|------|------|
| 阶段二 | `vellastra-publish` | 发布引擎、触发静态站构建 |
| 阶段二 | `vellastra-column` | 专栏/专题系统 |
| 阶段二 | `vellastra-newsletter` | 邮件订阅 |
| 阶段二 | `vellastra-version` | 文章版本管理与对比 |
| 阶段二 | `vellastra-recycle` | 内容回收站 |
| 阶段二 | `vellastra-analytics` | 运营数据平台 |
| 阶段三 | 21 个微服务 | 推荐系统、问答、AI 辅助、开放平台等 |

---

## 技术栈

### 阶段一（当前）

| 层级 | 技术选型 | 版本 | 说明 |
|------|----------|------|------|
| **核心框架** | Spring Boot | 3.2.5 | 适配 JDK 17，支持虚拟线程 |
| **微服务** | Spring Cloud Alibaba | 2023.0.1.0 | 注册配置中心 + 服务网关 |
| **注册中心** | Nacos | 2.3.x | 服务发现与配置管理 |
| **ORM** | MyBatis-Plus | 3.5.6 | 代码生成器、分页插件、逻辑删除 |
| **数据库** | MySQL | 8.0 | 核心存储，utf8mb4 字符集 |
| **连接池** | HikariCP | 默认 | 性能最优的连接池 |
| **缓存** | Redis | 7.0.x 单实例 | 缓存热点数据 + Token |
| **鉴权** | JJWT | 0.12.5 | JWT 令牌生成与校验 |
| **接口文档** | Knife4j (OpenAPI 3) | 4.5.0 | Swagger 增强版，在线调试 |
| **密码加密** | Spring Security Crypto | 随 Boot | BCrypt 密码编码器 |
| **工具类** | Hutool | 5.8.27 | 通用工具集 |
| **对象映射** | MapStruct | 1.5.5 | 编译期 PO ↔ Domain 转换 |
| **代码简化** | Lombok | 1.18.32 | @Data / @Builder / @Slf4j |
| **构建工具** | Maven | 3.8+ | 多模块项目管理 |

### 阶段二（规划新增）

| 组件 | 用途 | 版本 |
|------|------|------|
| ShardingSphere-JDBC | 读写分离 | 5.4.x |
| Caffeine + Redis | 多级缓存 | 3.1.x + 7.0.x |
| RabbitMQ | 异步消息 | 3.12.x |
| Elasticsearch | 全文搜索 | 7.17.x |
| XXL-Job | 分布式定时任务 | 2.4.x |
| Prometheus + Grafana | 监控 | 最新稳定版 |

### 阶段三（规划新增）

| 组件 | 用途 | 版本 |
|------|------|------|
| Seata | 分布式事务 | 1.7.1 |
| Sentinel | 限流熔断 | 1.8.7 |
| Redisson | 分布式锁 | 3.27.x |
| RocketMQ | 事务消息 | 4.9.x |
| SkyWalking | 链路追踪 | 9.7.x |
| Kubernetes | 容器编排 | 1.28+ |

---

## 快速开始

### 环境要求

| 依赖 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | 必需，推荐使用 Microsoft OpenJDK 17 LTS |
| Maven | 3.8+ | 必需，多模块项目构建 |
| MySQL | 5.7+ / 8.0 | 必需，推荐 8.0 |
| Redis | 7.0.x | 可选，阶段一非必需，用于缓存加速 |
| Nacos | 2.3.x | 可选，本地开发可跳过 |

### 1. 克隆项目

```bash
git clone https://github.com/your-org/vellastra.git
cd vellastra
```

### 2. 初始化数据库

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS vellastra \
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 导入完整表结构
mysql -u root -p vellastra < data/sql/phase1_full_schema.sql
```

### 3. 修改配置

各模块 `src/main/resources/application.yml` 中需修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/vellastra?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password    # ← 修改为你的数据库密码
```

### 4. 编译项目

```bash
# 编译全部模块
mvn clean compile -DskipTests
```

### 5. 启动服务

按依赖顺序启动（每个服务在独立终端中运行）：

```bash
# 1. 启动网关（端口 8080）
mvn spring-boot:run -pl vellastra-gateway

# 2. 启动认证服务（端口 8081）
mvn spring-boot:run -pl vellastra-auth

# 3. 启动用户服务（端口 8082）
mvn spring-boot:run -pl vellastra-user

# 4. 启动文章服务（端口 8083）
mvn spring-boot:run -pl vellastra-article

# 5. 启动分类服务（端口 8084）
mvn spring-boot:run -pl vellastra-category

# 6. 启动评论服务（端口 8085）
mvn spring-boot:run -pl vellastra-comment
```

### 6. 验证服务

```bash
# 登录获取 Token
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 预期响应
# {"code":200,"message":"success","data":{"token":"eyJhbGci...","expireIn":7200}}
```

### 7. 访问接口文档

启动任一服务后，访问 Knife4j 文档页面：

```
http://localhost:8083/doc.html
```

---

## 项目结构

### 多模块组织

```
vellastra/                          # 根项目
├── vellastra-common/               # 公共模块（共享底座）
├── vellastra-gateway/              # API 网关
├── vellastra-auth/                 # 认证鉴权服务
├── vellastra-user/                 # 用户管理服务
├── vellastra-article/              # 文章服务
├── vellastra-category/             # 分类服务
├── vellastra-comment/              # 评论服务
├── vellastra-file/                 # 文件服务
├── vellastra-cli/                  # CLI 工具（Node.js）
├── docs/                           # 设计文档
├── data/                           # 数据脚本
│   └── sql/                        #   建表 DDL
└── pom.xml                         # 父 POM（版本管理）
```

### DDD 四层架构

每个业务模块遵循领域驱动设计（DDD）分层规范：

```
vellastra-article/
├── interfaces/                    # 接口层：负责接收请求、响应输出
│   ├── facade/                    #   REST 控制器
│   └── dto/                       #   请求/响应 DTO
├── application/                   # 应用层：事务编排、用例协调
│   └── *ApplicationService.java
├── domain/                        # 领域层：核心业务逻辑（不依赖框架）
│   └── article/                   #   聚合
│       ├── entity/                #     领域实体（聚合根）
│       ├── repository/            #     仓储接口（端口定义）
│       └── valueobject/           #     值对象/枚举
└── infrastructure/                # 基础设施层：技术实现
    └── persistence/               #   持久化实现
        ├── mapper/                #     MyBatis Mapper 接口
        ├── po/                    #     持久化对象（表映射）
        └── converter/             #     PO ↔ Domain 转换器
```

### 接口规范

| 规范 | 说明 |
|------|------|
| 统一前缀 | `/api/` |
| 统一响应 | `Result<T>` 包裹（code + message + data） |
| 分页响应 | `PageResult<T>`（records + total + current + size + pages） |
| 认证方式 | JWT Bearer Token，请求头 `Authorization` |
| 用户标识 | 网关解析 JWT 后注入请求头 `X-User-Id` / `X-Username` |
| 错误编码 | 全局错误码枚举 `ErrorCode`，按模块分区（1xxx=用户, 2xxx=Token, 3xxx=文章, 4xxx=分类, 5xxx=评论） |
| 异常处理 | `GlobalExceptionHandler` 统一拦截，区分业务异常、参数校验、系统异常 |

---

## 开发路线

### 阶段一：个人博客 + 星垂野联邦（~8 个工作日）

| 阶段 | 任务 | 工时 | 产出物 |
|------|------|------|--------|
| Phase 0 | 环境搭建 + 公共模块 | 4h | 项目可编译启动 |
| Phase 1 | 认证鉴权核心（BCrypt/JWT/RBAC） | 12h | 登录注册 + 权限体系 |
| Phase 2 | 用户管理 | 8h | 用户 CRUD + 角色分配 |
| Phase 3 | 文章核心（CRUD/状态机/搜索/置顶/浏览/点赞） | 14h | 文章全生命周期 |
| Phase 4 | 分类 + 标签 + 文件上传 | 8h | 辅助功能 |
| Phase 5 | 评论互动 | 8h | 评论/回复/审核 |
| Phase 6 | 统计与日志 | 8h | 仪表盘 + AOP 日志 |
| Phase 7 | 非功能增强 | 10h | Redis/限流/文档/Docker |

### 阶段二：CMS + 内容生态（~6 个工作日）

专栏系统、版本管理、回收站、发布引擎、运营数据、多站点、中间件集成等。

### 阶段三：UGC 社区（~30 周）

微服务拆分、Feed 推荐流、问答系统、AI 辅助、开放平台等。

---

## 数据库设计

### 阶段一数据表（共 12 张）

| 分组 | 表名 | 说明 |
|------|------|------|
| **核心业务** | `sys_user` | 用户表（含角色/登录IP/时间） |
| | `blog_article` | 文章表（含 SEO/统计字段，FULLTEXT 索引） |
| | `blog_category` | 分类表（三级树形结构） |
| | `blog_tag` | 标签表（按使用次数排序） |
| | `blog_article_tag` | 文章标签关联表 |
| | `blog_comment` | 评论表（楼中楼 + 审核状态机） |
| | `sys_media` | 媒体资源表（本地/MinIO/OSS 三种存储） |
| **联邦专属** | `fed_node` | 联邦节点表（心跳/同步范围） |
| | `fed_sync_record` | 同步记录表（request_id 幂等） |
| **系统基础** | `sys_operate_log` | 操作日志表（AOP 自动写入） |
| | `sys_login_log` | 登录日志表（安全审计） |
| | `sys_config` | 系统配置表（键值对分组） |

> 完整 DDL 见 [`data/sql/phase1_full_schema.sql`](data/sql/phase1_full_schema.sql)

---

## 相关文档

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
| [数据库脚本](data/sql/phase1_full_schema.sql) | 建表 SQL |
| [开发计划](docs/DEVELOPMENT_PLAN.md) | 详细任务排期 |

---

## 贡献指南

### 开发规范

1. **分支策略**：`main`（稳定）→ `develop`（开发）→ `feat-*`（功能分支）
2. **代码风格**：遵循 Alibaba Java Coding Guidelines
3. **提交规范**：使用 Conventional Commits 格式（`feat:` / `fix:` / `refactor:` / `chore:` / `test:` / `docs:`）
4. **DDD 约束**：domain 层不允许依赖 framework 层（Spring 注解等）

### 本地开发

```bash
# 安装依赖
mvn clean install -DskipTests

# 运行全部测试
mvn test

# 构建并打包
mvn package -DskipTests
```

---

## 许可证

[MIT License](LICENSE)

---

<p align="center">
  <sub>Built with ❤️ by wanqiu</sub>
  <br />
  <sub>Copyright © 2026 wanqiu. All rights reserved.</sub>
</p>