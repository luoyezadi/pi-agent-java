package com.example.piagent.event;

import java.time.Instant;

/**
 * Agent 事件模型
 *
 * 设计目的：
 * - 封装事件的所有信息
 * - 包含事件类型、时间戳、关联数据
 */
public class AgentEvent {

    private final AgentEventType type;
    private final Instant timestamp;
    private final String sessionId;
    private final String content;
    private final Object data;

    private AgentEvent(Builder builder) {
        this.type = builder.type;
        this.timestamp = builder.timestamp != null ? builder.timestamp : Instant.now();
        this.sessionId = builder.sessionId;
        this.content = builder.content;
        this.data = builder.data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public AgentEventType getType() {
        return type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getContent() {
        return content;
    }

    public Object getData() {
        return data;
    }

    public static class Builder {
        private AgentEventType type;
        private Instant timestamp;
        private String sessionId;
        private String content;
        private Object data;

        public Builder type(AgentEventType type) {
            this.type = type;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public AgentEvent build() {
            return new AgentEvent(this);
        }
    }
}
