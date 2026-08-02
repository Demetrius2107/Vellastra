---
name: vellastra-rules
description: Vellastra 项目专属开发规范。当在本项目开发时需要遵循注释规范、模块约定、分支策略、SQL 表规范时使用。
---

# Vellastra 项目开发规范

## 1. 架构规范

### 模块划分
| 模块 | 端口 | 职责 |
|------|------|------|
| vellastra-gateway | 8080 | 网关路由 + JWT 鉴权 |
| vellastra-auth | 8081 | 认证鉴权 + RBAC |
| vellastra-user | 8082 | 用户管理 |
| vellastra-article | 8083 | 文章管理 |
| vellastra-category | 8084 | 分类管理 |
| vellastra-comment | 8085 | 评论管理 |
| vellastra-file | 8086 | 文件上传 |
| vellastra-tag | 8087 | 标签管理 |
| vellastra-publish | 8088 | 发布引擎 |
| vellastra-recycle | 8089 | 内容回收站 |
| vellastra-analytics | 8090 | 数据统计 |
| vellastra-column | 8091 | 专栏专题 |
| vellastra-mail | 8092 | 邮件系统 |
| vellastra-common | — | 公共模块 |

### DDD 分层
```
application → domain → infrastructure → interfaces(facade)
```
- application: 应用服务（事务、编排）
- domain: 实体/值对象/仓储接口
- infrastructure: PO/Mapper/Converter/仓储实现
- interfaces: Controller（只做参数接收与返回）

## 2. 注释规范（强制）

每个类必须有标准类注释：
```java
/**
 * <p>Title: Xxx</p>
 * <p>Description: 一句话职责</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-08-03
 * @updateTime 2026-08-03
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
```
- **每次修改文件必须同步更新 `@updateTime` 为当前日期**
- 每个字段必须有 `/** 描述 */`
- 每个业务方法必须有 `@param` / `@return`

## 3. 分支策略

```
master                    ← 生产（合并需过 CI/PR）
├── feat/<功能描述>       ← 新功能
├── fix/<问题描述>        ← 修复
```
- 不在 master 直接开发
- 功能分支完成后提 PR，过 CI 再合并

## 4. SQL 表规范

- 表名：`t_xxx`（业务表）
- 主键：`id BIGINT AUTO_INCREMENT`
- 必备字段：`create_time` / `update_time` DATETIME
- 逻辑删除：`deleted TINYINT DEFAULT 0` + `@TableLogic`
- 状态字段：`status TINYINT DEFAULT 1`（0禁用 1正常）
- 字符集：utf8mb4

## 5. 技术栈约束

- Java 17 + Spring Boot 3.2.5
- ORM：MyBatis-Plus（LambdaQueryWrapper，禁止 SQL 拼接）
- 密码：BCrypt（禁止 MD5/SHA1）
- Token：JJWT 0.12.5
- 日志：SLF4J，禁止 System.out

## 6. 质量门禁

- [ ] 新模块注册父 POM 并编译通过
- [ ] `mvn test` 不破坏现有测试
- [ ] 注释符合规范且 `@updateTime` 已更新
- [ ] 无硬编码密钥、无 SQL 拼接
