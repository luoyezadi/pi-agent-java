package com.example.piagent.message;

import java.util.UUID;

/**
 * 消息模型
 *
 * 设计目的：
 * - 统一消息数据结构，支持多种消息角色
 * - 使用 Builder 模式简化创建过程
 * - 消息不可变，确保线程安全
 *
 * 字段说明：
 * - id: 消息唯一标识符
 * - role: 消息角色（user/assistant/system/tool/tool_result）
 * - content: 消息内容
 * - name: 工具名称（仅 tool 角色使用）
 * - toolCallId: 工具调用 ID（用于关联 tool 和 tool_result）
 *
 * 为什么设计不可变？
 * - 多线程环境下，避免意外修改
 * - 消息作为历史记录，应该保持不变
 */
public class Message {

    private final String id;
    private final MessageRole role;
    private final String content;
    private final String name;
    private final String toolCallId;

    private Message(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.role = builder.role;
        this.content = builder.content;
        this.name = builder.name;
        this.toolCallId = builder.toolCallId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public MessageRole getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public String getToolCallId() {
        return toolCallId;
    }

    public static class Builder {
        private String id;
        private MessageRole role;
        private String content;
        private String name;
        private String toolCallId;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder role(MessageRole role) {
            this.role = role;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder toolCallId(String toolCallId) {
            this.toolCallId = toolCallId;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }
}
