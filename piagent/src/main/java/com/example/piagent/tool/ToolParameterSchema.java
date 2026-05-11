package com.example.piagent.tool;

import java.util.List;

/**
 * 工具参数模式定义
 *
 * 设计目的：
 * - 描述工具的完整参数结构
 * - 与 OpenAI Function Calling 格式保持兼容
 * - 支持必需参数和可选参数的区分
 */
public class ToolParameterSchema {

    private final List<ToolParameter> parameters;
    private final String type;

    public ToolParameterSchema(List<ToolParameter> parameters, String type) {
        this.parameters = parameters;
        this.type = type;
    }

    public List<ToolParameter> getParameters() {
        return parameters;
    }

    public String getType() {
        return type;
    }
}
