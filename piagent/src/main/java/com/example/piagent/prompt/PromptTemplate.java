package com.example.piagent.prompt;

/**
 * 提示词模板
 *
 * 设计目的：
 * - 存储可复用的提示词模板
 * - 支持变量占位符 {{variable_name}}
 * - 模板可组合，便于构建复杂提示词
 *
 * 变量格式说明：
 * - 使用双大括号 {{variable_name}} 包裹变量名
 * - 变量名只能包含字母、数字、下划线
 * - 变量在渲染时被实际值替换
 *
 * 为什么用 {{}} 格式？
 * - 视觉上清晰，与 Mustache 风格一致
 * - 避免与 HTML 模板语法冲突
 */
public class PromptTemplate {

    private final String name;
    private final String template;
    private final String description;

    private PromptTemplate(Builder builder) {
        this.name = builder.name;
        this.template = builder.template;
        this.description = builder.description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getTemplate() {
        return template;
    }

    public String getDescription() {
        return description;
    }

    public static class Builder {
        private String name;
        private String template;
        private String description;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder template(String template) {
            this.template = template;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public PromptTemplate build() {
            return new PromptTemplate(this);
        }
    }
}
