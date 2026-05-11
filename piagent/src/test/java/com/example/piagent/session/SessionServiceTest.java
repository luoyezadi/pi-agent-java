package com.example.piagent.session;

import com.example.piagent.common.BizException;
import com.example.piagent.common.ErrorCode;
import com.example.piagent.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * SessionService 会话服务测试
 *
 * 测试目的：
 * 1. 验证会话创建、查询、删除功能
 * 2. 验证消息添加功能
 * 3. 验证会话不存在时的异常处理
 * 4. 验证消息历史查询功能
 */
@DisplayName("SessionService 会话服务测试")
class SessionServiceTest {

    private SessionService sessionService;
    private InMemorySessionRepository sessionRepository;

    @BeforeEach
    void setUp() {
        sessionRepository = new InMemorySessionRepository();
        sessionService = new SessionService(sessionRepository);
    }

    @Nested
    @DisplayName("创建会话测试")
    class CreateSessionTest {

        @Test
        @DisplayName("应该成功创建会话")
        void shouldCreateSessionSuccessfully() {
            AgentSession session = sessionService.createSession("测试会话");

            assertThat(session).isNotNull();
            assertThat(session.getId()).isNotNull();
            assertThat(session.getName()).isEqualTo("测试会话");
        }

        @Test
        @DisplayName("创建的会话应该可以被查询")
        void createdSessionShouldBeFindable() {
            AgentSession created = sessionService.createSession("会话1");
            AgentSession found = sessionService.getSession(created.getId());

            assertThat(found).isNotNull();
            assertThat(found.getId()).isEqualTo(created.getId());
        }
    }

    @Nested
    @DisplayName("获取会话测试")
    class GetSessionTest {

        @Test
        @DisplayName("应该成功获取已存在的会话")
        void shouldGetExistingSession() {
            AgentSession created = sessionService.createSession("会话1");

            AgentSession result = sessionService.getSession(created.getId());

            assertThat(result.getId()).isEqualTo(created.getId());
        }

        @Test
        @DisplayName("获取不存在的会话应该抛出异常")
        void shouldThrowExceptionWhenSessionNotFound() {
            assertThatThrownBy(() -> sessionService.getSession("non_existent"))
                    .isInstanceOf(BizException.class)
                    .satisfies(e -> assertThat(((BizException) e).getCode())
                            .isEqualTo(ErrorCode.SESSION_NOT_FOUND.getCode()));
        }
    }

    @Nested
    @DisplayName("添加消息测试")
    class AddMessageTest {

        @Test
        @DisplayName("应该成功添加消息到会话")
        void shouldAddMessageSuccessfully() {
            AgentSession session = sessionService.createSession("会话1");
            Message message = Message.builder()
                    .role(com.example.piagent.message.MessageRole.USER)
                    .content("你好")
                    .build();

            sessionService.addMessage(session.getId(), message);

            AgentSession updated = sessionService.getSession(session.getId());
            assertThat(updated.getMessages()).hasSize(1);
        }

        @Test
        @DisplayName("添加到不存在的会话应该抛出异常")
        void shouldThrowExceptionWhenAddingToNonExistentSession() {
            Message message = Message.builder()
                    .role(com.example.piagent.message.MessageRole.USER)
                    .content("你好")
                    .build();

            assertThatThrownBy(() -> sessionService.addMessage("non_existent", message))
                    .isInstanceOf(BizException.class)
                    .satisfies(e -> assertThat(((BizException) e).getCode())
                            .isEqualTo(ErrorCode.SESSION_NOT_FOUND.getCode()));
        }
    }

    @Nested
    @DisplayName("获取消息历史测试")
    class GetMessageHistoryTest {

        @Test
        @DisplayName("应该返回会话的消息历史")
        void shouldReturnMessageHistory() {
            AgentSession session = sessionService.createSession("会话1");
            sessionService.addMessage(session.getId(),
                    com.example.piagent.message.MessageFactory.createUserMessage("你好"));
            sessionService.addMessage(session.getId(),
                    com.example.piagent.message.MessageFactory.createAssistantMessage("你好！"));

            List<Message> history = sessionService.getMessageHistory(session.getId());

            assertThat(history).hasSize(2);
        }

        @Test
        @DisplayName("空会话应该返回空列表")
        void shouldReturnEmptyListForEmptySession() {
            AgentSession session = sessionService.createSession("会话1");

            List<Message> history = sessionService.getMessageHistory(session.getId());

            assertThat(history).isEmpty();
        }

        @Test
        @DisplayName("获取不存在会话的历史应该抛出异常")
        void shouldThrowExceptionWhenGettingHistoryOfNonExistentSession() {
            assertThatThrownBy(() -> sessionService.getMessageHistory("non_existent"))
                    .isInstanceOf(BizException.class)
                    .satisfies(e -> assertThat(((BizException) e).getCode())
                            .isEqualTo(ErrorCode.SESSION_NOT_FOUND.getCode()));
        }
    }

    @Nested
    @DisplayName("删除会话测试")
    class DeleteSessionTest {

        @Test
        @DisplayName("应该成功删除会话")
        void shouldDeleteSessionSuccessfully() {
            AgentSession session = sessionService.createSession("会话1");

            boolean deleted = sessionService.deleteSession(session.getId());

            assertThat(deleted).isTrue();
            assertThatThrownBy(() -> sessionService.getSession(session.getId()))
                    .isInstanceOf(BizException.class);
        }

        @Test
        @DisplayName("删除不存在的会话应该返回 false")
        void shouldReturnFalseWhenDeletingNonExistent() {
            boolean deleted = sessionService.deleteSession("non_existent");

            assertThat(deleted).isFalse();
        }
    }

    @Nested
    @DisplayName("会话列表测试")
    class ListSessionsTest {

        @Test
        @DisplayName("应该返回所有会话")
        void shouldReturnAllSessions() {
            sessionService.createSession("会话1");
            sessionService.createSession("会话2");

            List<AgentSession> sessions = sessionService.listSessions();

            assertThat(sessions).hasSize(2);
        }

        @Test
        @DisplayName("空时应该返回空列表")
        void shouldReturnEmptyListWhenNoSessions() {
            List<AgentSession> sessions = sessionService.listSessions();

            assertThat(sessions).isEmpty();
        }
    }
}
