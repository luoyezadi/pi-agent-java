package com.example.piagent.tool;

/**
 * 工具定义
 *
 * 设计目的：
 * - 描述工具的元信息（名称、描述、参数模式）
 * - 工具定义与工具执行分离，便于注册和查询
 * - 使用 Builder 模式简化创建过程
 *
 * 为什么分离定义和执行？
 * - 定义可以持久化、序列化、传输
 * - 执行逻辑由 ToolExecutor 处理
 * - 便于 LLM 理解可用的工具
 */
public class ToolDefinition {

    private final String name;
    private final String description;
    private final ToolParameterSchema parameterSchema;

    private ToolDefinition(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.parameterSchema = builder.parameterSchema;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ToolParameterSchema getParameterSchema() {
        return parameterSchema;
    }

    public static class Builder {
        private String name;
        private String description;
        private ToolParameterSchema parameterSchema;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder parameterSchema(ToolParameterSchema parameterSchema) {
            this.parameterSchema = parameterSchema;
            return this;
        }

        public ToolDefinition build() {
            return new ToolDefinition(this);
        }
    }
}
