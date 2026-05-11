package com.example.piagent.tool;

/**
 * 工具参数定义
 *
 * 设计目的：
 * - 描述单个参数的元信息
 * - 便于 LLM 理解工具需要什么参数
 * - 支持参数验证和文档生成
 */
public class ToolParameter {

    private final String name;
    private final String description;
    private final boolean required;
    private final String type;

    public ToolParameter(String name, String description, boolean required, String type) {
        this.name = name;
        this.description = description;
        this.required = required;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }

    public String getType() {
        return type;
    }
}
