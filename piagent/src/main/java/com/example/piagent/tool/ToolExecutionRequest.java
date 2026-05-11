package com.example.piagent.tool;

/**
 * 工具执行请求
 *
 * 设计目的：
 * - 封装工具执行所需的输入数据
 * - 包含工具名称和参数
 * - 使用 Map 存储参数，保持灵活性
 */
public class ToolExecutionRequest {

    private final String toolName;
    private final Object arguments;

    private ToolExecutionRequest(String toolName, Object arguments) {
        this.toolName = toolName;
        this.arguments = arguments;
    }

    public static ToolExecutionRequest of(String toolName, Object arguments) {
        return new ToolExecutionRequest(toolName, arguments);
    }

    public String getToolName() {
        return toolName;
    }

    public Object getArguments() {
        return arguments;
    }
}
