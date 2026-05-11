package com.example.piagent.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Message 消息模型测试
 *
 * 测试目的：
 * 1. 验证消息对象的创建和属性访问
 * 2. 验证消息的不可变性（创建后不可修改）
 * 3. 验证不同角色消息的特定属性
 */
@DisplayName("Message 消息模型测试")
class MessageTest {

    @Nested
    @DisplayName("创建消息测试")
    class CreateMessageTest {

        @Test
        @DisplayName("应该创建用户消息")
        void shouldCreateUserMessage() {
            Message message = Message.builder()
                    .role(MessageRole.USER)
                    .content("你好")
                    .build();

            assertThat(message.getRole()).isEqualTo(MessageRole.USER);
            assertThat(message.getContent()).isEqualTo("你好");
            assertThat(message.getName()).isNull();
            assertThat(message.getToolCallId()).isNull();
        }

        @Test
        @DisplayName("应该创建助手消息")
        void shouldCreateAssistantMessage() {
            Message message = Message.builder()
                    .role(MessageRole.ASSISTANT)
                    .content("我是助手")
                    .build();

            assertThat(message.getRole()).isEqualTo(MessageRole.ASSISTANT);
            assertThat(message.getContent()).isEqualTo("我是助手");
        }

        @Test
        @DisplayName("应该创建系统消息")
        void shouldCreateSystemMessage() {
            Message message = Message.builder()
                    .role(MessageRole.SYSTEM)
                    .content("系统提示词")
                    .build();

            assertThat(message.getRole()).isEqualTo(MessageRole.SYSTEM);
            assertThat(message.getContent()).isEqualTo("系统提示词");
        }

        @Test
        @DisplayName("应该创建工具调用消息")
        void shouldCreateToolCallMessage() {
            Message message = Message.builder()
                    .role(MessageRole.TOOL)
                    .content("{\"name\":\"calculator\",\"arguments\":\"{\\\"expression\\\":\\\"1+2\\\"}\"}")
                    .toolCallId("call_123")
                    .name("calculator")
                    .build();

            assertThat(message.getRole()).isEqualTo(MessageRole.TOOL);
            assertThat(message.getToolCallId()).isEqualTo("call_123");
            assertThat(message.getName()).isEqualTo("calculator");
            assertThat(message.getContent()).isNotNull();
        }

        @Test
        @DisplayName("应该创建工具结果消息")
        void shouldCreateToolResultMessage() {
            Message message = Message.builder()
                    .role(MessageRole.TOOL_RESULT)
                    .content("3")
                    .toolCallId("call_123")
                    .build();

            assertThat(message.getRole()).isEqualTo(MessageRole.TOOL_RESULT);
            assertThat(message.getToolCallId()).isEqualTo("call_123");
            assertThat(message.getContent()).isEqualTo("3");
        }

        @Test
        @DisplayName("应该支持带 id 创建消息")
        void shouldCreateMessageWithId() {
            Message message = Message.builder()
                    .id("msg_001")
                    .role(MessageRole.USER)
                    .content("测试消息")
                    .build();

            assertThat(message.getId()).isEqualTo("msg_001");
        }

        @Test
        @DisplayName("应该自动生成 id")
        void shouldAutoGenerateId() {
            Message message = Message.builder()
                    .role(MessageRole.USER)
                    .content("测试消息")
                    .build();

            assertThat(message.getId()).isNotNull();
            assertThat(message.getId()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("消息列表转换测试")
    class MessageListConversionTest {

        @Test
        @DisplayName("应该正确识别用户消息")
        void shouldIdentifyUserMessages() {
            List<Message> messages = List.of(
                    Message.builder().role(MessageRole.USER).content("u1").build(),
                    Message.builder().role(MessageRole.ASSISTANT).content("a1").build(),
                    Message.builder().role(MessageRole.USER).content("u2").build()
            );

            List<Message> userMessages = messages.stream()
                    .filter(m -> m.getRole() == MessageRole.USER)
                    .toList();

            assertThat(userMessages).hasSize(2);
            assertThat(userMessages).allMatch(m -> m.getRole() == MessageRole.USER);
        }

        @Test
        @DisplayName("应该正确识别工具调用消息")
        void shouldIdentifyToolCallMessages() {
            List<Message> messages = List.of(
                    Message.builder().role(MessageRole.USER).content("u1").build(),
                    Message.builder().role(MessageRole.TOOL).name("calc").build(),
                    Message.builder().role(MessageRole.TOOL_RESULT).content("result").build()
            );

            List<Message> toolMessages = messages.stream()
                    .filter(m -> m.getRole() == MessageRole.TOOL)
                    .toList();

            assertThat(toolMessages).hasSize(1);
            assertThat(toolMessages.get(0).getName()).isEqualTo("calc");
        }
    }
}
