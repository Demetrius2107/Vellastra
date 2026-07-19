# 大厂权限管理技术方案选型与对比

> 本文梳理业界主流权限管理方案，覆盖自研方案到开源框架，分析各自的优缺点和适用场景。

---

## 目录

1. [方案总览](#1-方案总览)
2. [自研轻量方案](#2-自研轻量方案)
3. [Spring Security](#3-spring-security)
4. [Apache Shiro](#4-apache-shiro)
5. [OAuth 2.0 + 第三方登录](#5-oauth-20--第三方登录)
6. [Casbin](#6-casbin)
7. [腾讯/阿里云 CAM](#7-腾讯阿里云-cam)
8. [方案对比总表](#8-方案对比总表)
9. [选型建议](#9-选型建议)

---

## 1. 方案总览

| 方案 | 类型 | 典型使用者 | 一句话总结 |
|------|------|-----------|-----------|
| **自研 @RequirePermission + AOP** | 自研轻量 | 中小团队、DDD 项目 | 100 行代码搞定，完全可控 |
| **Spring Security** | 框架 | 多数 Spring Boot 项目 | 功能最全，但配置复杂 |
| **Apache Shiro** | 框架 | 非 Spring 项目、老项目 | 轻量，学习成本低 |
| **OAuth 2.0** | 协议 | 开放平台、第三方登录 | 解决"谁在用"的问题，不解决"能用什么" |
| **Casbin** | 库 | 需要灵活策略的项目 | 支持 ACL/RBAC/ABAC，策略文件管理 |
| **腾讯云 CAM / 阿里云 RAM** | 云服务 | 云原生应用 | 适合云资源管理，不适合业务系统 |

---

## 2. 自研轻量方案

### 实现方式

```java
@RequirePermission("article:create")
@PostMapping("/api/article")
public Result<Long> createArticle(...) { ... }
```

通过自定义注解 + AOP 切面 + Redis 缓存实现权限校验，**不依赖任何外部框架**。

### 优点

| 优点 | 说明 |
|------|------|
| **完全可控** | 所有代码在自己手里，出问题可快速定位 |
| **无配置复杂度** | 不需要学习 Spring Security 的 Filter Chain / AuthenticationManager 等概念 |
| **DDD 友好** | 不影响 domain 层的纯净度 |
| **体积小** | 核心代码不到 100 行 |
| **灵活** | 可以按需定制，比如结合数据权限 |

### 缺点

| 缺点 | 说明 |
|------|------|
| **重复造轮子** | 常见功能（Session 管理、RememberMe）需要自己实现 |
| **缺少社区支持** | 遇到的坑只能自己填 |
| **安全风险** | 自己写的代码可能有漏洞 |

### 适合场景

- 中小型项目，角色和权限数量可控
- 使用 DDD 架构，domain 层不允许框架侵入
- 团队对 Spring Security 掌握不足，不想引入黑盒

### 大厂案例

> 字节跳动早期内部系统使用自研权限方案，后期随着业务增长迁移到统一权限中台。

---

## 3. Spring Security

### 架构

```
请求 → SecurityFilterChain（过滤器链）
         ├── UsernamePasswordAuthenticationFilter    → 表单登录
         ├── BasicAuthenticationFilter               → HTTP Basic 认证
         ├── BearerTokenAuthenticationFilter         → JWT/OAuth2 认证
         ├── ...（可自定义 Filter）
         └── FilterSecurityInterceptor              → 最终权限校验
                     ↓
              AccessDecisionManager（决策器）
                     ↓
              AccessDecisionVoter（投票器）
```

### 优点

| 优点 | 说明 |
|------|------|
| **功能全面** | 认证、授权、OAuth2、OIDC、LDAP、RememberMe 开箱即用 |
| **Spring 原生集成** | 与 Spring Boot 无缝集成，自动配置 |
| **社区强大** | 遇到问题 StackOverflow 上几乎都有答案 |
| **安全** | 经过大量生产验证，CSRF、CORS、Session Fixation 等防护内置 |
| **扩展性强** | 自定义 Filter、AuthenticationProvider、AccessDecisionVoter |

### 缺点

| 缺点 | 说明 |
|------|------|
| **学习曲线陡峭** | Filter Chain、AuthenticationManager、ProviderManager、SecurityContextHolder 等概念多 |
| **配置复杂** | 一个简单的 JWT 认证需要配置多个组件 |
| **自动配置黑盒** | 自动配置了太多东西，出了问题难以排查 |
| **侵入性强** | 需要实现 Spring Security 的接口，业务代码与框架耦合 |
| **重** | 引入大量依赖，启动变慢 |

### 适合场景

- 标准 Spring Boot 项目，团队熟悉 Spring Security
- 需要 OAuth2 / OIDC / LDAP 等复杂认证协议
- 大型项目，需要完善的安全防护

### 大厂案例

> **阿里巴巴**内部很多 Spring Boot 项目使用 Spring Security，但在此基础上封装了统一的安全 SDK（如 `aliyun-spring-security`），屏蔽了底层复杂度。

---

## 4. Apache Shiro

### 架构

```
Subject（当前用户）
    ↓
SecurityManager（安全管理器）
    ↓
Realm（数据源：从 DB / LDAP 读取用户和权限）
```

### 优点

| 优点 | 说明 |
|------|------|
| **轻量** | 核心 jar 包只有 300KB，学习成本低 |
| **API 简洁** | `subject.hasRole("admin")`、`subject.isPermitted("article:create")` |
| **不依赖 Spring** | 可运行在任何 Java 环境中 |
| **易于理解** | 概念比 Spring Security 少很多 |

### 缺点

| 缺点 | 说明 |
|------|------|
| **社区活跃度低** | 更新缓慢，最后一个版本是 2020 年 |
| **功能少** | 不支持 OAuth2 / OIDC，需要自己实现 |
| **Spring Boot 集成差** | 官方 Starter 不完善，需要自己配置 |
| **安全更新滞后** | 发现漏洞后修复速度慢 |

### 适合场景

- 非 Spring 项目（如 Spring MVC 老项目、纯 Servlet 项目）
- 只需要简单认证和角色判断的小型项目
- 遗留系统维护

### 大厂案例

> 较少大型互联网公司使用，多见于传统企业项目和教育系统。

---

## 5. OAuth 2.0 + 第三方登录

### 不是权限框架，是认证协议

OAuth 2.0 解决的是**"你是谁"**的问题，而不是**"你能做什么"**的问题。它常和 RBAC 配合使用：

```
OAuth 2.0 认证 → 拿到用户身份 → RBAC 权限校验 → 决定能否操作
```

### 四种授权模式

| 模式 | 适用场景 | 安全性 |
|------|----------|--------|
| 授权码模式（Authorization Code） | Web 应用、第三方登录 | 最高 |
| 简化模式（Implicit） | 纯前端 SPA（已不推荐） | 低 |
| 密码模式（Password） | 自研系统、移动端 | 中 |
| 客户端模式（Client Credentials） | 服务间调用 | 高 |

### 优点

| 优点 | 说明 |
|------|------|
| **标准化** | 业界标准协议，几乎所有平台都支持 |
| **第三方登录** | 微信、GitHub、Google 等一键登录 |
| **前后端分离友好** | 天然支持无状态 Token |
| **微服务友好** | 网关统一认证，服务间调用使用 Client Credentials |

### 缺点

| 缺点 | 说明 |
|------|------|
| **只解决认证** | 不解决权限校验，需要配合 RBAC 使用 |
| **实现复杂** | 完整的 OAuth 2.0 实现涉及 Authorization Server、Resource Server 等多个组件 |
| **Token 管理** | 刷新 Token、撤销 Token 需要额外实现 |

### 大厂案例

| 公司 | 应用 |
|------|------|
| **微信** | 微信开放平台 OAuth 2.0，公众号/小程序登录 |
| **GitHub** | GitHub OAuth App，第三方应用授权 |
| **Google** | Google Sign-In，覆盖 Android/Web/iOS |
| **阿里云** | RAM + OAuth 2.0，子账号授权 |

---

## 6. Casbin

### 定位

Casbin 是一个**权限管理库**，不是完整的安全框架。它只做权限校验，不处理认证。

```
Casbin 不做：用户注册、登录、密码管理
Casbin 只做：给定一个用户和资源，判断是否有权限
```

### 核心概念

```
request（请求）:  subject, object, action  →  (alice, data1, read)
policy（策略）:  p, alice, data1, read     →  允许
effect（效果）:  e = some(where (p.eft == allow))
```

### 策略文件示例

```ini
# model.conf（模型定义）
[request_definition]
r = sub, obj, act

[policy_definition]
p = sub, obj, act

[policy_effect]
e = some(where (p.eft == allow))

[matchers]
m = r.sub == p.sub && r.obj == p.obj && r.act == p.act

# policy.csv（策略数据）
p, alice, data1, read
p, bob, data2, write
```

### 优点

| 优点 | 说明 |
|------|------|
| **多语言支持** | Java、Go、Python、Node.js 等 10+ 语言 |
| **灵活的策略模型** | 支持 ACL、RBAC、ABAC、RESTful 等多种模型 |
| **策略文件化管理** | 策略可存储在文件、DB、Redis 中 |
| **性能好** | 单次权限校验 < 1μs |
| **无框架依赖** | 可集成到任何项目中 |

### 缺点

| 缺点 | 说明 |
|------|------|
| **学习成本** | 需要学习 Casbin 的模型语法（Model + Policy） |
| **不处理认证** | 需要自己实现登录、Token 校验 |
| **社区规模** | 比 Spring Security 小，中文资料较多 |
| **过度设计风险** | 大部分场景用 RBAC 就够，Casbin 的灵活性反而增加了复杂度 |

### 适合场景

- 需要灵活策略模型（ACL + RBAC + ABAC 混用）
- 多语言项目，需要统一的权限校验逻辑
- 不想被 Spring Security 绑定

### 大厂案例

> **字节跳动**早期的部分内部系统中使用 Casbin 进行权限策略管理，后迁移到自研权限中台。**华为**部分开源项目中使用 Casbin 做权限控制。

---

## 7. 腾讯/阿里云 CAM

### 定位

云平台的身份和访问管理服务，用于管理**云资源**的访问权限，**不是应用层权限管理方案**。

### 核心概念

```
用户（User）          → 子账号、RAM 用户
用户组（Group）       → 权限组的集合
角色（Role）          → 跨账号授权
策略（Policy）        → 权限规则定义（JSON 格式）
```

### 策略示例（阿里云 RAM）

```json
{
  "Version": "1",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "oss:GetObject",
        "oss:PutObject"
      ],
      "Resource": [
        "acs:oss:*:*:my-bucket/*"
      ]
    }
  ]
}
```

### 优点

| 优点 | 说明 |
|------|------|
| **开箱即用** | 不需要自己开发，控制台配置即可 |
| **安全可靠** | 经过大规模生产验证，具备审计日志 |
| **细粒度** | 支持资源级别、操作级别、条件级别的权限控制 |
| **多因素认证** | 支持 MFA、IP 限制等安全策略 |

### 缺点

| 缺点 | 说明 |
|------|------|
| **只适用于云资源** | 不能用于业务系统的权限管理 |
| **厂商锁定** | 迁移到其他云平台需要重新配置 |
| **成本** | 超出免费额度后需要付费 |
| **灵活性不足** | 无法实现复杂的业务权限规则 |

### 适合场景

- 云资源访问控制（OSS、RDS、ECS 等）
- 企业内部子账号管理
- 多账号授权

---

## 8. 方案对比总表

| 对比维度 | 自研 AOP | Spring Security | Apache Shiro | OAuth 2.0 | Casbin | 云 CAM |
|----------|----------|----------------|--------------|------------|--------|--------|
| **学习成本** | 低 | 高 | 低 | 中 | 中 | 低 |
| **实现成本** | 低 | 低（集成） | 低 | 高 | 中 | 无（开箱即用） |
| **功能完整度** | 低 | 高 | 中 | 中（仅认证） | 中（仅权限） | 高（云资源） |
| **灵活性** | 高 | 中 | 中 | 中 | 高 | 低 |
| **性能** | 高 | 中 | 高 | 中 | 高 | 依赖于网络 |
| **社区活跃度** | — | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ |
| **DDD 友好** | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **微服务友好** | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ |
| **安全防护** | 需自建 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **适用规模** | 小/中 | 大/超大 | 小/中 | 中/大/超大 | 中/大 | 中/大（云资源） |

---

## 9. 选型建议

### 按项目规模

```
个人项目 / 小团队（1-5 人）
    └── 自研 @RequirePermission + AOP（100 行代码，完全可控）
            ↓ 业务增长
中型项目 / 中团队（5-20 人）
    ├── 熟悉 Spring → Spring Security（功能全面，少踩坑）
    ├── 需要灵活策略 → Casbin（策略文件化管理）
    └── 不想引入重量级框架 → 继续自研，逐步完善
            ↓ 业务增长
大型项目 / 大团队（20+ 人）
    └── Spring Security + OAuth 2.0（业界标准，社区支持最强）
         └── 可封装统一权限 SDK，屏蔽底层复杂度
```

### 按技术栈

```
Spring Boot 项目
    ├── 团队熟悉 Spring Security → 直接用
    ├── 团队不熟悉 → 自研 AOP 方案，或 Casbin
    └── 需要 OAuth 2.0 → Spring Security + OAuth 2.0

非 Spring 项目（Go / Python / Node.js）
    └── Casbin（多语言支持，统一策略模型）

云原生项目
    └── 云资源权限 → 云 CAM
        业务权限 → 自研 / Spring Security / Casbin
```

### 国内大厂实践

| 公司 | 权限方案 | 说明 |
|------|----------|------|
| **阿里巴巴** | Spring Security + 自研统一 SDK | 内部封装了 `aliyun-spring-security`，屏蔽底层复杂度 |
| **腾讯** | 自研权限中台（统一认证+授权） | 所有业务系统接入统一权限中台，支持 RBAC + ABAC |
| **字节跳动** | 自研权限中台 + 早期 Casbin | 早期部分系统使用 Casbin，后迁移到自研中台 |
| **美团** | Spring Security + 自研权限中心 | 基于 Spring Security 二次封装，支持多租户 |
| **华为** | 自研 + 部分开源组件 | 云服务使用 CAM，业务系统自研 |
| **百度** | 自研权限系统 | 内部统一权限平台，覆盖所有业务线 |

### 总结

| 如果你要 | 推荐方案 |
|----------|----------|
| 快速实现，代码量少 | 自研 @RequirePermission + AOP |
| 标准 Spring 项目，不差配置时间 | Spring Security |
| 需要灵活的策略模型 | Casbin |
| 需要第三方登录 | 自研 + OAuth 2.0 协议 |
| 管理云资源权限 | 云 CAM |
| 重写一个百万级用户系统 | Spring Security + OAuth 2.0 + 自研权限中台 |