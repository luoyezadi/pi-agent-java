package com.example.piagent.agent;

import com.example.piagent.llm.ChatModelClient;
import com.example.piagent.llm.ChatModelRequest;
import com.example.piagent.llm.ChatModelResponse;
import com.example.piagent.message.Message;
import com.example.piagent.message.MessageFactory;
import com.example.piagent.message.MessageRole;
import com.example.piagent.session.AgentSession;
import com.example.piagent.session.SessionService;
import com.example.piagent.tool.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AgentLoop 核心循环测试
 *
 * 测试目的：
 * 1. 验证普通问答流程
 * 2. 验证工具调用流程
 * 3. 验证最大步数终止流程
 * 4. 验证模型异常处理
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AgentLoop 核心循环测试")
class AgentLoopTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private ToolService toolService;

    private MockChatModelClient mockChatModelClient;

    private AgentLoop createAgentLoop(int maxSteps) {
        mockChatModelClient = new MockChatModelClient();
        return new AgentLoop(mockChatModelClient, sessionService, toolService, maxSteps);
    }

    @Nested
    @DisplayName("普通问答流程测试")
    class SimpleConversationTest {

        @Test
        @DisplayName("应该成功处理简单问答")
        void shouldHandleSimpleQuestion() {
            AgentLoop agentLoop = createAgentLoop(5);
            AgentSession session = AgentSession.builder().id("session_1").build();
            when(sessionService.createSession(any())).thenReturn(session);

            AgentRequest request = AgentRequest.builder()
                    .userMessage("你好")
                    .build();

            AgentResponse response = agentLoop.execute(request);

            assertThat(response).isNotNull();
            assertThat(response.getSessionId()).isEqualTo("session_1");
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getSteps()).isNotEmpty();
            assertThat(session.getMessages()).isNotEmpty();
        }

        @Test
        @DisplayName("应该处理用户回复并继续对话")
        void shouldContinueConversation() {
            AgentLoop agentLoop = createAgentLoop(5);
            AgentSession session = AgentSession.builder().id("session_1").build();
            session.addMessage(MessageFactory.createUserMessage("你好"));
            session.addMessage(MessageFactory.createAssistantMessage("你好！有什么可以帮助您的？"));
            when(sessionService.getSession("session_1")).thenReturn(session);

            AgentRequest request = AgentRequest.builder()
                    .sessionId("session_1")
                    .userMessage("谢谢，没什么了")
                    .build();

            AgentResponse response = agentLoop.execute(request);

            assertThat(response.isSuccess()).isTrue();
            assertThat(session.getMessages()).hasSizeGreaterThan(2);
        }
    }

    @Nested
    @DisplayName("工具调用流程测试")
    class ToolCallTest {

        @Test
        @DisplayName("当模型请求工具调用时应执行工具")
        void shouldExecuteToolWhenModelRequestsToolCall() {
            AgentLoop agentLoop = createAgentLoop(5);
            AgentSession session = AgentSession.builder().id("session_1").build();
            when(sessionService.createSession(any())).thenReturn(session);

            ToolDefinition calculatorTool = ToolDefinition.builder()
                    .name("calculator")
                    .description("计算器")
                    .build();
            when(toolService.getAllTools()).thenReturn(List.of(calculatorTool));
            when(toolService.hasTool("calculator")).thenReturn(true);
            when(toolService.executeTool(any())).thenReturn(ToolExecutionResult.success("3"));

            AgentRequest request = AgentRequest.builder()
                    .userMessage("计算 1+2")
                    .build();

            AgentResponse response = agentLoop.execute(request);

            assertThat(response.isSuccess()).isTrue();
            verify(toolService, atLeastOnce()).executeTool(any());
        }

        @Test
        @DisplayName("工具执行失败后应继续执行")
        void shouldContinueAfterToolFailure() {
            AgentLoop agentLoop = createAgentLoop(5);
            AgentSession session = AgentSession.builder().id("session_1").build();
            when(sessionService.createSession(any())).thenReturn(session);

            when(toolService.getAllTools()).thenReturn(List.of());
            when(toolService.executeTool(any())).thenReturn(ToolExecutionResult.failure("工具不存在"));

            AgentRequest request = AgentRequest.builder()
                    .userMessage("计算 1+2")
                    .build();

            AgentResponse response = agentLoop.execute(request);

            assertThat(response).isNotNull();
        }
    }

    @Nested
    @DisplayName("最大步数终止测试")
    class MaxStepsTerminationTest {

        @Test
        @DisplayName("达到最大步数时应停止执行")
        void shouldStopWhenMaxStepsReached() {
            AgentLoop agentLoop = createAgentLoop(2);
            AgentSession session = AgentSession.builder().id("session_1").build();
            when(sessionService.createSession(any())).thenReturn(session);
            when(toolService.getAllTools()).thenReturn(List.of());

            AgentRequest request = AgentRequest.builder()
                    .userMessage("测试")
                    .build();

            AgentResponse response = agentLoop.execute(request);

            assertThat(response.getSteps().size()).isLessThanOrEqualTo(2);
        }

        @Test
        @DisplayName("应该在响应中标记是否达到最大步数")
        void shouldMarkMaxStepsReached() {
            AgentLoop agentLoop = createAgentLoop(1);
            AgentSession session = AgentSession.builder().id("session_1").build();
            when(sessionService.createSession(any())).thenReturn(session);
            when(toolService.getAllTools()).thenReturn(List.of());

            AgentRequest request = AgentRequest.builder()
                    .userMessage("测试")
                    .build();

            AgentResponse response = agentLoop.execute(request);

            assertThat(response.isMaxStepsReached()).isFalse();
        }
    }

    @Nested
    @DisplayName("模型异常处理测试")
    class ModelExceptionTest {

        @Test
        @DisplayName("模型调用失败时应返回失败响应")
        void shouldReturnFailureWhenModelCallFails() {
            ChatModelClient failingClient = mock(ChatModelClient.class);
            when(failingClient.chat(any())).thenThrow(new RuntimeException("模型调用失败"));
            when(sessionService.createSession(any())).thenReturn(AgentSession.builder().id("session_1").build());

            AgentLoop agentLoop = new AgentLoop(failingClient, sessionService, toolService, 5);

            AgentRequest request = AgentRequest.builder()
                    .userMessage("测试")
                    .build();

            AgentResponse response = agentLoop.execute(request);

            assertThat(response.isSuccess()).isFalse();
        }
    }

    @Nested
    @DisplayName("消息历史测试")
    class MessageHistoryTest {

        @Test
        @DisplayName("应该将所有消息添加到会话历史")
        void shouldAddAllMessagesToSessionHistory() {
            AgentLoop agentLoop = createAgentLoop(5);
            AgentSession session = AgentSession.builder().id("session_1").build();
            when(sessionService.createSession(any())).thenReturn(session);

            AgentRequest request = AgentRequest.builder()
                    .userMessage("你好")
                    .build();

            agentLoop.execute(request);

            assertThat(session.getMessages()).isNotEmpty();
            assertThat(session.getMessages().get(0).getRole()).isEqualTo(MessageRole.USER);
        }

        @Test
        @DisplayName("应该保留之前的消息历史")
        void shouldPreservePreviousHistory() {
            AgentLoop agentLoop = createAgentLoop(5);
            AgentSession session = AgentSession.builder().id("session_1").build();
            session.addMessage(MessageFactory.createSystemMessage("你是一个有帮助的助手"));
            when(sessionService.getSession("session_1")).thenReturn(session);

            AgentRequest request = AgentRequest.builder()
                    .sessionId("session_1")
                    .userMessage("你好")
                    .build();

            agentLoop.execute(request);

            assertThat(session.getMessages().stream()
                    .anyMatch(m -> m.getRole() == MessageRole.SYSTEM)).isTrue();
        }
    }
}
