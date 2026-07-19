# Phase 2 - 用户管理 (P0) 功能清单

> 分支: `feat/phase2-user-management`
>
> 基于 PRD 需求文档，按依赖关系排列的 Phase 2 开发任务清单。

---

## 总体进度

| 任务 | 描述 | 优先级 | 预估 | 模块 | 进度 |
|------|------|--------|------|------|------|
| T2.1 | 用户分页列表 | P0 | 1.5h | `vellastra-user` | ⏳ 待开始 |
| T2.2 | 用户增删改 | P0 | 2h | `vellastra-user` | ⏳ 待开始 |
| T2.3 | 重置密码 & 修改密码 | P0 | 1.5h | `vellastra-user` / `vellastra-auth` | ⏳ 待开始 |
| T2.4 | 当前用户信息接口 | P0 | 1h | `vellastra-user` | ⏳ 待开始 |
| T2.5 | Feign 远程调用: auth→user | P1 | 2h | `vellastra-auth` → `vellastra-user` | ⏳ 待开始 |

**总预估工时: 8h**

---

## T2.1 用户分页列表

| 属性 | 内容 |
|------|------|
| **优先级** | P0 |
| **依赖** | T1.3（网关鉴权） |
| **预估** | 1.5h |
| **模块** | `vellastra-user` |
| **任务内容** | 创建 UserController，提供分页查询接口：支持 keyword（模糊搜索 username/nickname/email）、status 筛选、分页参数；返回 `PageResult<UserVO>`；UserVO 包含 roleName 字段（通过关联查询或 Feign 调用 auth 模块） |
| **涉及文件** | UserController, UserApplicationService, UserRepository, UserPO, UserVO, PageResult |
| **验收标准** | `GET /api/user/list?current=1&size=10&keyword=admin&status=1` 返回正确分页数据；keyword 搜索准确命中用户名/昵称/邮箱 |

---

## T2.2 用户增删改

| 属性 | 内容 |
|------|------|
| **优先级** | P0 |
| **依赖** | T2.1 |
| **预估** | 2h |
| **模块** | `vellastra-user` |
| **任务内容** | POST 新增用户（密码自动 BCrypt 加密）；PUT 编辑用户信息（昵称/邮箱/手机/头像）；DELETE 逻辑删除（设置 `deleted` 字段）；PATCH 启用/禁用切换 `status` 字段；所有写操作记录 `operation_log` |
| **涉及文件** | UserController, UserApplicationService, CreateUserRequest, UpdateUserRequest, UserPO |
| **验收标准** | CRUD 四个接口均可用；删除后查询不到（逻辑删除生效）；禁用后用户无法登录 |

---

## T2.3 重置密码 & 修改密码

| 属性 | 内容 |
|------|------|
| **优先级** | P0 |
| **依赖** | T2.2 |
| **预估** | 1.5h |
| **模块** | `vellastra-user` / `vellastra-auth` |
| **任务内容** | 管理员重置密码：`PUT /api/user/{id}/reset-password` → 重置为 "123456" 的 BCrypt 加密；用户自助修改密码：`PUT /api/user/password` → 校验旧密码 → 更新新密码（需校验密码强度） |
| **涉及文件** | UserController, UserApplicationService, PasswordRequest DTO |
| **验收标准** | 重置后可用新密码登录；修改密码时旧密码错误则提示；新密码强度不足则拒绝 |

---

## T2.4 当前用户信息接口

| 属性 | 内容 |
|------|------|
| **优先级** | P0 |
| **依赖** | T1.3（网关鉴权注入 X-User-Id） |
| **预估** | 1h |
| **模块** | `vellastra-user` |
| **任务内容** | `GET /api/user/info` → 从请求头 `X-User-Id` 取 userId → 查库返回完整信息（不含密码）；`PUT /api/user/info` → 修改昵称/头像/个人简介 |
| **涉及文件** | UserController, UserApplicationService, UserInfoVO |
| **验收标准** | 登录后调用 info 接口返回当前用户信息；修改后重新查询返回更新后的值 |

---

## T2.5 Feign 远程调用: auth→user

| 属性 | 内容 |
|------|------|
| **优先级** | P1 |
| **依赖** | T2.1 |
| **预估** | 2h |
| **模块** | `vellastra-auth` → `vellastra-user` |
| **任务内容** | `vellastra-auth` 中定义 `UserFeignClient` 接口（`@FeignClient("vellastra-user")`）；`vellastra-user` 提供 internal API（`/internal/user/{id}`）；auth 模块在登录/获取用户信息时通过 Feign 调用 user 服务获取用户名和头像 |
| **涉及文件** | UserFeignClient, UserInternalController, UserInternalService |
| **验收标准** | auth 服务可通过 Feign 调用 user 服务获取用户详情 |

---

## 依赖关系

```
T2.1 ──▶ T2.2 ──▶ T2.3
  │              │
  │              └── 依赖 auth 模块的密码校验能力
  │
  └──▶ T2.4 ── 依赖网关 X-User-Id 请求头
  │
  └──▶ T2.5 ── 依赖 user 模块提供 internal API
```

## 推荐执行顺序

```
T2.1（用户分页列表）→ T2.2（用户增删改）→ T2.3（重置密码）
→ T2.4（当前用户信息）→ T2.5（Feign 远程调用）
```

## 涉及模块

| 模块 | 说明 |
|------|------|
| `vellastra-user` | 主要开发模块，实现全部用户管理功能 |
| `vellastra-auth` | T2.3（密码修改依赖 auth 的密码校验）、T2.5（Feign 客户端定义） |
| `vellastra-common` | 依赖 `PageResult` 等通用组件 |