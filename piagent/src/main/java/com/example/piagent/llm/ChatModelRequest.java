package com.example.piagent.llm;

import com.example.piagent.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天模型请求
 *
 * 设计目的：
 * - 封装发送给 LLM 的请求参数
 * - 统一请求格式，便于不同模型供应商适配
 * - 支持工具调用配置
 */
public class ChatModelRequest {

    private final List<Message> messages;
    private final Double temperature;
    private final Integer maxTokens;
    private final List<String> tools;
    private final String systemPrompt;

    private ChatModelRequest(Builder builder) {
        this.messages = new ArrayList<>(builder.messages);
        this.temperature = builder.temperature;
        this.maxTokens = builder.maxTokens;
        this.tools = builder.tools != null ? new ArrayList<>(builder.tools) : null;
        this.systemPrompt = builder.systemPrompt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public Double getTemperature() {
        return temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public List<String> getTools() {
        return tools != null ? new ArrayList<>(tools) : null;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public static class Builder {
        private List<Message> messages = new ArrayList<>();
        private Double temperature;
        private Integer maxTokens;
        private List<String> tools;
        private String systemPrompt;

        public Builder messages(List<Message> messages) {
            this.messages = messages;
            return this;
        }

        public Builder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder maxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public Builder tools(List<String> tools) {
            this.tools = tools;
            return this;
        }

        public Builder systemPrompt(String systemPrompt) {
            this.systemPrompt = systemPrompt;
            return this;
        }

        public ChatModelRequest build() {
            return new ChatModelRequest(this);
        }
    }
}
