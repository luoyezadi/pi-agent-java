package com.example.piagent.message;

import java.util.Optional;

/**
 * 消息角色枚举
 *
 * 设计目的：
 * - 统一消息类型，避免使用字符串硬编码
 * - 与 LangChain4j 的消息角色保持兼容
 * - 便于扩展新的消息类型
 *
 * 角色说明：
 * - USER: 用户发送的消息
 * - ASSISTANT: AI 助手生成的消息
 * - SYSTEM: 系统级提示词
 * - TOOL: 工具调用请求（包含工具名称和参数）
 * - TOOL_RESULT: 工具执行结果
 */
public enum MessageRole {

    USER("user"),
    ASSISTANT("assistant"),
    SYSTEM("system"),
    TOOL("tool"),
    TOOL_RESULT("tool_result");

    private final String value;

    MessageRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Optional<MessageRole> fromValue(String value) {
        if (value == null) {
            return Optional.empty();
        }
        for (MessageRole role : values()) {
            if (role.value.equals(value)) {
                return Optional.of(role);
            }
        }
        return Optional.empty();
    }
}
