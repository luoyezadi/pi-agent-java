package com.example.piagent.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MessageRole 消息角色枚举测试
 *
 * 测试目的：验证消息角色枚举值是否正确定义
 */
@DisplayName("MessageRole 消息角色测试")
class MessageRoleTest {

    @Test
    @DisplayName("应该包含所有必需的消息角色")
    void shouldContainAllRequiredRoles() {
        assertThat(MessageRole.values())
                .containsExactlyInAnyOrder(
                        MessageRole.USER,
                        MessageRole.ASSISTANT,
                        MessageRole.SYSTEM,
                        MessageRole.TOOL,
                        MessageRole.TOOL_RESULT
                );
    }

    @Test
    @DisplayName("USER 角色应该是用户消息")
    void userRoleShouldBeUserMessage() {
        assertThat(MessageRole.USER.getValue()).isEqualTo("user");
    }

    @Test
    @DisplayName("ASSISTANT 角色应该是助手消息")
    void assistantRoleShouldBeAssistantMessage() {
        assertThat(MessageRole.ASSISTANT.getValue()).isEqualTo("assistant");
    }

    @Test
    @DisplayName("SYSTEM 角色应该是系统消息")
    void systemRoleShouldBeSystemMessage() {
        assertThat(MessageRole.SYSTEM.getValue()).isEqualTo("system");
    }

    @Test
    @DisplayName("TOOL 角色应该是工具调用消息")
    void toolRoleShouldBeToolMessage() {
        assertThat(MessageRole.TOOL.getValue()).isEqualTo("tool");
    }

    @Test
    @DisplayName("TOOL_RESULT 角色应该是工具结果消息")
    void toolResultRoleShouldBeToolResultMessage() {
        assertThat(MessageRole.TOOL_RESULT.getValue()).isEqualTo("tool_result");
    }

    @Test
    @DisplayName("应该能从字符串值获取枚举")
    void shouldGetEnumFromStringValue() {
        assertThat(MessageRole.fromValue("user")).contains(MessageRole.USER);
        assertThat(MessageRole.fromValue("assistant")).contains(MessageRole.ASSISTANT);
        assertThat(MessageRole.fromValue("system")).contains(MessageRole.SYSTEM);
        assertThat(MessageRole.fromValue("tool")).contains(MessageRole.TOOL);
        assertThat(MessageRole.fromValue("tool_result")).contains(MessageRole.TOOL_RESULT);
    }

    @Test
    @DisplayName("应该对未知值返回 Optional.empty")
    void shouldReturnEmptyForUnknownValue() {
        assertThat(MessageRole.fromValue("unknown")).isEmpty();
        assertThat(MessageRole.fromValue("")).isEmpty();
    }
}
