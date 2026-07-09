# V6 — 智能 Agent 协作平台

> 目标：在 V5 AI 辅助工具基础上，引入**自主智能 Agent**，实现从"人用工具"到"Agent 协作"的升级。
> 定位：每个用户拥有自己的 AI Agent 团队，自动完成内容创作、运营、审核等任务。
> 原则：Agent 不是替代人，而是可配置、可编排的智能协作者。

---

## 一、V6 在 V5 基础上新增的能力

```
V5（AI 辅助工具）                    V6（智能 Agent 协作）
─────────────────                   ─────────────────
AI 标签/摘要（被动调用）   ───→   Agent 自主决策何时需要打标签/摘要
AI 写作助手（用户触发）    ───→   写作 Agent 自主调研/写作/优化
多平台分发（手动分发）     ───→   分发 Agent 按策略自动分发
Webhook（事件通知）       ───→   Agent 间消息通信 + 任务编排
SEO 工具（按需执行）      ───→   SEO Agent 持续监控 + 自动优化
                                 ── 新增模块 ──
                                 Agent 运行时引擎
                                 内容创作 Agent
                                 内容审核 Agent
                                 运营推广 Agent
                                 数据分析 Agent
                                 客服互动 Agent
                                 Agent 编排工作流
                                 Agent 记忆与知识库
                                 Agent 市场
```

---

## 二、核心概念

### 2.1 什么是 Agent（在这个系统里）

```
Agent = 大语言模型(LLM) + 工具(Tools) + 记忆(Memory) + 权限(Permissions)

┌──────────────────────────────────────────┐
│               Agent 实例                   │
│  ┌────────────────────────────────────┐  │
│  │  大脑 (LLM)                        │  │
│  │  └ 决策: 当前应该做什么              │  │
│  │  └ 规划: 拆解为可执行的步骤           │  │
│  │  └ 反思: 检查结果是否达标             │  │
│  ├────────────────────────────────────┤  │
│  │  工具 (Tools)                      │  │
│  │  └ 调用 API: 创建文章/发布/审核       │  │
│  │  └ 搜索: 站内搜索/网络搜索            │  │
│  │  └ 计算: 数据分析/统计               │  │
│  ├────────────────────────────────────┤  │
│  │  记忆 (Memory)                     │  │
│  │  └ 短期: 当前对话上下文              │  │
│  │  └ 长期: 用户偏好/历史决策/风格库      │  │
│  ├────────────────────────────────────┤  │
│  │  权限 (Permissions)                │  │
│  │  └ 可执行的操作范围                  │  │
│  │  └ 需要人类确认的敏感操作             │  │
│  └────────────────────────────────────┘  │
└──────────────────────────────────────────┘
```

### 2.2 Agent 运行模式

| 模式 | 说明 | 适用场景 |
|------|------|----------|
| **自动模式** | Agent 自主决策执行，无需人工干预 | 内容审核、SEO 监控 |
| **建议模式** | Agent 给出建议，人确认后执行 | 文章发布、内容分发 |
| **协作模式** | 人 + Agent 实时对话协作 | 写作助手、数据分析 |
| **审批模式** | Agent 执行，敏感操作需审批 | 删除内容、修改配置 |
| **定时模式** | Agent 按计划执行任务 | 每日简报、周报生成 |

---

## 三、新增模块详解

### 3.1 Agent 运行时引擎 (P0)

Agent 的底层基础设施，负责 Agent 的生命周期管理。

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| POST | /agent/instance | 创建 Agent 实例 | P0 |
| GET | /agent/instance/{id} | Agent 实例详情 | P0 |
| DELETE | /agent/instance/{id} | 销毁 Agent 实例 | P0 |
| PUT | /agent/instance/{id}/config | 更新 Agent 配置（工具/权限/模型） | P0 |
| POST | /agent/instance/{id}/invoke | 手动调用 Agent 执行任务 | P0 |
| GET | /agent/instance | Agent 实例列表 | P0 |
| PUT | /agent/instance/{id}/status | 启/停 Agent | P1 |
| POST | /agent/instance/{id}/reset | 重置 Agent 记忆 | P1 |

**运行架构：**

```
┌──────────────────────────────────────────────────────────┐
│                     Agent 运行时引擎                        │
│                                                          │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐             │
│  │ 写作 Agent │  │ 审核 Agent │  │ 运营 Agent │  ...        │
│  │ (GPT-4)   │  │ (DeepSeek)│  │ (GPT-4)   │             │
│  └─────┬─────┘  └─────┬─────┘  └─────┬─────┘             │
│        │              │              │                    │
│        └──────────────┼──────────────┘                    │
│                       │                                   │
│          ┌────────────▼────────────┐                      │
│          │     Agent 消息总线       │                      │
│          │  (RabbitMQ/Redis PubSub) │                      │
│          └────────────┬────────────┘                      │
│                       │                                   │
│          ┌────────────▼────────────┐                      │
│          │     Agent 注册中心       │                      │
│          │  运行状态/健康检查/调度    │                      │
│          └─────────────────────────┘                      │
└──────────────────────────────────────────────────────────┘
```

**数据库设计：**

```
t_agent_instance
├── id / name / agent_type (WRITER / AUDITOR / MARKETER / ANALYST / SUPPORT)
├── llm_model (GPT-4 / DeepSeek / Claude)
├── status (RUNNING / PAUSED / STOPPED / ERROR)
├── mode (AUTO / SUGGEST / COLLAB / APPROVE / SCHEDULE)
├── config_json (JSON: 工具列表/权限/参数)
├── memory_summary (JSON: 长期记忆摘要)
├── user_id / owner_type (USER / SITE / SYSTEM)
├── task_count / success_count / fail_count
├── last_active_time
└── create_time / update_time

t_agent_memory
├── id / agent_id / memory_type (SHORT_TERM / LONG_TERM / PREFERENCE)
├── content (JSON: 记忆内容)
├── embedding (向量, 用于相似检索)
├── importance_score (1-10, 重要度)
└── create_time / expire_time

t_agent_log
├── id / agent_id / session_id
├── action / tool_used / input_snapshot / output_snapshot
├── tokens_consumed / duration_ms
├── success / error_message
└── create_time
```

### 3.2 内容创作 Agent (P0)

自主完成从选题到发布的完整创作流程。

#### 3.2.1 能力

```
内容创作 Agent 能力
├── 选题调研
│   ├── 扫描热点话题 + 分析竞争内容覆盖度
│   ├── 生成选题建议列表（含预估阅读量/SEO潜力）
│   └── 自动推送到用户待确认列表
├── 素材收集
│   ├── 联网搜索相关论文/文章/数据
│   ├── 提取关键引用 + 数据来源
│   └── 整理为素材包
├── 写作执行
│   ├── 根据提纲/大纲自动撰写初稿
│   ├── 自动插入配图（调用 AI 配图工具）
│   ├── 自动生成摘要/标签/SEO 描述
│   └── 多版本（长文版/摘要版/社交版）
├── 优化迭代
│   ├── 根据历史风格偏好自动调整文风
│   ├── 自我审校（检查事实/逻辑/格式）
│   └── A/B 测试标题
└── 发布执行
    ├── 按最佳发布时间自动排期
    ├── 调用分发 Agent 同步到多平台
    └── 发布后监控数据，自动生成效果报告
```

#### 3.2.2 接口清单

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| POST | /agent/writer/research | 触发选题调研 | P0 |
| GET | /agent/writer/topics | 选题建议列表 | P0 |
| POST | /agent/writer/draft/{topicId} | 基于选题生成初稿 | P0 |
| POST | /agent/writer/optimize/{articleId} | 优化已有文章（AI 审校 + 润色） | P0 |
| GET | /agent/writer/writing-log/{articleId} | 查看 Agent 写作过程日志 | P1 |
| PUT | /agent/writer/style | 设置写作风格偏好 | P1 |
| POST | /agent/writer/batch | 批量生成（多个选题） | P2 |

#### 3.2.3 Agent 工作流示例

```
用户操作: "帮我写一篇关于 RISC-V 的深度分析"
         │
         ▼
写作 Agent 启动
         │
         ▼
Step 1: 联网搜索 RISC-V 最新动态 (2026 Q2 信息)
        └── arXiv / TechCrunch / GitHub 最新数据
         │
         ▼
Step 2: 分析已有内容覆盖度
        └── 站内已有 3 篇相关文章 → 避免重复
         │
         ▼
Step 3: 生成文章大纲
        └── 推送给用户确认 / 或自动继续
         │
         ▼
Step 4: 逐段写作（每段结束后自我审校）
        └── 插入配图 → 生成代码示例
         │
         ▼
Step 5: 生成标题候选项 + SEO 描述 + 摘要
         │
         ▼
Step 6: 保存为草稿 + 通知用户审阅
```

### 3.3 内容审核 Agent (P1)

V3 的审核是规则引擎，V6 升级为 AI Agent 自主审核。

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| POST | /agent/auditor/check | 提交内容给 Agent 审核 | P0 |
| GET | /agent/auditor/queue | 审核队列 | P0 |
| GET | /agent/auditor/log/{id} | 审核决策日志（含推理过程） | P1 |
| PUT | /agent/auditor/rules | 自定义审核规则 | P1 |
| POST | /agent/auditor/appeal/{id} | 对审核结果申诉 | P1 |
| GET | /agent/auditor/stats | 审核统计（通过率/误判率/平均耗时） | P1 |

**审核维度：**

```
多维度审核
├── 内容安全
│   ├── 政治敏感 (可配置尺度和地域)
│   ├── 色情/暴力/违法内容
│   └── 垃圾广告/恶意推广
├── 内容质量
│   ├── 原创度检测（站内 + 互联网比对）
│   ├── 可读性评分
│   ├── 事实性核查（标注不确定信息）
│   └── 格式规范（标题规范/图片 Alt/标签完整）
└── 社区规范
    ├── 人身攻击/引战内容
    ├── 版权问题（引用是否合规）
    └── 重复内容（是否与已有文章高度相似）
```

**审核决策：**

| 决策 | 说明 | 自动/人工 |
|------|------|-----------|
| 通过 | 内容完全合规 | Agent 自动 |
| 需修改 | 有小问题，标注具体位置建议修改 | Agent 自动 |
| 人工复审 | 边界情况，Agent 无法确定 | 转人工 |
| 拒绝 | 严重违规 | Agent 标注原因，人工确认 |

### 3.4 运营推广 Agent (P1)

自动执行内容分发、社交推广、Newsletter 等运营任务。

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /agent/marketer/schedule | 制定推广计划 |
| GET | /agent/marketer/plan | 查看当前推广计划 |
| POST | /agent/marketer/distribute | 触发自动分发 |
| PUT | /agent/marketer/strategy | 设置推广策略（平台/时间/频率） |
| GET | /agent/marketer/report | 推广效果报告 |
| POST | /agent/marketer/newsletter | Agent 自动生成并发送 Newsletter |

**自动运营能力：**

```
├── 内容分发: 新文章发布后自动分发到各平台
├── 社交推广: 自动生成社交文案并定时发布
├── Newsletter: 自动汇总本周热门内容，生成并发送
├── 互动回复: 自动回复评论中的常见问题
├── 定时唤醒: 定期重新推广历史优质内容
└── 数据分析: 每周自动生成运营报告 + 优化建议
```

### 3.5 数据分析 Agent (P1)

主动分析数据，发现问题并生成洞察。

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /agent/analyst/insights | 数据洞察列表（Agent 自动生成） |
| POST | /agent/analyst/query | 向 Agent 提问（自然语言→数据查询） |
| POST | /agent/analyst/report | 生成数据报告 |
| GET | /agent/analyst/anomalies | 异常检测结果 |
| POST | /agent/analyst/trend | 趋势预测分析 |

**能力示例：**

```
用户提问: "为什么最近一周的阅读量下降了？"

数据分析 Agent 执行:
├── 1. 查询近 2 周每日 PV/UV 数据
├── 2. 按文章分类/来源/时段下钻
├── 3. 发现主要下降来自"AI 框架"分类
├── 4. 检查该分类是否有新内容 → 发现 5 天未更新
├── 5. 检查外部因素 → 当天有重大科技新闻分流
└── 输出: "阅读量下降主要由 AI 框架分类断更 5 天 + 隔壁科技大会热点分流导致"
```

### 3.6 客服互动 Agent (P2)

自动回复用户咨询，处理常见问题。

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /agent/support/chat | 用户发起对话 |
| GET | /agent/support/sessions | 对话历史列表 |
| GET | /agent/support/session/{id} | 对话详情 |
| POST | /agent/support/escalate | 升级到人工客服 |
| PUT | /agent/support/knowledge-base | 管理知识库（FAQ/文档） |
| GET | /agent/support/stats | 客服统计（解决率/满意度/平均响应时间） |

**自动回复范围：**
- 账号问题（登录/注册/密码重置）
- 内容问题（如何发文/审核规则/格式说明）
- 平台使用问题（功能引导/快捷键/Markdown 语法）
- 常规咨询（联系方式/友链申请/合作）

### 3.7 Agent 编排工作流 (P1)

支持将多个 Agent 串联成自动化工作流。

| 方法 | 路径 | 说明 | 优先级 |
|------|------|------|--------|
| POST | /workflow | 创建工作流 | P0 |
| GET | /workflow | 工作流列表 | P0 |
| GET | /workflow/{id} | 工作流详情 | P0 |
| PUT | /workflow/{id} | 编辑工作流 | P0 |
| DELETE | /workflow/{id} | 删除工作流 | P1 |
| POST | /workflow/{id}/run | 手动触发执行 | P0 |
| PUT | /workflow/{id}/trigger | 设置触发条件 | P1 |
| GET | /workflow/{id}/executions | 执行历史 | P1 |
| GET | /workflow/{id}/execution/{execId} | 单次执行详情（各步骤状态） | P1 |

**内置工作流模板：**

| 工作流 | 参与的 Agent | 触发条件 |
|--------|-------------|---------|
| 自动创作发布 | 写作 Agent → 审核 Agent → 分发 Agent | 定时/手动 |
| 内容质量巡检 | 审核 Agent → 数据分析 Agent → 写作 Agent | 每日 |
| 每周运营报告 | 数据分析 Agent → 运营 Agent | 每周一 |
| 用户反馈闭环 | 客服 Agent → 审核 Agent → 写作 Agent | 用户投诉触发 |
| 热点追踪创作 | 写作 Agent（调研）→ 写作 Agent（写作）→ 分发 Agent | 热点检测触发 |

**工作流 DSL 示例（JSON）：**

```json
{
  "name": "热点自动创作",
  "trigger": {
    "type": "schedule",
    "cron": "0 0 9 * * ?",
    "condition": {
      "type": "hot_topic_detected",
      "min_score": 70
    }
  },
  "steps": [
    {
      "id": "research",
      "agent_type": "WRITER",
      "action": "research_topic",
      "output_vars": ["topic_id", "materials"]
    },
    {
      "id": "human_approve_topic",
      "type": "human_approval",
      "timeout_hours": 24,
      "fallback": "cancel"
    },
    {
      "id": "write_draft",
      "agent_type": "WRITER",
      "action": "write_article",
      "input": {
        "topic_id": "${research.topic_id}",
        "materials": "${research.materials}"
      }
    },
    {
      "id": "audit_content",
      "agent_type": "AUDITOR",
      "action": "check_article",
      "input": {
        "article_id": "${write_draft.article_id}"
      }
    },
    {
      "id": "distribute",
      "agent_type": "MARKETER",
      "action": "auto_distribute",
      "input": {
        "article_id": "${write_draft.article_id}"
      },
      "condition": "${audit_content.decision == 'APPROVED'}"
    }
  ]
}
```

### 3.8 Agent 市场 (P2)

社区共享和交易 Agent 配置/工作流模板。

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /agent/market | Agent 市场列表 |
| GET | /agent/market/{id} | Agent 模板详情 |
| POST | /agent/market/install | 安装 Agent 模板 |
| POST | /agent/market/publish | 发布自定义 Agent 到市场 |
| POST | /agent/market/{id}/review | 评价/评分 |

**市场内容：**
- Agent 预设配置（写作风格/审核规则/运营策略）
- 工作流模板
- 工具扩展包
- Agent 提示词优化方案

---

## 三、V6 技术架构变更

### 3.1 新增服务

| 服务 | 职责 | 说明 |
|------|------|------|
| blog-agent-runtime | Agent 运行时引擎 | LLM 调用、工具执行、记忆管理 |
| blog-agent-orchestrator | Agent 编排引擎 | 工作流调度、状态管理、重试机制 |
| blog-agent-market | Agent 市场 | 模板管理、发布/安装/评分 |

### 3.2 新增基础设施

| 组件 | 用途 |
|------|------|
| LangChain4j / Spring AI | Java 生态的 Agent 框架（工具调用/记忆/链式调用） |
| MCP (Model Context Protocol) | Agent 工具的标准协议接口 |
| Vector DB (Milvus/PGVector) | Agent 长期记忆存储 + 相似检索 |
| LLM API (DeepSeek/GPT-4/Claude) | Agent 大脑，支持多模型切换 |
| RabbitMQ | Agent 间消息通信 + 事件驱动 |
| Redis Stream | 工作流状态管理 + 断点续传 |

### 3.3 Agent 安全架构

| 安全维度 | 方案 |
|----------|------|
| 操作权限 | Agent 权限 <= 创建者权限，不可越权 |
| 敏感操作 | 删除/修改配置/提权等需人工审批 |
| 数据隔离 | Agent 只能访问有权限的内容 |
| 审计追踪 | 所有 Agent 操作全量记录 |
| Token 预算 | 每个 Agent 有月度 Token 上限 |
| 沙箱执行 | Agent 工具调用在沙箱中执行 |
| 人类监督 | Agent 决策链路可追溯、可干预 |

### 3.4 Agent 与传统模块的关系

```
                    ┌──────────────────────────┐
                    │      API 网关              │
                    └────┬──────┬──────┬───────┘
                         │      │      │
               ┌─────────┘      │      └─────────┐
               │                │                │
         ┌─────▼─────┐   ┌─────▼─────┐    ┌─────▼─────┐
         │ V1~V5 业务  │   │ V5 AI 工具  │    │ V6 Agent  │
         │ (传统 CRUD) │   │ (被动调用)   │    │ (主动自主) │
         └─────┬─────┘   └─────┬─────┘    └─────┬─────┘
               │                │                │
               └────────────────┼────────────────┘
                                │
                    ┌───────────▼───────────┐
                    │   Agent 工具层          │
                    │  (Agent 通过工具调用 API) │
                    │  与传统 API 共用同一套    │
                    └───────────────────────┘
```

**关键设计原则：** Agent 不创建新的业务 API，而是通过**工具调用**复用 V1~V5 已有的业务 API。

---

## 四、V6 开发路线

```
Phase 6.1（Agent 基础设施）: 2.5 个月
├── Agent 运行时引擎（blog-agent-runtime）
├── LLM 多模型适配层（DeepSeek / GPT-4 / Claude）
├── 工具调用框架（基于 LangChain4j / Spring AI）
├── Agent 记忆系统（短期 + 长期 + 向量检索）
├── Agent 实例管理（创建/启停/配置）
└── Agent 安全与权限体系

Phase 6.2（核心 Agent 开发）: 3 个月
├── 内容创作 Agent（调研 → 写作 → 审校）
├── 内容审核 Agent（多维度审核 → 决策）
├── 运营推广 Agent（分发 → 社交 → Newsletter）
└── Agent 日志与审计面板

Phase 6.3（Agent 编排与协作）: 2 个月
├── blog-agent-orchestrator 工作流引擎
├── 工作流 DSL 解析器
├── Agent 间消息通信
├── 内置工作流模板
├── 人类审批节点
└── 工作流可视化监控

Phase 6.4（Agent 市场 + 优化）: 1.5 个月
├── Agent 市场（发布/安装/评分）
├── 数据分析 Agent
├── 客服互动 Agent
├── Agent 效果评估（成功率/Token 消耗/ROI）
├── 提示词优化工具
└── Agent 行为分析与调试工具
```

---

## 五、V6 不做的

| 功能 | 原因 |
|------|------|
| Agent 自修改代码 | 安全风险太高，当前阶段不允许 |
| Agent 间自主交易 | 无实际场景，复杂度过高 |
| Agent 联网完全自由 | 限定工具范围，防止失控 |
| 用户自定义 Agent 训练 | 成本过高，使用预设 LLM |
| 多模态 Agent（图片/视频生成）| 单图生成已有，视频成本太高 |
| Agent 对战/竞技 | 偏离 CMS 定位 |

---

## 六、V1 → V6 完整演进全景

```
V1（个人博客）    基础 CRUD + CLI + 静态站
    │
V2（通用 CMS）    评论/文件/网关/RBAC/Vue3 后台
    │
V3（UGC 社区）    问答/Feed/通知/排行榜/审核/搜索/用户等级
    │
V4（内容生态）    专栏/Newsletter/版本管理/回收站/运营数据/多站点
    │
V5（智能开放）    AI 标签摘要/AI 写作/OAuth/开放 API/CDN/备份/主题市场
    │
V6（Agent 协作）  Agent 运行时/创作Agent/审核Agent/运营Agent/编排工作流/Agent市场
    │
    └── 从"人操作"到"人指挥"，Agent 执行
```

**Agent 化程度演进：**

```
V5: 人→工具（人主动调用 AI 功能）
       ↓
V6: 人→Agent→工具（人告诉 Agent 目标，Agent 自主执行）
       ↓
未来: 人→多 Agent 团队（多个 Agent 协作完成复杂目标）
```

**每个版本的核心理念：**

| 版本 | 核心理念 |
|------|----------|
| V1 | **能用**: 最小可行博客系统 |
| V2 | **好用**: 完整的 CMS 管理体验 |
| V3 | **好玩**: 社区互动让内容活起来 |
| V4 | **好运营**: 让运营者高效管理内容生态 |
| V5 | **好智能**: AI 辅助降低创作门槛 |
| V6 | **好省心**: Agent 自主执行，人只做决策 |
