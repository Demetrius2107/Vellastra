# Phase 1 - 认证鉴权核心 · 剩余任务清单

> 分支: `phase1-remaining`
>
> 本文档基于当前代码检查结果，列出 Phase 1 中尚未完成的子任务。每条任务可独立开发，按依赖关系排列。

---

## 总体进度

| 任务 | 描述 | 进度 | 剩余工作量 |
|------|------|------|-----------|
| T1.1 | BCrypt 密码加密 | ✅ 完成 | 0h |
| T1.2 | JWT Token 完整链路 | ⚠️ 部分完成 | ~1h |
| T1.3 | 网关鉴权过滤器增强 | ⚠️ 部分完成 | ~1h |
| T1.4 | RBAC 数据模型实现 | ✅ 完成 | 0h |
| T1.5 | 用户-角色绑定 | ⚠️ 部分完成 | ~1.5h |
| T1.6 | 接口级别权限注解 | ⚠️ 部分完成 | ~1h |

**剩余总工时: 约 4.5h**

---

## T1.2 JWT Token 完整链路 — 剩余工作

### 当前状态
- `UserDomainService.generateToken()` 已实现，包含 `sub`(userId) 和 `username`
- `refresh()` 已实现
- `jwt.secret` 从 application.yml 读取 ✅
- `jwt.expire-seconds` 在 yml 中配置 (7200)，但代码中**硬编码**了 7200L

### 剩余子任务

#### 1.2.1 从配置读取 Token 有效期

**文件**: `vellastra-auth/src/main/java/.../domain/user/service/UserDomainService.java`

**改动内容**:
- 添加 `@Value("${jwt.expire-seconds:7200}")` 注入 `expireSeconds`
- `generateToken()` 中的 `7200000L` 替换为 `expireSeconds * 1000L`
- `login()` 返回的 `7200L` 改为从配置读取

**验收标准**: 修改 yml 中 `jwt.expire-seconds` 后，Token 有效期随之变化

#### 1.2.2 JWT payload 中增加 roles 字段

**文件**: `vellastra-auth/src/main/java/.../domain/user/service/UserDomainService.java`
**关联文件**: `vellastra-auth/src/main/java/.../application/AuthApplicationService.java`

**改动内容**:
- `generateToken(User user)` 增加从 `UserRoleService.getUserRoleIds(user.getId())` 查询角色列表
- 在 JWT claims 中添加 `roles` 字段（逗号分隔的角色ID字符串，如 "1,2"）
- 注意：`UserDomainService` 是 domain 层服务，不应直接注入 `UserRoleService`（应用层服务）
  - 推荐方案：在 `AuthApplicationService.login()` 中查询角色列表，传入 `generateToken(user, roleIds)`
  - 或修改 `generateToken` 签名，增加 `List<Long> roleIds` 参数

**验收标准**: 解析登录返回的 JWT，可从中提取出 roles 字段

---

## T1.3 网关鉴权过滤器增强 — 剩余工作

### 当前状态
- `AuthGlobalFilter` 已实现，可解析 JWT 并注入 `X-User-Id` / `X-Username` 请求头
- 白名单路径硬编码在代码中
- 未注入 `X-Roles` 请求头（因为 JWT 中还没有 roles）

### 剩余子任务

#### 1.3.1 白名单路径可配置化

**文件**: `vellastra-gateway/src/main/java/.../filter/AuthGlobalFilter.java`

**改动内容**:
- 将 `WHITE_LIST` 改为从配置文件读取
- 在 `application.yml` 中添加 `gateway.white-list` 配置项

**示例配置**:
```yaml
gateway:
  white-list:
    - /auth/login
    - /auth/register
    - /actuator/**
    - /doc.html
    - /v3/api-docs
    - /swagger-ui/**
    - /webjars/**
```

**验收标准**: 修改 yml 后无需改代码即可增减白名单路径

#### 1.3.2 注入 X-Roles 请求头

**文件**: `vellastra-gateway/src/main/java/.../filter/AuthGlobalFilter.java`

**改动内容**:
- 从 JWT claims 中提取 `roles` 字段
- 写入请求头 `X-Roles`

**验收标准**: 携带有效 Token 的请求，下游服务可在请求头中获取 `X-Roles`

---

## T1.5 用户-角色绑定 — 剩余工作

### 当前状态
- `UserRoleService` 已实现 `assignRoles()` 和 `getUserRoleIds()`
- `UserRolePO` / `UserRoleMapper` 已存在
- 但 `AuthApplicationService.login()` 未查询角色、未写入 JWT
- 没有提供用户-角色绑定的 REST API

### 剩余子任务

#### 1.5.1 登录时查询角色列表并写入 JWT

**文件**: `vellastra-auth/src/main/java/.../application/AuthApplicationService.java`
**关联文件**: `vellastra-auth/src/main/java/.../domain/user/service/UserDomainService.java`

**改动内容**:
- `AuthApplicationService.login()` 中调用 `UserRoleService.getUserRoleIds(user.getId())` 获取角色列表
- 将角色列表传给 `UserDomainService.generateToken(user, roleIds)`
- `UserDomainService.generateToken()` 签名增加 `List<Long> roleIds` 参数，写入 JWT claims

**验收标准**: 登录后解析 JWT 可获取 roles 列表

#### 1.5.2 提供用户-角色分配 REST API

**文件**: 新增或修改 Controller

**方案 A**: 在 `AuthController` 中增加
- `GET /auth/user/{userId}/roles` — 查询用户角色ID列表
- `PUT /auth/user/{userId}/roles` — 分配用户角色（全量覆盖）

**方案 B**: 创建独立的 `UserController`（推荐，更符合 REST 风格）

**验收标准**: 可通过 API 为用户分配/移除角色

---

## T1.6 接口级别权限注解 — 剩余工作

### 当前状态
- `@RequirePermission` 注解已定义 ✅
- `RequirePermissionAspect` 切面已实现 ✅
- 但切面依赖 `X-Roles` 和 `X-Perms` 请求头（网关未设置）
- 未实现 Redis 缓存权限数据

### 剩余子任务

#### 1.6.1 打通网关 → 切面链路

**文件**: 
- `vellastra-gateway/.../filter/AuthGlobalFilter.java`
- `vellastra-common/.../config/RequirePermissionAspect.java`

**改动内容**:
- 网关完成 T1.3.2 后，`X-Roles` 请求头已可用
- `RequirePermissionAspect` 从 `X-Roles` 获取角色ID列表
- 根据角色ID查询对应的权限标识列表（菜单 perms 字段）
- 查询方式：通过 Feign 调用 auth 模块接口，或从请求头 `X-Perms` 获取

**验收标准**: 在 Controller 方法上添加 `@RequirePermission("article:create")`，无权限用户返回 403

#### 1.6.2 权限缓存（可选，可延迟到 Phase 7）

**说明**: 当前暂不实现 Redis 缓存，先从请求头获取权限列表。后续 Phase 7 优化。

---

## 推荐执行顺序

```
T1.2.1（有效期配置）
  │
  ▼
T1.2.2（JWT roles）+ T1.5.1（登录查角色）
  │
  ▼
T1.3.1（白名单配置化）
  │
  ▼
T1.3.2（网关 X-Roles）
  │
  ▼
T1.5.2（用户-角色 API）
  │
  ▼
T1.6.1（权限注解链路打通）
```

---

## 涉及模块

| 模块 | 修改文件数 | 说明 |
|------|-----------|------|
| `vellastra-auth` | 3-4 个 | UserDomainService, AuthApplicationService, UserRoleService, Controller |
| `vellastra-gateway` | 1-2 个 | AuthGlobalFilter, application.yml |
| `vellastra-common` | 0-1 个 | RequirePermissionAspect（可能无需修改） |