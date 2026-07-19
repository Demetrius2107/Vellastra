# RBAC 权限系统实现需求文档

## 1. 概述

### 1.1 目标
在现有 `sys_user.role` 字段的基础上，实现完整的**基于角色的访问控制**（Role-Based Access Control），支持多角色、多权限的灵活管理。

### 1.2 核心概念

```
用户(User) ──N:N──▶ 角色(Role) ──N:N──▶ 权限/菜单(Menu)
```

| 概念 | 说明 | 示例 |
|------|------|------|
| **用户** | 系统登录账号，可拥有多个角色 | admin、zhangsan |
| **角色** | 权限的集合，一个角色包含多个权限点 | 超级管理员、内容编辑、访客 |
| **权限/菜单** | 最小的操作单元，树形结构 | 文章管理(目录) → 文章列表(菜单) → 创建文章(按钮) |

### 1.3 现有可复用资源

| 资源 | 说明 |
|------|------|
| `t_role` 表 | 已存在，字段：id, role_name, role_code, description, sort_order, status, deleted, create_time, update_time |
| `t_menu` 表 | 已存在，字段：id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, visible, status, deleted, create_time, update_time |
| `t_user_role` 表 | 已存在，字段：id, user_id, role_id |
| `t_role_menu` 表 | 已存在，字段：id, role_id, menu_id |
| `@NoAuth` 注解 | 已创建，用于标记无需认证的接口 |
| `vellastra-auth` 模块 | 现有认证模块，RBAC 代码应放在此模块中 |

---

## 2. 数据库（已有，无需修改）

四张表已存在，直接使用：

```sql
-- t_role：角色表
-- t_menu：权限/菜单表（三级树形：目录→菜单→按钮）
-- t_user_role：用户-角色关联表
-- t_role_menu：角色-菜单关联表
```

### 2.1 初始数据

在 `phase1_full_schema.sql` 中补充以下初始数据：

```sql
-- 默认角色
INSERT INTO `t_role` (`role_name`, `role_code`, `description`, `sort_order`)
VALUES ('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1),
       ('内容编辑', 'EDITOR', '内容编辑，可管理文章和分类', 2),
       ('访客', 'GUEST', '普通访客，仅可浏览', 3);

-- 默认菜单（文章管理）
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `perms`, `sort_order`)
VALUES (0, '文章管理', 1, '/article', NULL, 1),
       (1, '文章列表', 2, '/article/list', 'article:list', 1),
       (1, '创建文章', 3, NULL, 'article:create', 2),
       (1, '编辑文章', 3, NULL, 'article:edit', 3),
       (1, '删除文章', 3, NULL, 'article:delete', 4),
       (0, '分类管理', 1, '/category', NULL, 2),
       (6, '分类列表', 2, '/category/list', 'category:list', 1),
       (6, '创建分类', 3, NULL, 'category:create', 2),
       (6, '删除分类', 3, NULL, 'category:delete', 3),
       (0, '评论管理', 1, '/comment', NULL, 3),
       (10, '评论列表', 2, '/comment/list', 'comment:list', 1),
       (10, '审核评论', 3, NULL, 'comment:audit', 2),
       (0, '用户管理', 1, '/user', NULL, 4),
       (13, '用户列表', 2, '/user/list', 'user:list', 1),
       (13, '分配角色', 3, NULL, 'user:assign-role', 2),
       (0, '系统管理', 1, '/system', NULL, 5),
       (16, '菜单管理', 2, '/system/menu', 'system:menu', 1),
       (16, '角色管理', 2, '/system/role', 'system:role', 2);

-- 超级管理员拥有所有菜单权限
INSERT INTO `t_role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `t_menu`;

-- 内容编辑拥有文章+分类+评论权限
INSERT INTO `t_role_menu` (`role_id`, `menu_id`)
SELECT 2, id FROM `t_menu`
WHERE `perms` IS NOT NULL
  AND (`perms` LIKE 'article:%' OR `perms` LIKE 'category:%' OR `perms` LIKE 'comment:%');
```

---

## 3. 后端代码实现

### 3.1 新增文件清单

```
vellastra-auth/
├── domain/
│   └── role/                          # 角色聚合
│       ├── entity/
│       │   └── Role.java              # 角色领域实体
│       ├── repository/
│       │   └── RoleRepository.java    # 角色仓储接口
│       └── valueobject/
│           └── RoleStatus.java        # 角色状态枚举
│   └── menu/                          # 菜单聚合
│       ├── entity/
│       │   └── Menu.java              # 菜单领域实体
│       ├── repository/
│       │   └── MenuRepository.java    # 菜单仓储接口
│       └── valueobject/
│           └── MenuType.java          # 菜单类型枚举（目录/菜单/按钮）
├── application/
│   ├── RoleApplicationService.java    # 角色应用服务（CRUD + 分配权限）
│   └── MenuApplicationService.java    # 菜单应用服务（CRUD + 树形）
└── infrastructure/
    └── persistence/
        ├── po/
        │   ├── RolePO.java            # 角色持久化对象
        │   └── MenuPO.java            # 菜单持久化对象
        ├── mapper/
        │   ├── RoleMapper.java        # 角色 Mapper
        │   └── MenuMapper.java        # 菜单 Mapper
        ├── converter/
        │   ├── RoleConverter.java     # 角色转换器
        │   └── MenuConverter.java     # 菜单转换器
        └── impl/
            ├── RoleRepositoryImpl.java
            └── MenuRepositoryImpl.java

vellastra-common/
└── annotation/
    └── RequirePermission.java         # 权限注解（已有 @NoAuth）
```

### 3.2 各文件详细设计

#### 3.2.1 `Role.java` — 角色领域实体

```java
package com.demetrius.vellastra.auth.domain.role.entity;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Role {
    private Long id;
    private String roleName;       // 角色名称
    private String roleCode;       // 角色编码，如 SUPER_ADMIN
    private String description;    // 描述
    private Integer sortOrder;     // 排序
    private RoleStatus status;     // 状态
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

#### 3.2.2 `Menu.java` — 菜单领域实体

```java
package com.demetrius.vellastra.auth.domain.menu.entity;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Menu {
    private Long id;
    private Long parentId;         // 父菜单ID（0=顶级）
    private String menuName;       // 菜单名称
    private MenuType menuType;     // 类型：目录/菜单/按钮
    private String path;           // 路由路径
    private String component;      // 组件路径
    private String perms;          // 权限标识，如 "article:create"
    private String icon;           // 图标
    private Integer sortOrder;     // 排序
    private Boolean visible;       // 是否显示
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

#### 3.2.3 `RequirePermission.java` — 权限注解

```java
package com.demetrius.vellastra.common.annotation;

import java.lang.annotation.*;

/**
 * 标记接口所需的权限标识
 *
 * 使用示例：
 * @RequirePermission("article:create")
 * @PostMapping
 * public Result<Long> createArticle(...) { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    /** 权限标识，如 "article:create" */
    String value();
}
```

#### 3.2.4 `RoleApplicationService.java` — 角色应用服务

```java
@Service
public class RoleApplicationService {

    // 核心方法：
    public Long createRole(CreateRoleRequest request);           // POST
    public void updateRole(Long id, UpdateRoleRequest request); // PUT
    public void deleteRole(Long id);                            // DELETE
    public RoleVO getRoleById(Long id);                         // GET
    public List<RoleVO> listRoles();                            // GET 列表
    public void assignMenus(Long roleId, List<Long> menuIds);   // PUT 分配菜单权限
    public List<Long> getRoleMenuIds(Long roleId);              // GET 已选菜单ID列表
}
```

#### 3.2.5 `MenuApplicationService.java` — 菜单应用服务

```java
@Service
public class MenuApplicationService {

    // 核心方法：
    public Long createMenu(CreateMenuRequest request);           // POST
    public void updateMenu(Long id, UpdateMenuRequest request);  // PUT
    public void deleteMenu(Long id);                             // DELETE（需校验无子菜单）
    public MenuVO getMenuById(Long id);                          // GET
    public List<MenuVO> getMenuTree();                           // GET 树形结构
}
```

#### 3.2.6 `RequirePermissionAspect.java` — 权限校验 AOP 切面

```java
package com.demetrius.vellastra.common.config;

@Aspect
@Component
public class RequirePermissionAspect {

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint,
                                   RequirePermission requirePermission) throws Throwable {
        // 1. 从请求头获取 X-User-Id
        // 2. 查询用户角色
        // 3. 查询角色关联的菜单权限（从 Redis 缓存获取，降低 DB 压力）
        // 4. 匹配注解的 value() 是否在用户权限列表中
        // 5. 匹配成功 → 放行；匹配失败 → 抛出 FORBIDDEN 异常
    }
}
```

### 3.3 网关过滤器增强

修改 `vellastra-gateway` 的 `AuthGlobalFilter`：

```java
// 当前：只检查 Token 是否存在
// 需要增强为：
// 1. 解析 JWT → 提取 userId, username, roles
// 2. 写入请求头：X-User-Id, X-Username, X-Roles
// 3. Token 过期返回具体原因
// 4. 白名单路径支持从配置读取
```

---

## 4. API 接口设计

### 4.1 角色管理接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/role` | 创建角色 | `system:role` |
| PUT | `/api/role/{id}` | 编辑角色 | `system:role` |
| DELETE | `/api/role/{id}` | 删除角色 | `system:role` |
| GET | `/api/role/{id}` | 角色详情 | `system:role` |
| GET | `/api/role/list` | 角色列表 | `system:role` |
| PUT | `/api/role/{id}/menus` | 分配菜单权限 | `system:role` |
| GET | `/api/role/{id}/menu-ids` | 获取已选菜单ID | `system:role` |

### 4.2 菜单管理接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/menu` | 创建菜单 | `system:menu` |
| PUT | `/api/menu/{id}` | 编辑菜单 | `system:menu` |
| DELETE | `/api/menu/{id}` | 删除菜单 | `system:menu` |
| GET | `/api/menu/{id}` | 菜单详情 | `system:menu` |
| GET | `/api/menu/tree` | 菜单树 | `system:menu` |

### 4.3 用户-角色绑定接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| PUT | `/api/user/{id}/roles` | 分配用户角色 | `user:assign-role` |

---

## 5. 实现顺序

建议按以下顺序逐步实现，每步完成后可验证：

```
Step 1: 实体 + PO + Mapper                  约 1h
  ├── Role.java / RolePO.java / RoleMapper.java
  └── Menu.java / MenuPO.java / MenuMapper.java

Step 2: Converter + Repository                约 1h
  ├── RoleConverter.java / RoleRepositoryImpl.java
  └── MenuConverter.java / MenuRepositoryImpl.java

Step 3: ApplicationService                    约 2h
  ├── RoleApplicationService（CRUD + 分配菜单权限）
  └── MenuApplicationService（CRUD + 树形结构）

Step 4: Controller + DTO                      约 1h
  ├── RoleController / RoleDTO
  └── MenuController / MenuDTO

Step 5: 权限注解 + AOP 切面                    约 1h
  ├── @RequirePermission 注解
  └── RequirePermissionAspect 切面

Step 6: 网关增强                              约 1h
  └── AuthGlobalFilter 解析 JWT → 写入请求头

Step 7: 补充初始数据 SQL + 测试                 约 1h
  ├── 默认角色/菜单/权限数据
  └── 单元测试
```

**总计工时：约 8h**

---

## 6. 验证标准

实现完成后，通过以下验证：

```bash
# 1. 超级管理员登录 → 可看到所有菜单
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. 创建角色
curl -X POST http://localhost:8080/api/role \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"roleName":"测试角色","roleCode":"TEST"}'

# 3. 分配菜单权限
curl -X PUT http://localhost:8080/api/role/1/menus \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '[1, 2, 3]'

# 4. 给用户分配角色
curl -X PUT http://localhost:8080/api/user/2/roles \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '[1, 2]'

# 5. 无权限用户调用受保护接口 → 403
curl -X POST http://localhost:8080/api/article \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"title":"...","content":"..."}'
# 预期：{"code":403,"message":"无权限","data":null}
```

---

## 7. 注意事项

1. **DDD 分层约束**：domain 层不依赖 Spring 框架注解，`Role.java` 和 `Menu.java` 使用纯 POJO
2. **缓存策略**：用户权限查询结果建议缓存到 Redis（key=`auth:permissions:{userId}`），避免每次请求都查 DB
3. **菜单树**：`MenuApplicationService.getMenuTree()` 查全表后在内存中构建树，参考 `CategoryApplicationService.buildTree()` 的实现
4. **软删除**：`t_role` 和 `t_menu` 有 `deleted` 字段，删除操作使用逻辑删除
5. **权限标识命名规范**：`模块:操作`，如 `article:create`、`article:edit`、`user:list`、`system:role`