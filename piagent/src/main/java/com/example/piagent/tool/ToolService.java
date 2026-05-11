package com.example.piagent.tool;

import com.example.piagent.tool.builtin.ToolExecutorRegistry;

/**
 * 工具调用服务
 *
 * 设计目的：
 * - 提供统一的工具调用入口
 * - 协调 ToolRegistry 和 ToolExecutorRegistry
 * - 简化 AgentLoop 的工具调用逻辑
 *
 * 为什么需要单独的服务类？
 * - 封装查找和执行的两个步骤
 * - 便于添加日志、监控等横切关注点
 * - 单一职责：工具调用逻辑集中管理
 */
public class ToolService {

    private final ToolRegistry toolRegistry;
    private final ToolExecutorRegistry executorRegistry;

    public ToolService(ToolRegistry toolRegistry, ToolExecutorRegistry executorRegistry) {
        this.toolRegistry = toolRegistry;
        this.executorRegistry = executorRegistry;
    }

    /**
     * 执行工具
     *
     * @param request 执行请求
     * @return 执行结果
     */
    public ToolExecutionResult executeTool(ToolExecutionRequest request) {
        String toolName = request.getToolName();

        if (!toolRegistry.get(toolName).isPresent()) {
            return ToolExecutionResult.failure("工具不存在: " + toolName);
        }

        ToolExecutor executor = executorRegistry.get(toolName);
        if (executor == null) {
            return ToolExecutionResult.failure("工具执行器未注册: " + toolName);
        }

        return executor.execute(request);
    }

    /**
     * 获取所有可用工具
     */
    public java.util.List<ToolDefinition> getAllTools() {
        return toolRegistry.getAll();
    }

    /**
     * 检查工具是否存在
     */
    public boolean hasTool(String toolName) {
        return toolRegistry.get(toolName).isPresent();
    }
}
