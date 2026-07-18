# RBAC 权限系统 — 深度讨论总结

> 本文档基于 Vellastra 项目开发过程中关于 RBAC 的系列讨论整理而成，记录了从"为什么需要 RBAC"到"面试怎么讲"的完整思考链路。

---

## 目录

1. [RBAC 的本质](#1-rbac-的本质)
2. [RBAC 到底是不是 CRUD？](#2-rbac-到底是不是-crud)
3. [简单 role 字段 vs 完整 RBAC](#3-简单-role-字段-vs-完整-rbac)
4. [RBAC 实现的核心挑战](#4-rbac-实现的核心挑战)
5. [什么时候不该用 RBAC](#5-什么时候不该用-rbac)
6. [面试怎么讲 RBAC](#6-面试怎么讲-rbac)

---

## 1. RBAC 的本质

**RBAC（Role-Based Access Control）就一句话：**

> "你是谁"决定"你能做什么"

完整的链路是：

```
你登录了 → 系统知道你是谁 → 查你的角色 → 角色绑定了权限 → 决定你能访问哪些接口
```

这个链条里，Role 表和 Menu 表的 CRUD **只是前置准备**，真正的 RBAC 核心在最后一步——**运行时的权限校验**。

### 核心概念

```
用户(User) ──N:N──▶ 角色(Role) ──N:N──▶ 权限/菜单(Menu)
```

| 概念 | 说明 | 示例 |
|------|------|------|
| **用户** | 系统登录账号，可拥有多个角色 | admin（同时是管理员和编辑） |
| **角色** | 权限的集合，一个角色包含多个权限点 | 超级管理员、内容编辑、访客 |
| **权限/菜单** | 最小的操作单元，树形结构 | `article:create`、`article:delete` |

---

## 2. RBAC 到底是不是 CRUD？

### 常见的误解

> "RBAC 不就是建角色、配菜单、给用户分配角色吗？这不还是 CRUD？"

**这个说法只对了一半。** Role 表和 Menu 表的增删改查确实是 CRUD，但这只是 RBAC 的**管理工具**，就像写文章需要先建分类一样——分类的 CRUD 不是目的，用它来组织文章才是。

### RBAC 真正做的事

```
                ┌─────────────────────────────────────┐
                │  @RequirePermission("article:create")│
                │  AOP 切面拦截                        │
                │  ↓                                  │
                │  取 userId → 查 Redis 缓存 → 匹配     │
                │  ↓                                  │
                │  放行 / 403                          │
                └─────────────────────────────────────┘
```

这个**运行时的拦截和校验逻辑**才是 RBAC 的核心。它不是 CRUD，是一个**横跨所有接口的权限校验管道**。

### 去掉 CRUD 后剩下的 4 个核心问题

| # | 问题 | 需要考虑的点 |
|---|------|-------------|
| 1 | **每次请求怎么查权限？** | 缓存 key 设计、失效策略、DB 回源 |
| 2 | **AOP 切面怎么拿请求上下文？** | RequestContextHolder、异步线程、请求头传递 |
| 3 | **没有网关时权限校验在哪做？** | Filter vs Interceptor vs AOP，架构迁移 |
| 4 | **权限标识是字符串，拼错了怎么办？** | 编译期检查、枚举 vs 字符串、规范命名 |

---

## 3. 简单 role 字段 vs 完整 RBAC

| 对比维度 | `sys_user.role` 字段 | 完整 RBAC |
|----------|----------------------|-----------|
| **一个用户能有几个角色？** | 1 个 | N 个（多对多） |
| **新增角色需要改代码吗？** | 要改 if 判断 | 不需要，加数据即可 |
| **权限粒度** | 粗（角色级） | 细（操作级，如 `article:create`） |
| **实现复杂度** | 低，1 个字段 + if 判断 | 高，4 张表 + 注解 + AOP + 缓存 |
| **适合场景** | 个人博客，3 种角色 | 管理后台，10+ 角色，50+ 权限点 |

### 选择建议

```
个人博客（2-3 种角色） →  sys_user.role 字段就够了
小型管理后台           →  轻量 RBAC（角色+权限，不加 AOP）
大型管理后台           →  完整 RBAC（角色+权限+菜单+注解+AOP+缓存）
```

---

## 4. RBAC 实现的核心挑战

### 4.1 权限缓存设计

每次请求都查 DB 显然不行，必须用缓存。

```java
// 缓存 key 设计
auth:perms:{userId}  →  ["article:create", "article:delete", "user:list", ...]

// 缓存读取逻辑
List<String> perms = redis.get("auth:perms:" + userId);
if (perms == null) {
    perms = db.queryPermissions(userId);  // 查 DB 回源
    redis.set("auth:perms:" + userId, perms, 30分钟);
}
```

**缓存失效策略：**

| 策略 | 说明 | 优缺点 |
|------|------|--------|
| **主动失效** | 修改角色权限时，删除相关用户缓存 | 即时生效，但需要知道哪些用户受影响 |
| **被动过期** | 设置 TTL 30 分钟，到期自动重载 | 实现简单，但权限变更有延迟 |
| **双写** | 修改权限时同时更新缓存 | 即时生效，但需要事务保证一致性 |

### 4.2 AOP 切面与请求上下文

```java
@Aspect
@Component
public class RequirePermissionAspect {

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint pjp,
                                   RequirePermission requirePermission) throws Throwable {
        // 从请求头取 userId（网关解析 JWT 后注入）
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes()).getRequest();
        String userId = request.getHeader("X-User-Id");

        // 查缓存
        List<String> perms = redis.get("auth:perms:" + userId);

        // 匹配
        if (!perms.contains(requirePermission.value())) {
            throw new BizException(403, "无权限");
        }

        return pjp.proceed();
    }
}
```

**注意问题：**
- `RequestContextHolder` 是线程绑定的，异步线程里拿不到
- 网关和微服务之间用请求头传递用户信息，需要约定好 header 名称

### 4.3 权限标识的命名规范

权限标识是字符串，拼错了只有跑起来才知道。规范命名是唯一可行的方案：

```
模块:操作

article:create     文章创建
article:edit       文章编辑
article:delete     文章删除
article:list       文章列表

user:list          用户列表
user:assign-role   分配角色

system:role        角色管理
system:menu        菜单管理
```

---

## 5. 什么时候不该用 RBAC

### 适合用简单 role 字段的场景

- 用户角色不超过 3 种
- 权限粒度只需要"能做什么"（写文章、管理用户），不需要"按钮级"控制
- 系统只有管理员和普通用户两种角色

### 适合用其他方案（如 ACL）的场景

- 资源维度的权限控制（如"只能编辑自己的文章"）
- 数据行级别的权限控制（如"只能查看本部门的数据"）
- 这些场景 RBAC 的粗粒度无法满足，需要 ACL（Access Control List）或 ABAC（Attribute-Based Access Control）

---

## 6. 面试怎么讲 RBAC

### 推荐话术框架

> 问题 → 方案 → 落地 → 取舍

### 第一步：说问题（30 秒）

> "我们系统一开始权限控制很简单，就是在 `sys_user` 表里加了一个 `role` 字段，1=管理员，2=撰稿人，3=访客。每个接口里写 `if (user.getRole() == 1)` 来判断。后来发现两个问题：
>
> **第一**，一个用户可能既是撰稿人又是某个栏目的审核员，`role` 一个字段没法表示多角色。
> **第二**，新增一个权限点（比如"审核评论"），要去改所有接口的 if 判断，非常容易漏。"

### 第二步：说方案（40 秒）

> "所以改成了 RBAC 模型，核心是**用户 N:N 角色 N:N 权限**这三层关系。用户登录后，后台查他有哪些角色，这些角色绑定了哪些权限标识（比如 `article:create`、`comment:audit`），把权限列表缓存到 Redis。然后在每个需要权限的接口上加一个 `@RequirePermission("article:create")` 注解，用 AOP 切面统一拦截，从 Redis 里取权限列表做匹配。"

### 第三步：说落地细节（40 秒）

> "实现的时候有几个关键点：
>
> **权限缓存**：用户登录后把他的权限标识列表写入 Redis，key 是 `auth:perms:{userId}`，这样每次请求不用查 DB。如果管理员改了角色权限，需要主动清除相关用户的缓存。
>
> **AOP 拦截**：自定义 `@RequirePermission` 注解，用 `@Around` 切面拦截。切面里从请求头取 `X-User-Id`（网关解析 JWT 后注入的），查 Redis 匹配权限标识。
>
> **菜单树**：权限管理页面需要一个树形菜单，查全表后在内存里递归构建，参考了分类树的实现。"

### 第四步：说取舍（30 秒）

> "不过 RBAC 也有它的局限：
>
> - **数据量大的时候**，每次请求都要查一次 Redis，虽然比查 DB 快，但还是有开销。如果对延迟敏感，可以考虑把权限列表塞进 JWT 的 claims 里，省掉一次 Redis 查询，但 JWT 会变大，而且权限改了要等 Token 过期才生效。
> - **权限标识是字符串**，拼错了只有跑起来才知道。我们当时考虑过用枚举代替字符串，但枚举没法动态扩展，最后用了字符串+规范命名（`模块:操作`）。
> - **如果只是个人博客**，两三个角色，用 `role` 字段 + `if` 判断就够了，上 RBAC 属于过度设计。"

### 面试官可能追问的问题

**Q1：权限改了怎么让缓存失效？**

> 主动失效策略。管理员修改角色权限后，删除相关用户的 Redis 缓存。下次请求时缓存没有，就查 DB 重新加载。如果用户量很大，可以用 Redis 的 `scan` 命令批量匹配删除，避免用 `keys` 阻塞。

**Q2：为什么不用 Spring Security？**

> Spring Security 功能很强大，但对我们来说太重了。我们只需要一个轻量的权限校验，自己写一个 `@RequirePermission` 注解 + AOP 切面，总共不到 100 行代码，完全可控，没有配置复杂度。而且我们用了 DDD 架构，不希望 framework 层的注解侵入到 domain 层。

**Q3：JWT 里可以直接放权限吗？**

> 可以，把权限标识列表塞进 JWT 的 claims 里，这样网关解析 JWT 后就能直接拿到权限，不需要查 Redis。但缺点是：① JWT 体积会变大，每次请求都携带；② 权限改了要等 Token 过期才生效，不适合权限变更频繁的场景。我们选择放 Redis 是因为权限变更能即时生效。

---

## 总结

RBAC 的核心不是 CRUD，而是**运行时的权限校验管道**。去掉 Role 和 Menu 的增删改查后，剩下的 4 个问题（缓存、AOP 上下文、架构迁移、字符串校验）才是真正需要思考的技术点。面试时按"问题→方案→落地→取舍"的框架讲，比背诵概念更能体现深度。