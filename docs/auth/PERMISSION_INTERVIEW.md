# 权限管理 — 面试八股文整理

> 覆盖后端面试中权限管理相关的高频问题，从基础到深入，每个问题附带"回答要点"和"面试官追问"。

---

## 目录

1. [基础篇](#1-基础篇)
2. [进阶篇](#2-进阶篇)
3. [架构篇](#3-架构篇)
4. [场景题](#4-场景题)

---

## 1. 基础篇

### Q1：RBAC 是什么？它的核心模型是怎样的？

**回答要点：**

RBAC（Role-Based Access Control）基于角色的访问控制，核心思想是**权限不直接分配给用户，而是通过角色作为中间层**。

```
用户(User) ──N:N──▶ 角色(Role) ──N:N──▶ 权限(Permission)
```

三张核心关联表：

| 表 | 作用 |
|------|------|
| `t_user_role` | 用户-角色关联 |
| `t_role_permission` | 角色-权限关联 |
| `t_permission` | 权限定义表 |

**追问：为什么不用直接给用户分配权限，非要加一个"角色"中间层？**

> 解耦。如果直接给 1000 个用户分配权限，改一个权限要改 1000 条记录。有了角色，只需要改角色和权限的关联，所有属于该角色的用户自动生效。这就是"角色"作为中间层的价值。

### Q2：RBAC 和 ACL 有什么区别？

**回答要点：**

| 对比维度 | RBAC | ACL（Access Control List） |
|----------|------|---------------------------|
| **控制粒度** | 角色级 | 资源级（每个资源都有独立的访问列表） |
| **管理成本** | 低，通过角色批量管理 | 高，每个资源都要维护 ACL |
| **灵活性** | 中，适合通用权限 | 高，适合细粒度控制 |
| **典型场景** | 管理后台、企业系统 | 文件系统、云存储权限 |
| **实现复杂度** | 低 | 高 |

**一句话总结：** RBAC 管"你能做什么"（角色决定操作权限），ACL 管"你能动哪些数据"（资源决定访问权限）。

### Q3：RBAC 和 ABAC 有什么区别？

**回答要点：**

ABAC（Attribute-Based Access Control）基于属性的访问控制，比 RBAC 更灵活。

| 对比维度 | RBAC | ABAC |
|----------|------|------|
| **判断依据** | 角色 | 任意属性（用户属性、资源属性、环境属性） |
| **规则表达** | 静态：拥有 role=admin 即可 | 动态：user.department == resource.department |
| **灵活性** | 低 | 高 |
| **实现复杂度** | 低 | 高 |
| **性能** | 好（查缓存即可） | 差（需要实时计算规则） |

**示例：**"经理可以查看本部门员工的薪资"

- RBAC：给"经理角色"分配"查看薪资"权限 → 所有经理都能看到全公司薪资 ❌
- ABAC：规则 `user.department == resource.department AND user.role == "manager"` → 只看到本部门 ✅

---

## 2. 进阶篇

### Q4：权限校验缓存怎么设计？权限改了怎么让缓存失效？

**回答要点：**

**缓存设计：**

```java
// 缓存 key
auth:perms:{userId}  →  ["article:create", "article:delete", ...]

// 读取逻辑（Cache-Aside 模式）
List<String> perms = redis.get(key);
if (perms == null) {
    perms = db.queryPermissions(userId);  // 回源 DB
    redis.set(key, perms, 30分钟);         // 写入缓存
}
return perms;
```

**失效策略：**

| 策略 | 做法 | 优点 | 缺点 |
|------|------|------|------|
| **主动失效** | 改权限时删缓存 | 即时生效 | 需要知道哪些用户受影响 |
| **被动过期** | 设 TTL 到期自动重载 | 实现简单 | 有延迟窗口 |
| **双写** | 改权限时同步更新缓存 | 即时生效 | 需要保证 DB 和缓存一致性 |

**推荐方案：** 主动失效 + 被动过期兜底。改权限时主动删除相关用户缓存，即使删除失败，TTL 到期后也会自动刷新。

### Q5：JWT 里放权限好还是放 Redis 好？

**回答要点：**

| 对比维度 | JWT 存放 | Redis 存放 |
|----------|----------|------------|
| **查询耗时** | 0（直接解析 Token） | 1 次 Redis 查询（<1ms） |
| **权限即时性** | ❌ 需等 Token 过期 | ✅ 即时生效 |
| **JWT 体积** | 变大（每次请求携带） | 不变 |
| **实现复杂度** | 低 | 中 |
| **服务端状态** | 无状态 | 有状态 |

**结论：**
- 权限变更不频繁（如企业内网系统）→ **JWT 存放**，省一次 Redis 查询
- 权限变更频繁（如 SaaS 平台）→ **Redis 存放**，权限即时生效，不受 Token 生命周期限制

### Q6：为什么不用 Spring Security 的权限管理？

**回答要点：**

Spring Security 功能强大，但有以下问题：

1. **配置复杂**：`SecurityFilterChain`、`AuthenticationManager`、`UserDetailsService` 等概念多，学习成本高
2. **侵入性强**：需要实现 Spring Security 的接口，耦合了 framework 层代码
3. **DDD 约束冲突**：在 DDD 架构中，domain 层不应该依赖 Spring 框架注解
4. **过度设计**：对于不需要 OAuth2、SSO、RememberMe 等功能的项目，Spring Security 大部分功能用不上

**替代方案：** 自定义 `@RequirePermission` 注解 + AOP 切面，不到 100 行代码，完全可控。

### Q7：@RequirePermission 注解的 AOP 切面怎么实现？

**回答要点：**

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    String value();  // 权限标识，如 "article:create"
}

@Aspect
@Component
public class RequirePermissionAspect {

    @Around("@annotation(requirePermission)")
    public Object check(ProceedingJoinPoint pjp,
                         RequirePermission requirePermission) throws Throwable {
        // 1. 获取当前用户 ID
        HttpServletRequest request = ((ServletRequestAttributes)
            RequestContextHolder.getRequestAttributes()).getRequest();
        String userId = request.getHeader("X-User-Id");

        // 2. 从 Redis 查用户权限列表
        List<String> perms = redisTemplate.opsForList()
            .range("auth:perms:" + userId, 0, -1);

        // 3. 匹配权限
        if (!perms.contains(requirePermission.value())) {
            throw new BizException(403, "无权限访问");
        }

        // 4. 放行
        return pjp.proceed();
    }
}
```

**需要注意：**
- `RequestContextHolder` 是线程绑定的，异步线程里拿不到
- 需要配合网关在请求头中注入 `X-User-Id`
- 权限列表建议用 `Set` 或 `List` 存储，匹配时 `O(1)` 或 `O(n)`

---

## 3. 架构篇

### Q8：微服务架构下权限校验应该在网关层做还是各个服务自己做？

**回答要点：**

**分层处理：**

```
网关层（Gateway）           各微服务（Service）
    │                            │
    │  JWT 解析                  │
    │  Token 有效性校验           │
    │  提取 userId/username      │
    │  写入请求头                │
    │  白名单放行                │
    │                            │
    │  转发请求（带请求头）──────▶ │  接收请求头
    │                            │  @RequirePermission 注解校验
    │                            │  业务权限判断
    │                            │  （如：只能编辑自己的文章）
```

**职责划分：**

| 职责 | 放在哪层 | 原因 |
|------|----------|------|
| Token 校验 | 网关 | 统一处理，避免每个服务重复实现 |
| 身份提取 | 网关 | 解析 JWT 后注入请求头 |
| 白名单 | 网关 | 集中管理无需认证的路径 |
| 权限匹配 | 各服务 | 权限规则是业务逻辑，网关不感知业务 |
| 资源级权限 | 各服务 | 如"只能编辑自己的文章"，网关无从判断 |

**为什么不把权限校验全放网关？**

网关是流量入口，如果把所有权限规则都放在网关，会导致：
1. 网关规则膨胀，每次新增接口都要改网关配置
2. 网关需要感知业务逻辑（如"谁能编辑这篇文章"），职责不单一
3. 网关成为性能瓶颈，权限校验逻辑会阻塞请求转发

### Q9：权限数据怎么在微服务之间传递？

**回答要点：**

**方案一：请求头传递（推荐）**

```
网关解析 JWT → 提取 userId, username, roles
              → 写入请求头 X-User-Id, X-Username, X-Roles
              → 转发到下游服务
```

**方案二：Token 透传**

```
服务 A 收到请求 → 携带原 Token 调用服务 B
              → 服务 B 自己解析 Token 取用户信息
```

**方案三：上下文传递（Feign 拦截器）**

```java
@Component
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        // 从当前请求中取出用户信息，透传到下游服务
        ServletRequestAttributes attrs = (ServletRequestAttributes)
            RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            template.header("X-User-Id",
                attrs.getRequest().getHeader("X-User-Id"));
        }
    }
}
```

### Q10：数据库层面的权限控制怎么做？比如"只能看到本部门的数据"

**回答要点：**

这是**数据行级权限**，RBAC 无法直接解决，需要结合数据过滤。

**方案一：WHERE 条件拼接（推荐）**

```java
// Service 层
public PageResult<ArticleVO> listArticles(Long current, Long size, Long userId) {
    // 普通用户只能看到自己的文章
    // 管理员可以看到所有文章
    LambdaQueryWrapper<ArticlePO> wrapper = new LambdaQueryWrapper<>();
    if (!isAdmin(userId)) {
        wrapper.eq(ArticlePO::getAuthorId, userId);
    }
    // ... 执行查询
}
```

**方案二：MyBatis-Plus 数据权限插件**

```java
@InterceptorIgnore(dataPermission = "true")
// 或者自定义 DataPermissionHandler 拦截器
```

**方案三：字段冗余 + 部门表**

在设计表结构时，给数据表加上 `dept_id` 字段，查询时根据用户的部门 ID 过滤。

---

## 4. 场景题

### Q11：如果一个用户有多个角色，权限冲突了怎么办？

**回答要点：**

**权限合并策略：** 用户的最终权限 = 所有角色权限的**并集**。

```
用户 A 的角色：
    角色1（管理员）：article:create, article:delete, user:list
    角色2（编辑）：  article:create, article:edit
    最终权限：       article:create, article:delete, article:edit, user:list
```

**交集 vs 并集的选择：**

| 策略 | 含义 | 适用场景 |
|------|------|----------|
| **并集（OR）** | 只要有一个角色有权限，就有权限 | 大部分通用场景 |
| **交集（AND）** | 所有角色都有权限，才有权限 | 安全敏感场景（如提现审批） |
| **优先级** | 指定角色优先级，高优先级覆盖低优先级 | 需要精细控制的场景 |

### Q12：如何设计一个支持"菜单可见"和"按钮可用"两级权限的系统？

**回答要点：**

**表结构设计：**

```sql
t_menu:
    id          BIGINT
    parent_id   BIGINT          -- 父菜单ID
    menu_name   VARCHAR(32)     -- 菜单名称
    menu_type   TINYINT         -- 类型：1=目录 2=菜单 3=按钮
    perms       VARCHAR(100)    -- 权限标识
    visible     TINYINT         -- 是否显示
```

**三级菜单类型：**

| 类型 | 说明 | 示例 | 前端控制 |
|------|------|------|----------|
| 1-目录 | 侧边栏一级菜单 | 文章管理 | 无权限时隐藏整个目录 |
| 2-菜单 | 可点击的页面 | 文章列表 | 无权限时隐藏菜单项 |
| 3-按钮 | 页面内的操作按钮 | 创建文章、删除 | 无权限时按钮置灰或隐藏 |

**返回给前端的权限数据结构：**

```json
{
  "menus": [
    {
      "id": 1,
      "name": "文章管理",
      "children": [
        { "id": 2, "name": "文章列表", "path": "/article/list" }
      ]
    }
  ],
  "perms": ["article:create", "article:delete", "user:list"]
}
```

前端根据 `perms` 列表控制按钮是否显示，后端根据 `@RequirePermission` 注解拦截请求，双层保障。

### Q13：白名单（不需要登录就能访问的接口）怎么设计？

**回答要点：**

**方案一：硬编码列表（简单）**

```java
private static final List<String> WHITE_LIST = Arrays.asList(
    "/auth/login", "/auth/register", "/actuator/health"
);
```

**方案二：注解标记（推荐）**

```java
@NoAuth
@PostMapping("/login")
public Result<TokenVO> login(...) { ... }
```

在网关过滤器中检查是否有 `@NoAuth` 注解：

```java
// 网关 Filter 中
if (handlerMethod.getMethodAnnotation(NoAuth.class) != null) {
    return chain.filter(exchange);  // 放行
}
```

**方案三：配置中心动态管理**

将白名单路径存储在 **Nacos 配置中心** 或数据库 `sys_config` 表中，修改后即时生效，无需重启服务。

### Q14：Token 过期了怎么办？刷新 Token 的设计？

**回答要点：**

**双 Token 方案：**

| Token | 有效期 | 用途 |
|-------|--------|------|
| Access Token | 2 小时 | 访问资源，放在请求头中 |
| Refresh Token | 7 天 | 仅用于刷新 Access Token |

**刷新流程：**

```
1. 用户请求接口，Access Token 过期
2. 前端收到 401，携带 Refresh Token 调用 /auth/refresh
3. 服务端校验 Refresh Token 有效性
4. 校验通过 → 签发新的 Access Token + Refresh Token
5. 前端用新 Token 重试原请求
```

**注意：** 如果 Refresh Token 也过期了，用户需要重新登录。

### Q15：权限系统如何防止水平越权？

**回答要点：**

**水平越权：** 用户 A 修改了用户 B 的数据。

**防护方案：**

```java
// 错误做法：只校验了登录，没校验数据归属
@PutMapping("/article/{id}")
public Result<Void> updateArticle(@PathVariable Long id, @RequestBody request) {
    articleService.update(id, request);  // ❌ 没有校验当前用户是否是文章作者
}

// 正确做法：校验数据归属权
@PutMapping("/article/{id}")
public Result<Void> updateArticle(@PathVariable Long id, @RequestBody request,
                                  @RequestHeader("X-User-Id") Long userId) {
    Article article = articleService.getById(id);
    if (!article.getAuthorId().equals(userId)) {
        throw new BizException(403, "只能编辑自己的文章");
    }
    articleService.update(id, request);
}
```

**权限校验的层次：**

| 层次 | 校验内容 | 谁做 |
|------|----------|------|
| 第一层 | Token 有效性（是否登录） | 网关 |
| 第二层 | 功能权限（能否操作这个功能） | AOP 切面 / @RequirePermission |
| 第三层 | 数据权限（能否操作这条数据） | Service 层代码 |

---

## 总结

| 知识点 | 重要程度 | 面试频率 |
|--------|----------|----------|
| RBAC 模型 | ⭐⭐⭐⭐⭐ | 几乎必问 |
| 权限缓存设计 | ⭐⭐⭐⭐ | 高频 |
| JWT vs Redis 存放权限 | ⭐⭐⭐⭐ | 高频 |
| 微服务权限架构 | ⭐⭐⭐⭐ | 中高频 |
| AOP 权限切面实现 | ⭐⭐⭐ | 中频 |
| Spring Security | ⭐⭐⭐ | 中频 |
| 数据权限（行级） | ⭐⭐⭐ | 中频 |
| 水平越权防护 | ⭐⭐⭐⭐⭐ | 几乎必问 |
| ACL / ABAC | ⭐⭐ | 低频 |
| 双 Token 刷新 | ⭐⭐⭐ | 中频 |