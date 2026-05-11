# PiAgent

基于 Java + Spring Boot + LangChain4j 的 AI Agent 后端框架

## 项目简介

PiAgent 是一个借鉴 Pi 框架核心设计思想的 AI Agent 后端框架，采用 Java/Spring Boot/LangChain4j 技术栈重新设计实现。

### 设计目标

1. **小核心，大扩展**：保持核心逻辑简洁，通过扩展机制支持复杂功能
2. **Agent Loop 驱动**：以 Agent 执行循环为核心，统一处理输入、推理、工具调用
3. **可插拔架构**：Tool、Skill、Extension、Prompt Template 均可插拔
4. **统一 LLM 抽象**：支持多种模型供应商（OpenAI、Ollama、DashScope、DeepSeek）
5. **多调用方式**：支持 REST API、SSE 事件流等多种调用方式

### 技术栈

- Java 25
- Spring Boot 3.4.x
- LangChain4j 1.0.0
- Maven
- JUnit 5 + Mockito + AssertJ

## 快速开始

### 前置要求

- JDK 25+
- Maven 3.8+
- 网络连接（用于下载依赖）

### 编译运行

```bash
# 克隆项目
git clone <repository-url>
cd piagent

# 编译项目
mvn clean compile

# 运行测试
mvn test

# 启动应用
mvn spring-boot:run
```

### 配置说明

在 `src/main/resources/application.yml` 中配置 LLM：

```yaml
piagent:
  llm:
    provider: openai  # 可选: openai, ollama, dashscope, deepseek
    openai:
      api-key: your-api-key
      model-name: gpt-4o-mini
```

## 项目结构

```
com.example.piagent
├── agent          # Agent 核心循环
├── session        # 会话管理
├── message        # 消息模型
├── llm            # LLM Provider 抽象
├── tool           # 工具系统
│   └── builtin    # 内置工具实现
├── skill          # 技能系统
│   └── builtin    # 内置技能实现
├── prompt         # Prompt Template
├── extension      # 扩展系统
├── event          # 事件系统
├── api            # REST API
├── config         # 配置类
└── common         # 通用工具
```

## API 接口说明

### Agent 接口

```bash
# 同步聊天
curl -X POST http://localhost:8080/api/v1/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好"}'

# SSE 流式聊天
curl -X POST http://localhost:8080/api/v1/agent/chat/stream \
  -H "Content-Type: application/json" \
  -d '{"message": "你好"}'
```

### Session 接口

```bash
# 创建会话
curl -X POST http://localhost:8080/api/v1/sessions?name=新会话

# 获取会话
curl http://localhost:8080/api/v1/sessions/{sessionId}

# 列出所有会话
curl http://localhost:8080/api/v1/sessions

# 删除会话
curl -X DELETE http://localhost:8080/api/v1/sessions/{sessionId}
```

### Tool 接口

```bash
# 列出所有工具
curl http://localhost:8080/api/v1/tools

# 获取指定工具
curl http://localhost:8080/api/v1/tools/calculator
```

### Skill 接口

```bash
# 列出所有技能
curl http://localhost:8080/api/v1/skills

# 获取指定技能
curl http://localhost:8080/api/v1/skills/simple_question
```

## 核心概念说明

### AgentLoop（Agent 执行循环）

AgentLoop 是框架的核心，它循环执行以下步骤：

1. **接收用户输入**：获取用户消息
2. **构建上下文**：将历史消息、系统提示词组合
3. **调用 LLM**：发送给语言模型
4. **解析响应**：检查是否有工具调用
5. **执行工具**：如有工具调用，执行相应工具
6. **追加结果**：将工具结果添加到上下文
7. **继续循环**：直到得到最终答案或达到最大步数

### Tool（工具）

工具是 Agent 调用外部系统的能力。例如：
- `calculator`：执行数学计算
- `current_time`：获取当前时间
- `search`：搜索网络（示例扩展）

### Skill（技能）

技能是一组配置的组合：
- 系统提示词
- 允许使用的工具列表
- 最大执行步数
- Prompt 模板

### Extension（扩展）

扩展是框架的插件机制，可以在应用启动时注册工具、技能等。

### Session（会话）

会话管理对话历史，支持多轮对话。

## 扩展开发

### 新增一个 Tool

```java
// 1. 创建工具执行器
@Component
public class MyToolExecutor implements ToolExecutor {
    @Override
    public ToolExecutionResult execute(ToolExecutionRequest request) {
        // 你的工具逻辑
        return ToolExecutionResult.success("结果");
    }
}

// 2. 注册工具
@Bean
public CommandLineRunner registerMyTool(ToolRegistry registry, ToolExecutorRegistry executorRegistry) {
    return args -> {
        ToolDefinition tool = ToolDefinition.builder()
                .name("my_tool")
                .description("我的工具")
                .parameterSchema(new ToolParameterSchema(List.of(
                    new ToolParameter("input", "输入", true, "string")
                ), "object"))
                .build();
        registry.register(tool);
        executorRegistry.register("my_tool", new MyToolExecutor());
    };
}
```

### 新增一个 Skill

```java
// 1. 创建技能定义
SkillDefinition skill = SkillDefinition.builder()
        .name("my_skill")
        .description("我的技能")
        .systemPrompt("你是一个...的助手")
        .allowedTools(List.of("calculator", "search"))
        .maxSteps(5)
        .build();

// 2. 注册技能
skillRegistry.register(skill);
```

### 新增一个 Extension

```java
@Component
public class MyExtension implements Extension {
    @Override
    public String getName() {
        return "MyExtension";
    }

    @Override
    public String getDescription() {
        return "我的扩展";
    }

    @Override
    public void onLoad(ExtensionContext context) {
        // 注册工具、技能等
    }
}
```

### 切换模型供应商

```yaml
# 使用 OpenAI
piagent:
  llm:
    provider: openai
    openai:
      api-key: your-key
      model-name: gpt-4o-mini

# 使用 Ollama（本地模型）
piagent:
  llm:
    provider: ollama
    ollama:
      base-url: http://localhost:11434
      model-name: llama3.2

# 使用通义千问
piagent:
  llm:
    provider: dashscope
    dashscope:
      api-key: your-key
      model-name: qwen-plus

# 使用 DeepSeek
piagent:
  llm:
    provider: deepseek
    deepseek:
      api-key: your-key
      model-name: deepseek-chat
```

## 开发计划

- [x] 第1轮：项目骨架
- [x] 第2轮：message、session、prompt 模块
- [x] 第3轮：tool 模块
- [x] 第4轮：llm 抽象
- [x] 第5轮：AgentLoop 核心
- [x] 第6轮：skill、extension、event 模块
- [x] 第7轮：REST API 和 SSE
- [x] 第8轮：完善文档

## License

MIT
