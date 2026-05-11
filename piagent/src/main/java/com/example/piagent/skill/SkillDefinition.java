package com.example.piagent.skill;

import com.example.piagent.prompt.PromptTemplate;

import java.util.List;

/**
 * 技能定义
 *
 * 设计目的：
 * - 定义技能的基本属性
 * - 技能包含系统提示词、可用工具、执行约束
 * - 使用 Builder 模式简化创建
 *
 * 为什么设计 Skill？
 * - Skill 是一组 Prompt + Tools + 执行约束的组合
 * - 便于复用常见的 Agent 配置
 * - 例如：代码审查技能、问答技能等
 */
public class SkillDefinition {

    private final String name;
    private final String description;
    private final String systemPrompt;
    private final List<String> allowedTools;
    private final int maxSteps;
    private final PromptTemplate promptTemplate;

    private SkillDefinition(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.systemPrompt = builder.systemPrompt;
        this.allowedTools = builder.allowedTools;
        this.maxSteps = builder.maxSteps;
        this.promptTemplate = builder.promptTemplate;
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

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public List<String> getAllowedTools() {
        return allowedTools;
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    public PromptTemplate getPromptTemplate() {
        return promptTemplate;
    }

    public static class Builder {
        private String name;
        private String description;
        private String systemPrompt;
        private List<String> allowedTools;
        private int maxSteps = 5;
        private PromptTemplate promptTemplate;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder systemPrompt(String systemPrompt) {
            this.systemPrompt = systemPrompt;
            return this;
        }

        public Builder allowedTools(List<String> allowedTools) {
            this.allowedTools = allowedTools;
            return this;
        }

        public Builder maxSteps(int maxSteps) {
            this.maxSteps = maxSteps;
            return this;
        }

        public Builder promptTemplate(PromptTemplate promptTemplate) {
            this.promptTemplate = promptTemplate;
            return this;
        }

        public SkillDefinition build() {
            return new SkillDefinition(this);
        }
    }
}
