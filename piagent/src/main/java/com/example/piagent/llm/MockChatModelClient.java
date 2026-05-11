package com.example.piagent.llm;

import com.example.piagent.message.Message;
import com.example.piagent.message.MessageFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mock 聊天模型客户端（用于测试）
 *
 * 设计目的：
 * - 提供可预测响应的 Mock 实现
 * - 支持模拟文本回复和工具调用
 * - 便于 AgentLoop 的单元测试
 *
 * Mock 行为说明：
 * - 如果消息中包含"计算"或"1+2"，返回 calculator 工具调用
 * - 如果消息中包含"时间"，返回 current_time 工具调用
 * - 否则返回简单的文本回复
 */
public class MockChatModelClient implements ChatModelClient {

    private static final Pattern CALCULATOR_PATTERN = Pattern.compile("(计算|1\\+2|\\d+[+\\-*/]\\d+)");
    private static final Pattern TIME_PATTERN = Pattern.compile("(时间|现在几点了)");
    private static final Pattern QUIT_PATTERN = Pattern.compile("(再见|退出|quit|exit)");

    private final String modelName;
    private final Map<String, List<ChatModelResponse>> predefinedResponses = new ConcurrentHashMap<>();

    public MockChatModelClient() {
        this("mock-model");
    }

    public MockChatModelClient(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public ChatModelResponse chat(ChatModelRequest request) {
        if (request == null || request.getMessages() == null || request.getMessages().isEmpty()) {
            return createTextResponse("请问有什么可以帮助您的？");
        }

        Message lastMessage = request.getMessages().get(request.getMessages().size() - 1);
        String content = lastMessage.getContent() != null ? lastMessage.getContent() : "";

        if (QUIT_PATTERN.matcher(content).find()) {
            return createTextResponse("再见！有任何问题随时找我。");
        }

        if (CALCULATOR_PATTERN.matcher(content).find() && request.getTools() != null && request.getTools().contains("calculator")) {
            return createCalculatorToolCallResponse();
        }

        if (TIME_PATTERN.matcher(content).find() && request.getTools() != null && request.getTools().contains("current_time")) {
            return createTimeToolCallResponse();
        }

        if (request.getTools() != null && !request.getTools().isEmpty()) {
            return createTextResponse("我需要使用工具来帮助您。请问具体需要我做什么？");
        }

        return createTextResponse("这是 Mock 模型的回复：" + content);
    }

    private ChatModelResponse createTextResponse(String text) {
        return ChatModelResponse.builder()
                .message(MessageFactory.createAssistantMessage(text))
                .hasToolCalls(false)
                .build();
    }

    private ChatModelResponse createCalculatorToolCallResponse() {
        String callId = "call_" + System.currentTimeMillis();
        return ChatModelResponse.builder()
                .message(MessageFactory.createAssistantMessage("让我帮您计算..."))
                .hasToolCalls(true)
                .toolCalls(List.of(
                        new ChatModelResponse.ToolCall(callId, "calculator", "{\"expression\":\"1+2\"}")
                ))
                .build();
    }

    private ChatModelResponse createTimeToolCallResponse() {
        String callId = "call_" + System.currentTimeMillis();
        return ChatModelResponse.builder()
                .message(MessageFactory.createAssistantMessage("让我查看一下当前时间..."))
                .hasToolCalls(true)
                .toolCalls(List.of(
                        new ChatModelResponse.ToolCall(callId, "current_time", "{}")
                ))
                .build();
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    @Override
    public boolean supportsTools() {
        return true;
    }

    /**
     * 添加预定义的响应序列（用于测试复杂对话流程）
     */
    public void addPredefinedResponse(String key, List<ChatModelResponse> responses) {
        predefinedResponses.put(key, responses);
    }
}
