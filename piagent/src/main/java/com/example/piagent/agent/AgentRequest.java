package com.example.piagent.agent;

/**
 * Agent 请求
 *
 * 设计目的：
 * - 封装发送给 Agent 的输入参数
 * - 支持创建新会话或继续已有会话
 */
public class AgentRequest {

    private final String sessionId;
    private final String userMessage;
    private final String systemPrompt;
    private final String skillName;

    private AgentRequest(Builder builder) {
        this.sessionId = builder.sessionId;
        this.userMessage = builder.userMessage;
        this.systemPrompt = builder.systemPrompt;
        this.skillName = builder.skillName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public String getSkillName() {
        return skillName;
    }

    public static class Builder {
        private String sessionId;
        private String userMessage;
        private String systemPrompt;
        private String skillName;

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder userMessage(String userMessage) {
            this.userMessage = userMessage;
            return this;
        }

        public Builder systemPrompt(String systemPrompt) {
            this.systemPrompt = systemPrompt;
            return this;
        }

        public Builder skillName(String skillName) {
            this.skillName = skillName;
            return this;
        }

        public AgentRequest build() {
            return new AgentRequest(this);
        }
    }
}
