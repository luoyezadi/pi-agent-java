package com.example.piagent.llm;

import com.example.piagent.message.Message;
import com.example.piagent.message.MessageFactory;
import com.example.piagent.message.MessageRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ChatModelRequest 聊天模型请求测试
 */
@DisplayName("ChatModelRequest 测试")
class ChatModelRequestTest {

    @Nested
    @DisplayName("创建请求测试")
    class CreateRequestTest {

        @Test
        @DisplayName("应该正确创建请求")
        void shouldCreateRequestCorrectly() {
            List<Message> messages = List.of(
                    MessageFactory.createUserMessage("你好")
            );

            ChatModelRequest request = ChatModelRequest.builder()
                    .messages(messages)
                    .temperature(0.7)
                    .maxTokens(100)
                    .build();

            assertThat(request.getMessages()).hasSize(1);
            assertThat(request.getTemperature()).isEqualTo(0.7);
            assertThat(request.getMaxTokens()).isEqualTo(100);
        }

        @Test
        @DisplayName("应该支持创建带工具的请求")
        void shouldCreateRequestWithTools() {
            List<Message> messages = List.of(
                    MessageFactory.createUserMessage("计算 1+2")
            );

            ChatModelRequest request = ChatModelRequest.builder()
                    .messages(messages)
                    .tools(List.of("calculator"))
                    .build();

            assertThat(request.getTools()).hasSize(1);
            assertThat(request.getTools()).contains("calculator");
        }
    }
}
