# Blog-BackEnd-MS API 接口文档

> 网关统一入口：`http://localhost:8080`
>
> 生成日期：2026-07-05

---

## 目录

1. [鉴权服务 (blog-auth)](#1-鉴权服务-blog-auth)
2. [用户服务 (blog-user)](#2-用户服务-blog-user)
3. [文章服务 (blog-article)](#3-文章服务-blog-article)
4. [分类服务 (blog-category)](#4-分类服务-blog-category)
5. [评论服务 (blog-comment)](#5-评论服务-blog-comment)

---

## 1. 鉴权服务 (blog-auth)

**基础路径：** `/auth`（无 `/api` 前缀）

| 请求方式 | 路径 | 功能说明 | 请求头/参数 |
|--------|------|---------|-----------|
| POST | `/auth/login` | 用户登录 | Body: `LoginRequest`(username, password) |
| POST | `/auth/register` | 用户注册 | Body: `RegisterRequest`(username, password, email, etc.) |
| POST | `/auth/logout` | 用户登出 | Header: `Authorization`(token) |
| POST | `/auth/refresh` | 刷新 Token | Header: `Authorization`(token) |

> ⚠️ **注意：** 鉴权服务与其他服务不同，Gateway 路由路径为 `/auth/**`，**没有 `/api` 前缀**。

---

## 2. 用户服务 (blog-user)

**基础路径：** `/api/user`

| 请求方式 | 路径 | 功能说明 | 请求头/参数 |
|--------|------|---------|-----------|
| GET | `/api/user/{id}` | 根据 ID 获取用户信息 | Path: `id`(用户ID) |
| GET | `/api/user/info` | 获取当前登录用户信息 | Header: `X-User-Id` |
| PUT | `/api/user/{id}` | 更新用户信息 | Path: `id`, Body: `UserVO` |

---

## 3. 文章服务 (blog-article)

**基础路径：** `/api/article`

### CRUD 基础操作

| 请求方式 | 路径 | 功能说明 | 请求头/参数 |
|--------|------|---------|-----------|
| POST | `/api/article` | **创建文章** | Header: `X-User-Id`, Body: `CreateArticleRequest` |
| GET | `/api/article` | **分页查询文章列表** | Query: `current`(默认1), `size`(默认10), `categoryId`(可选), `keyword`(可选), `tag`(可选), `authorId`(可选) |
| GET | `/api/article/{id}` | **查看文章详情** | Path: `id`(文章ID) |
| PUT | `/api/article/{id}` | **更新文章** | Path: `id`, Body: `UpdateArticleRequest` |
| DELETE | `/api/article/{id}` | **删除文章** | Path: `id`(文章ID)（已发布的文章不可删除） |

### 文章状态管理

| 请求方式 | 路径 | 功能说明 | 请求头/参数 |
|--------|------|---------|-----------|
| PATCH | `/api/article/{id}/publish` | **发布文章**（草稿 → 已发布） | Path: `id` |
| PATCH | `/api/article/{id}/withdraw` | **撤回发布**（已发布 → 下架） | Path: `id` |

### 置顶管理

| 请求方式 | 路径 | 功能说明 | 请求头/参数 |
|--------|------|---------|-----------|
| PATCH | `/api/article/{id}/top` | **设置/取消置顶** | Path: `id`, Query: `top`(boolean) |

### 互动统计

| 请求方式 | 路径 | 功能说明 | 请求头/参数 |
|--------|------|---------|-----------|
| POST | `/api/article/{id}/view` | **浏览计数**（IP+时间窗口防刷） | Path: `id` |
| POST | `/api/article/{id}/like` | **点赞/取消点赞**（toggle 模式） | Path: `id`, Header: `X-User-Id` |

### 其他

| 请求方式 | 路径 | 功能说明 | 请求头/参数 |
|--------|------|---------|-----------|
| GET | `/api/article/latest` | **获取最新文章** | Query: `size`(默认5) |
| POST | `/api/article/batch` | **批量操作文章**（删除/发布） | Body: `BatchArticleRequest`(ids + action) |

---

## 4. 分类服务 (blog-category)

**基础路径：** `/api/category`

| 请求方式 | 路径 | 功能说明 | 请求头/参数 |
|--------|------|---------|-----------|
| GET | `/api/category/tree` | **获取分类树**（树形结构） | - |
| GET | `/api/category/{id}` | **查看分类详情** | Path: `id`(分类ID) |
| POST | `/api/category` | **新增分类** | Body: `CreateCategoryRequest` |
| PUT | `/api/category/{id}` | **更新分类** | Path: `id`, Body: `UpdateCategoryRequest` |
| DELETE | `/api/category/{id}` | **删除分类**（有子分类或文章时不可删除） | Path: `id` |

---

## 5. 评论服务 (blog-comment)

**基础路径：** `/api/comment`

| 请求方式 | 路径 | 功能说明 | 请求头/参数 |
|--------|------|---------|-----------|
| GET | `/api/comment` | **分页查询评论列表** | Query: `current`(默认1), `size`(默认10), `articleId`(可选), `status`(可选) |
| POST | `/api/comment` | **创建评论** | Header: `X-User-Id`, Body: `CreateCommentRequest` |
| POST | `/api/comment/reply` | **回复评论** | Header: `X-User-Id`, Body: `ReplyCommentRequest` |
| DELETE | `/api/comment/{id}` | **删除评论** | Path: `id` |
| PATCH | `/api/comment/{id}/audit` | **审核评论** | Path: `id`, Query: `status`(Integer) |

---

## Gateway 路由配置速览

| 微服务 | 内部端口 | 外部路径 | Gateway StripPrefix |
|--------|---------|---------|-------------------|
| blog-auth | 8081 | `/auth/**` | 1 |
| blog-user | 8082 | `/api/user/**` | 2 |
| blog-article | 8083 | `/api/article/**` | 2 |
| blog-category | 8084 | `/api/category/**` | 2 |
| blog-comment | 8085 | `/api/comment/**` | 2 |

> **说明：** `StripPrefix=1` 表示去掉路径中的第一个前缀段（如 `/auth/login` → `/login`），`StripPrefix=2` 表示去掉前两个前缀段（如 `/api/article/id` → `/article/id`）。内部微服务 `@RequestMapping` 中已经包含了 `/article`、`/user` 等二级路径，因此 Gateway 转发时需去掉 `/api` 前缀。

---

## 请求头约定

| 请求头 | 说明 | 使用场景 |
|--------|------|---------|
| `X-User-Id` | 当前登录用户 ID（Long） | 创建文章、评论、点赞等需要用户身份的接口 |
| `Authorization` | JWT Token | 登出、刷新 Token |

## 通用响应格式

所有接口统一返回 `Result<T>` 结构：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

分页接口返回 `PageResult<T>` 结构：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 0,
    "current": 1,
    "size": 10
  }
}
```

---

> **统计：** 共 **5 个微服务**，**26 个 API 端点**（不含 PRD 中规划的日志等未实现接口）。
