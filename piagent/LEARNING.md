# PiAgent 学习指南

## 给初学者的解释

本项目是一个 AI Agent 框架，可能对初学者来说有些概念比较抽象。让我用简单的方式解释一下：

### 什么是 Agent？

想象一个"智能助手"。当你问它问题时，它会：
1. 理解你的问题
2. 思考如何回答
3. 如果需要，可以"使用工具"（比如查日历、计算器）来帮助你
4. 最后给你答案

这个"智能助手"就是一个 Agent。

### 什么是 AgentLoop？

"AgentLoop"就是 Agent 的"思考循环"：

```
用户提问 → Agent思考 → 需要工具吗？
                           ↓
                    是 → 执行工具 → 把结果告诉Agent → Agent再次思考
                           ↓
                    否 → 直接回答 → 结束
```

这个循环可能会执行多次，直到得到最终答案。

### 什么是 Tool？

Tool 就是"工具"。就像人需要工具来完成任务一样，Agent 也需要工具。

项目中的内置工具：
- `calculator`：计算器，可以算数学题
- `current_time`：获取当前时间
- `search`：搜索信息（示例扩展）

### 什么是 Skill？

Skill 是"技能包"。它是一组配置的组合：
- 系统提示词：告诉 Agent "你是什么样的角色"
- 允许使用的工具：这个技能可以用哪些工具
- 最大执行步数：最多思考多少次

比如"代码审查"技能：
- 系统提示词："你是一个专业的代码审查员..."
- 允许的工具：代码分析工具
- 最大步数：5次

### 什么是 Extension？

Extension 是"插件"。它可以在应用启动时添加新功能：
- 注册新的工具
- 注册新的技能
- 做任何初始化工作

### 什么是 Session？

Session 是"会话"。它保存对话历史：
- 你之前说了什么
- Agent 回答了什么
- 用过哪些工具

这样 Agent 才能理解上下文，进行多轮对话。

### 什么是 Prompt Template？

Prompt Template 是"提示词模板"。它使用占位符：

```
你好，{{name}}！今天是 {{date}}。
```

渲染后变成：
```
你好，张三！今天是 2024-01-01。
```

## 项目架构图

```
┌─────────────────────────────────────────────────────────┐
│                      REST API 层                         │
│  AgentController | SessionController | ToolController   │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                     核心业务层                           │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────┐ │
│  │  AgentLoop   │  │    Skill     │  │    Event    │ │
│  │  (核心循环)   │  │   (技能)     │  │   (事件)    │ │
│  └──────────────┘  └──────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                     工具系统                             │
│  ToolRegistry ←→ ToolExecutorRegistry                  │
│       ↓                                                │
│  CalculatorTool | CurrentTimeTool | SearchTool         │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                     LLM 抽象层                          │
│              ChatModelClient (接口)                      │
│                    ↓                                    │
│  OpenAI | Ollama | DashScope | DeepSeek | Mock        │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                     存储层                               │
│            SessionRepository (接口)                      │
│                    ↓                                    │
│           InMemorySessionRepository                     │
└─────────────────────────────────────────────────────────┘
```

## 代码组织说明

每个包都有明确的职责：

| 包名 | 职责 | 关键类 |
|------|------|--------|
| `agent` | Agent 执行循环 | AgentLoop, AgentRequest, AgentResponse |
| `session` | 会话管理 | AgentSession, SessionService |
| `message` | 消息模型 | Message, MessageRole, MessageFactory |
| `llm` | LLM 调用抽象 | ChatModelClient, MockChatModelClient |
| `tool` | 工具系统 | ToolRegistry, ToolExecutor |
| `skill` | 技能系统 | SkillDefinition, SkillRegistry |
| `extension` | 扩展机制 | Extension, ExtensionContext |
| `event` | 事件系统 | AgentEvent, AgentEventPublisher |
| `api` | REST API | AgentController, SessionController |
| `common` | 通用工具 | ApiResponse, BizException, ErrorCode |

## 如何阅读代码？

建议按以下顺序阅读：

1. **先看接口**：每个模块都有接口定义，理解接口比实现更重要
2. **再看简单实现**：如 Message、Session
3. **然后看核心**：AgentLoop 是最重要的类
4. **最后看集成**：REST API 如何把各部分串联起来

## TDD 方式理解代码

每个测试类都描述了一个模块的功能：

- `MessageRoleTest` → 消息有哪些类型？
- `ToolRegistryTest` → 如何注册工具？
- `AgentLoopTest` → Agent 执行流程是什么？

通过测试用例，你可以了解每个类的"契约"。
