package com.example.piagent.llm;

import com.example.piagent.message.Message;
import com.example.piagent.message.MessageFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 聊天模型响应
 *
 * 设计目的：
 * - 封装 LLM 返回的结果
 * - 区分文本回复和工具调用
 * - 使用 Optional 处理可能不存在的字段
 */
public class ChatModelResponse {

    private final Message message;
    private final boolean hasToolCalls;
    private final List<ToolCall> toolCalls;

    private ChatModelResponse(Builder builder) {
        this.message = builder.message;
        this.hasToolCalls = builder.hasToolCalls;
        this.toolCalls = builder.toolCalls != null ? new ArrayList<>(builder.toolCalls) : null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Message getMessage() {
        return message;
    }

    public boolean hasToolCalls() {
        return hasToolCalls;
    }

    public List<ToolCall> getToolCalls() {
        return toolCalls != null ? new ArrayList<>(toolCalls) : null;
    }

    public Optional<String> getTextContent() {
        return message != null && message.getContent() != null
                ? Optional.of(message.getContent())
                : Optional.empty();
    }

    public static class Builder {
        private Message message;
        private boolean hasToolCalls;
        private List<ToolCall> toolCalls;

        public Builder message(Message message) {
            this.message = message;
            return this;
        }

        public Builder hasToolCalls(boolean hasToolCalls) {
            this.hasToolCalls = hasToolCalls;
            return this;
        }

        public Builder toolCalls(List<ToolCall> toolCalls) {
            this.toolCalls = toolCalls;
            return this;
        }

        public ChatModelResponse build() {
            return new ChatModelResponse(this);
        }
    }

    /**
     * 工具调用信息
     */
    public static class ToolCall {
        private final String id;
        private final String name;
        private final String arguments;

        public ToolCall(String id, String name, String arguments) {
            this.id = id;
            this.name = name;
            this.arguments = arguments;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getArguments() {
            return arguments;
        }
    }
}
