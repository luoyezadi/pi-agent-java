package com.example.piagent.message;

/**
 * 消息工厂类
 *
 * 设计目的：
 * - 提供便捷的工厂方法，简化消息创建
 * - 统一消息创建入口，便于后续扩展
 * - 减少重复的 Builder 代码
 *
 * 为什么使用工厂方法而不是直接 new？
 * - 代码更简洁，调用方不需要关心 Builder 模式
 * - 便于后续在工厂中添加默认值、校验逻辑
 * - 符合简单对象直接创建、复杂对象通过工厂的原则
 */
public final class MessageFactory {

    private MessageFactory() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 创建用户消息
     */
    public static Message createUserMessage(String content) {
        return Message.builder()
                .role(MessageRole.USER)
                .content(content)
                .build();
    }

    /**
     * 创建带 ID 的用户消息
     */
    public static Message createUserMessage(String id, String content) {
        return Message.builder()
                .id(id)
                .role(MessageRole.USER)
                .content(content)
                .build();
    }

    /**
     * 创建助手消息
     */
    public static Message createAssistantMessage(String content) {
        return Message.builder()
                .role(MessageRole.ASSISTANT)
                .content(content)
                .build();
    }

    /**
     * 创建带 ID 的助手消息
     */
    public static Message createAssistantMessage(String id, String content) {
        return Message.builder()
                .id(id)
                .role(MessageRole.ASSISTANT)
                .content(content)
                .build();
    }

    /**
     * 创建系统消息
     */
    public static Message createSystemMessage(String content) {
        return Message.builder()
                .role(MessageRole.SYSTEM)
                .content(content)
                .build();
    }

    /**
     * 创建工具调用消息
     *
     * @param toolCallId 工具调用 ID（用于关联结果）
     * @param toolName   工具名称
     * @param arguments  工具参数（JSON 格式）
     */
    public static Message createToolCallMessage(String toolCallId, String toolName, String arguments) {
        return Message.builder()
                .role(MessageRole.TOOL)
                .toolCallId(toolCallId)
                .name(toolName)
                .content(arguments)
                .build();
    }

    /**
     * 创建工具结果消息
     *
     * @param toolCallId 关联的工具调用 ID
     * @param result     工具执行结果
     */
    public static Message createToolResultMessage(String toolCallId, String result) {
        return Message.builder()
                .role(MessageRole.TOOL_RESULT)
                .toolCallId(toolCallId)
                .content(result)
                .build();
    }
}
