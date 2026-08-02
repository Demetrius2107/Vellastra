# Vellastra 后端 API 接口文档

> 用途：前端对接参考。所有接口经网关 `http://localhost:8080` 访问，携带 `Authorization: Bearer <token>`。
> 版本：Phase 1-2 全部模块 | 更新日期：2026-08-03

---

## 通用约定

| 项 | 说明 |
|----|------|
| 统一响应体 | `{"code": 200, "message": "success", "data": ...}` |
| 认证方式 | 登录后返回 token，请求头 `Authorization: Bearer <token>` |
| 用户识别 | 网关解析 JWT 后注入 `X-User-Id` / `X-Username` / `X-Roles` 请求头 |
| 分页参数 | `current`(页码) + `size`(每页) |
| 错误码 | 200 成功 / 400 参数 / 401 未认证 / 403 无权限 / 404 不存在 / 1xxx 用户 / 2xxx Token / 5xxx 评论 |

---

## 1. 认证鉴权 (vellastra-auth :8081)

| 方法 | 路径 | 功能 |
|------|------|------|
| POST | `/auth/login` | 登录，body `{username, password}`，返回 `{token, expireIn}` |
| POST | `/auth/register` | 注册，body `{username, password, email}` |
| POST | `/auth/logout` | 登出（Token 加入黑名单），头 `Authorization` |
| POST | `/auth/refresh` | 刷新 Token，头 `Authorization` |

### 角色管理

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/role/list` | 角色列表 |
| GET | `/role/{id}` | 角色详情 |
| POST | `/role` | 创建角色 `{roleName, roleCode, description, sortOrder}` |
| PUT | `/role/{id}` | 更新角色 |
| DELETE | `/role/{id}` | 删除角色 |
| PUT | `/role/{id}/menus` | 分配菜单权限（全量覆盖）body `[menuId,...]` |
| GET | `/role/{id}/menu-ids` | 查询角色已分配菜单 ID |

### 菜单管理

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/menu/tree` | 菜单树（目录→菜单→按钮） |
| GET | `/menu/{id}` | 菜单详情 |
| POST | `/menu` | 创建菜单 `{menuName, menuType, parentId, path, component, perms, icon}` |
| PUT | `/menu/{id}` | 更新菜单 |
| DELETE | `/menu/{id}` | 删除菜单（有子菜单不可删） |

### 用户-角色

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/user/{userId}/roles` | 查询用户角色 ID 列表 |
| PUT | `/user/{userId}/roles` | 分配用户角色 body `[roleId,...]` |

### 系统配置

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/system/config` | 获取全部系统配置（Map） |
| GET | `/system/config/{key}` | 按 key 获取配置 |
| PUT | `/system/config/{key}` | 设置配置 |
| GET | `/system/friend-link` | 友情链接列表 |
| POST | `/system/friend-link` | 新增友情链接 |
| PUT | `/system/friend-link/{id}` | 更新友情链接 |
| DELETE | `/system/friend-link/{id}` | 删除友情链接 |

---

## 2. 用户管理 (vellastra-user :8082)

> 注意：此模块用户接口前缀为 `/api/user`，与 auth 模块的 `/user` 不同

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/api/user/list` | 分页用户列表，参数 `keyword`(用户名/昵称/邮箱模糊) `status` |
| GET | `/api/user/{id}` | 用户详情 |
| GET | `/api/user/info` | 当前登录用户信息（从 X-User-Id 获取） |
| PUT | `/api/user/info` | 更新当前用户信息 `{nickname, avatar, bio}` |
| POST | `/api/user` | 新增用户 `{username, password, email, nickname}` |
| PUT | `/api/user/{id}` | 编辑用户 `{nickname, email, avatar}` |
| DELETE | `/api/user/{id}` | 逻辑删除用户 |
| PATCH | `/api/user/{id}/status?status=0\|1` | 启用/禁用用户 |
| PUT | `/api/user/{id}/reset-password` | 管理员重置密码为 123456 |
| PUT | `/api/user/password` | 自助修改密码 `{oldPassword, newPassword}` |

### 内部 API（Feign 调用）

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/internal/user/{id}` | 用户基本信息（供 auth 模块 Feign 调用） |

---

## 3. 文章管理 (vellastra-article :8083)

| 方法 | 路径 | 功能 |
|------|------|------|
| POST | `/article` | 创建文章，头 `X-User-Id` |
| PUT | `/article/{id}` | 更新文章 |
| DELETE | `/article/{id}` | 删除文章（已发布不可删） |
| GET | `/article/{id}` | 文章详情 |
| GET | `/article` | 分页列表，参数 `categoryId` `keyword` `authorId` |
| PATCH | `/article/{id}/publish` | 发布（草稿→已发布） |
| PATCH | `/article/{id}/withdraw` | 撤回（已发布→下架） |
| PATCH | `/article/{id}/top?top=0\|1` | 置顶/取消置顶 |
| POST | `/article/{id}/view` | 浏览计数 +1 |
| POST | `/article/{id}/like` | 点赞/取消点赞（toggle），头 `X-User-Id` |
| GET | `/article/latest?size=5` | 最新文章 |
| POST | `/article/batch` | 批量操作 `{ids:[], action:"delete"/"publish"}` |
| GET | `/article/dashboard` | 仪表盘数据（总览+热门文章） |

---

## 4. 分类管理 (vellastra-category :8084)

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/category/tree` | 分类树 |
| GET | `/category/{id}` | 分类详情 |
| POST | `/category` | 新增分类 `{name, parentId, description, sort}` |
| PUT | `/category/{id}` | 更新分类 |
| DELETE | `/category/{id}` | 删除分类（有子分类/文章不可删） |

---

## 5. 评论管理 (vellastra-comment :8085)

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/comment` | 分页评论列表，参数 `articleId` `status` |
| POST | `/comment` | 发表评论，头 `X-User-Id`，body `{articleId, content, parentId}` |
| POST | `/comment/reply` | 回复评论（楼中楼） |
| DELETE | `/comment/{id}` | 删除评论 |
| PATCH | `/comment/{id}/audit?status=1\|2` | 审核评论（1通过 2拒绝） |

---

## 6. 文件管理 (vellastra-file :8086)

| 方法 | 路径 | 功能 |
|------|------|------|
| POST | `/api/file/upload` | 单文件上传，参数 `file` + `category`(image/doc) + 头 `X-User-Id` |
| POST | `/api/file/upload/init` | 分块上传初始化 `{fileName, totalSize, totalChunks}` → `{uploadId, chunkSize}` |
| POST | `/api/file/upload/chunk` | 上传分块 `{uploadId, chunkIndex, file}` |
| POST | `/api/file/upload/complete` | 合并分块 `{uploadId, fileName}` + 头 `X-User-Id` |
| POST | `/api/file/upload/cancel` | 取消分块上传 |

---

## 7. 标签管理 (vellastra-tag :8087)

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/tag` | 全部标签 |
| GET | `/tag/hot?limit=10` | 热门标签 |
| GET | `/tag/{id}` | 标签详情 |
| POST | `/tag` | 创建标签 `{name, slug}` |
| PUT | `/tag/{id}` | 更新标签 |
| DELETE | `/tag/{id}` | 删除标签（有关联文章不可删） |

---

## 8. 发布引擎 (vellastra-publish :8088)

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/publish/sites` | 站点列表 |
| GET | `/publish/sites/{id}` | 站点详情 |
| POST | `/publish/sites` | 创建站点 `{name, slug, repoUrl, buildCommand, outputDir, domain}` |
| PUT | `/publish/sites/{id}` | 更新站点 |
| DELETE | `/publish/sites/{id}` | 删除站点 |
| POST | `/publish/builds?siteId=&environment=` | 触发构建，头 `X-User-Id` |
| GET | `/publish/builds?siteId=&status=` | 构建记录分页 |
| GET | `/publish/builds/{id}` | 构建详情 |
| GET | `/publish/builds/history/{siteId}` | 构建历史 |
| POST | `/publish/builds/{id}/retry` | 重试构建 |
| POST | `/publish/builds/{id}/rollback?targetBuildId=` | 回滚到指定版本 |

---

## 9. 内容回收站 (vellastra-recycle :8089)

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/recycle` | 回收站分页，参数 `type` `keyword` `dateFrom` `dateTo` |
| GET | `/recycle/stats` | 回收站统计（总数/各类型） |
| GET | `/recycle/{id}` | 回收项详情 |
| POST | `/recycle/restore/{id}` | 恢复单项 |
| POST | `/recycle/restore/batch` | 批量恢复 body `[id,...]` |
| DELETE | `/recycle/{id}` | 永久删除单项 |
| POST | `/recycle/delete/batch` | 批量永久删除 |
| DELETE | `/recycle/empty` | 清空回收站 |

---

## 10. 数据统计 (vellastra-analytics :8090)

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/analytics/dashboard` | 总览（文章/用户/评论/浏览量/分类/标签数） |
| GET | `/analytics/trend?metric=&days=` | 单指标趋势（article_publish/user_register/total_views/comment_create） |
| GET | `/analytics/trend/all?days=30` | 多指标趋势汇总 |
| GET | `/analytics/hot-articles?limit=10` | 热门文章 TOP |
| GET | `/analytics/category-stats` | 分类文章统计 |
| GET | `/analytics/author-stats` | 作者贡献统计 |
| GET | `/analytics/export/{type}` | 导出 CSV（type=articles/users） |

---

## 11. 专栏专题 (vellastra-column :8091)

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/column` | 专栏分页，参数 `status` `featured` |
| GET | `/column/all` | 全部专栏 |
| GET | `/column/{id}` | 专栏详情 |
| POST | `/column` | 创建专栏 `{name, slug, description, coverImage, authorId, authorName}` |
| PUT | `/column/{id}` | 更新专栏 |
| DELETE | `/column/{id}` | 删除专栏 |
| GET | `/column/{columnId}/articles` | 专栏文章列表 |
| POST | `/column/{columnId}/articles` | 收录文章 `{articleId, articleTitle, note}` |
| POST | `/column/{columnId}/articles/batch` | 批量收录 |
| DELETE | `/column/articles/{id}` | 移出专栏 |
| PUT | `/column/articles/{id}/sort?sortOrder=` | 调整文章排序 |

---

## 12. 邮件系统 (vellastra-mail :8092)

### 订阅管理

| 方法 | 路径 | 功能 |
|------|------|------|
| POST | `/mail/subscribers?email=&name=` | 订阅（发送确认邮件，double opt-in） |
| GET | `/mail/subscribers/confirm?token=` | 确认订阅（邮件链接点击） |
| GET | `/mail/subscribers/unsubscribe?token=` | 退订（邮件链接点击） |
| GET | `/mail/subscribers` | 订阅者分页，参数 `status` |
| GET | `/mail/subscribers/count` | 已确认订阅者数量 |

### 模板管理

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/mail/templates` | 模板分页 |
| GET | `/mail/templates/{id}` | 模板详情 |
| POST | `/mail/templates` | 创建模板 `{name, code, subject, content, createdBy}` |
| PUT | `/mail/templates/{id}` | 更新模板 |
| DELETE | `/mail/templates/{id}` | 删除模板 |
| POST | `/mail/templates/{id}/preview` | 模板渲染预览 |

### 发送与追踪

| 方法 | 路径 | 功能 |
|------|------|------|
| POST | `/mail/send/single?to=&templateCode=` | 单封发送 |
| POST | `/mail/send/batch?templateCode=` | 批量发送 body `[email,...]` |
| POST | `/mail/send/{id}/retry` | 重试失败邮件 |
| POST | `/mail/send/{id}/bounce?reason=` | 标记退信 |
| GET | `/mail/send/logs?status=&batchNo=` | 发送记录分页 |
| GET | `/mail/track/open/{logId}` | 打开追踪（透明像素） |
| GET | `/mail/track/click/{logId}?url=` | 点击追踪（302 跳转） |

---

## 13. 幂等 Token（通用）

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/idempotent/token` | 获取幂等 Token，写操作前调用后放入请求头 `X-Idempotent-Token` |

---

## 前端对接建议

1. **统一封装**：在 `packages/api-core` 中按模块建立 `auth.ts` / `user.ts` / `article.ts` / `mail.ts` 等
2. **Token 管理**：登录后存 token，axios 拦截器自动加 `Authorization: Bearer`
3. **401 处理**：拦截器收到 401 自动跳登录页
4. **权限控制**：登录返回 token 内含 roles，前端菜单根据 `/menu/tree` 渲染
