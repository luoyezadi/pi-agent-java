package com.example.piagent.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MessageFactory 消息工厂测试
 *
 * 测试目的：
 * 1. 验证工厂方法创建不同角色消息
 * 2. 验证工厂方法简化消息创建过程
 */
@DisplayName("MessageFactory 消息工厂测试")
class MessageFactoryTest {

    @Nested
    @DisplayName("创建用户消息测试")
    class CreateUserMessageTest {

        @Test
        @DisplayName("应该创建简单的用户消息")
        void shouldCreateSimpleUserMessage() {
            Message message = MessageFactory.createUserMessage("你好");

            assertThat(message.getRole()).isEqualTo(MessageRole.USER);
            assertThat(message.getContent()).isEqualTo("你好");
            assertThat(message.getId()).isNotNull();
        }

        @Test
        @DisplayName("应该创建带 id 的用户消息")
        void shouldCreateUserMessageWithId() {
            Message message = MessageFactory.createUserMessage("msg_001", "你好");

            assertThat(message.getId()).isEqualTo("msg_001");
            assertThat(message.getRole()).isEqualTo(MessageRole.USER);
            assertThat(message.getContent()).isEqualTo("你好");
        }
    }

    @Nested
    @DisplayName("创建助手消息测试")
    class CreateAssistantMessageTest {

        @Test
        @DisplayName("应该创建助手消息")
        void shouldCreateAssistantMessage() {
            Message message = MessageFactory.createAssistantMessage("我是助手");

            assertThat(message.getRole()).isEqualTo(MessageRole.ASSISTANT);
            assertThat(message.getContent()).isEqualTo("我是助手");
        }

        @Test
        @DisplayName("应该创建带 id 的助手消息")
        void shouldCreateAssistantMessageWithId() {
            Message message = MessageFactory.createAssistantMessage("msg_002", "回复内容");

            assertThat(message.getId()).isEqualTo("msg_002");
            assertThat(message.getContent()).isEqualTo("回复内容");
        }
    }

    @Nested
    @DisplayName("创建系统消息测试")
    class CreateSystemMessageTest {

        @Test
        @DisplayName("应该创建系统消息")
        void shouldCreateSystemMessage() {
            Message message = MessageFactory.createSystemMessage("系统提示词");

            assertThat(message.getRole()).isEqualTo(MessageRole.SYSTEM);
            assertThat(message.getContent()).isEqualTo("系统提示词");
        }
    }

    @Nested
    @DisplayName("创建工具相关消息测试")
    class CreateToolRelatedMessageTest {

        @Test
        @DisplayName("应该创建工具调用消息")
        void shouldCreateToolCallMessage() {
            Message message = MessageFactory.createToolCallMessage(
                    "call_123",
                    "calculator",
                    "{\"expression\":\"1+2\"}"
            );

            assertThat(message.getRole()).isEqualTo(MessageRole.TOOL);
            assertThat(message.getToolCallId()).isEqualTo("call_123");
            assertThat(message.getName()).isEqualTo("calculator");
            assertThat(message.getContent()).contains("1+2");
        }

        @Test
        @DisplayName("应该创建工具结果消息")
        void shouldCreateToolResultMessage() {
            Message message = MessageFactory.createToolResultMessage(
                    "call_123",
                    "计算结果是 3"
            );

            assertThat(message.getRole()).isEqualTo(MessageRole.TOOL_RESULT);
            assertThat(message.getToolCallId()).isEqualTo("call_123");
            assertThat(message.getContent()).isEqualTo("计算结果是 3");
        }
    }
}
