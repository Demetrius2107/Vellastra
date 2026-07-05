# Blog-BackEnd-MS

基于 Spring Cloud Alibaba 的微服务博客后端系统，采用 DDD（领域驱动设计）分层架构。

## 架构概览

```
┌─────────────────────────────────────────────────────────────┐
│                        API Gateway                          │
│                   blog-gateway (Spring Cloud Gateway)        │
└──────┬──────────────┬──────────────┬──────────────┬─────────┘
       │              │              │              │
┌──────▼──────┐ ┌─────▼──────┐ ┌───▼──────┐ ┌───▼──────────┐
│  blog-auth  │ │blog-article│ │blog-user │ │ blog-comment  │
│  鉴权服务    │ │ 文章服务    │ │ 用户服务  │ │  评论服务     │
└──────┬──────┘ └─────┬──────┘ └───┬──────┘ └───┬──────────┘
       │              │              │              │
┌──────▼──────────────▼──────────────▼──────────────▼─────────┐
│                     blog-category (分类服务)                  │
│                     blog-file (文件服务)                      │
│                     blog-publish (发布引擎)                   │
│                     blog-dashboard (数据统计)                 │
│                     blog-system (系统配置)                    │
└──────────────────────────────────────────────────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │      MySQL 8.0      │
                    │      Redis          │
                    │      Nacos          │
                    └─────────────────────┘
```

## 模块说明

| 模块 | 端口 | 说明 | 状态 |
|------|------|------|------|
| blog-article | 8083 | 文章 CRUD、发布、置顶、浏览/点赞 | ✅ |
| blog-category | - | 分类树形 CRUD | ✅ |
| blog-auth | - | 登录/注册/登出/token刷新 | ✅ |
| blog-user | - | 用户资料管理 | ✅ |
| blog-comment | - | 评论/回复/审核 | ❌ |
| blog-file | - | 文件上传/管理 | ❌ |
| blog-gateway | 8080 | API 网关、统一鉴权 | ❌ |
| blog-common | - | 公共依赖（DTO、异常、响应封装） | ✅ |
| blog-publish | - | 发布引擎（CI/CD 触发） | ❌ |
| blog-dashboard | - | 数据仪表盘 | ❌ |

## 技术栈

| 层级 | 技术 |
|------|------|
| 框架 | Spring Boot 3.2.5 / Spring Cloud 2023.0.1 |
| 注册中心 | Nacos Discovery |
| ORM | MyBatis-Plus 3.5.7 |
| 数据库 | MySQL 8.0 |
| 鉴权 | JWT (jjwt 0.12.x) |
| 接口文档 | Knife4j (OpenAPI 3) |
| 构建工具 | Maven |
| Java | 17 |
| 代码风格 | Lombok |

## 快速开始

### 1. 环境准备

- JDK 17+
- MySQL 8.0+
- Nacos 2.x

### 2. 初始化数据库

```bash
mysql -u root -p < data/sql/blog_schema.sql
```

### 3. 启动服务

```bash
# 启动文章服务（其他模块同理）
cd blog-article
mvn spring-boot:run
```

### 4. 访问接口文档

启动任一服务后访问：

```
http://localhost:{port}/doc.html
```

示例：文章服务启动后打开 `http://localhost:8083/doc.html`

## 项目结构（DDD 分层）

```
blog-article/
├── interfaces/          # 接口层（Controller + DTO）
│   ├── facade/         # REST 控制器
│   └── dto/            # 请求/响应对象
├── application/        # 应用服务层（事务编排）
├── domain/             # 领域层
│   ├── entity/         # 领域实体
│   ├── repository/     # 仓储接口
│   └── valueobject/    # 值对象/枚举
└── infrastructure/     # 基础设施层
    ├── persistence/    # 持久化实现
    │   ├── mapper/     # MyBatis Mapper
    │   ├── po/         # 持久化对象
    │   └── converter/  # PO <-> Domain 转换
    └── config/         # 配置
```

## 接口示例

### 文章

```bash
# 创建文章
curl -X POST http://localhost:8083/article \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{"title":"测试文章","content":"正文内容","categoryId":1,"status":0}'

# 文章列表
curl "http://localhost:8083/article?current=1&size=10&categoryId=1"

# 发布文章
curl -X PATCH http://localhost:8083/article/1/publish
```

### 认证

```bash
# 登录
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 刷新 token
curl -X POST http://localhost:8081/auth/refresh \
  -H "Authorization: Bearer {token}"
```

## 开发路线

```
V1（个人博客）          V2（通用 CMS）           V3（UGC 社区）
──────────────         ──────────────           ──────────────
文章 + 分类 + 认证      评论 + 文件 + 权限        问答 + Feed 流
CLI + 静态站           网关 + 仪表盘             热度榜 + 通知
Vue3 管理后台          系统配置                  ES搜索 + 审核
```

详见 `docs/` 目录下的 PRD 文档。

## 类注释规范

```java
/**
 * <p>Title: ClassName</p>
 * <p>Description: 类说明</p>
 * <p>项目名称: Blog-BackEnd-MS</p>
 *
 * @author wanqiu
 * @version 1.0
 * @date 2026年05月17日 首次创建
 * @date 2026年07月05日 最后修改
 *
 * All rights Reserved, Designed By wanqiu
 * @Copyright: 2026
 */
```

## 协议

MIT License
