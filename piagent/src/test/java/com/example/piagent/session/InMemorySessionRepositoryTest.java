package com.example.piagent.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * InMemorySessionRepository 内存会话仓库测试
 *
 * 测试目的：
 * 1. 验证会话的创建、查询、删除功能
 * 2. 验证会话不存在时的异常处理
 * 3. 验证会话列表查询功能
 */
@DisplayName("InMemorySessionRepository 内存会话仓库测试")
class InMemorySessionRepositoryTest {

    private InMemorySessionRepository createRepository() {
        return new InMemorySessionRepository();
    }

    @Nested
    @DisplayName("创建会话测试")
    class CreateSessionTest {

        @Test
        @DisplayName("应该成功创建会话")
        void shouldCreateSessionSuccessfully() {
            var repository = createRepository();

            AgentSession session = repository.createSession("测试会话");

            assertThat(session).isNotNull();
            assertThat(session.getId()).isNotNull();
            assertThat(session.getName()).isEqualTo("测试会话");
        }

        @Test
        @DisplayName("应该成功创建带自定义 ID 的会话")
        void shouldCreateSessionWithCustomId() {
            var repository = createRepository();

            AgentSession session = repository.createSession("custom_id", "测试会话");

            assertThat(session.getId()).isEqualTo("custom_id");
            assertThat(session.getName()).isEqualTo("测试会话");
        }

        @Test
        @DisplayName("创建的会话应该可以被查询到")
        void shouldFindCreatedSession() {
            var repository = createRepository();

            AgentSession created = repository.createSession("测试会话");
            AgentSession found = repository.findById(created.getId()).orElse(null);

            assertThat(found).isNotNull();
            assertThat(found.getId()).isEqualTo(created.getId());
        }
    }

    @Nested
    @DisplayName("查询会话测试")
    class FindSessionTest {

        @Test
        @DisplayName("应该能通过 ID 找到已存在的会话")
        void shouldFindExistingSessionById() {
            var repository = createRepository();
            AgentSession created = repository.createSession("会话1");

            var found = repository.findById(created.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(created.getId());
        }

        @Test
        @DisplayName("应该返回 Optional.empty 当会话不存在")
        void shouldReturnEmptyWhenSessionNotFound() {
            var repository = createRepository();

            var found = repository.findById("non_existent_id");

            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("应该能找到所有会话")
        void shouldFindAllSessions() {
            var repository = createRepository();
            repository.createSession("会话1");
            repository.createSession("会话2");
            repository.createSession("会话3");

            var sessions = repository.findAll();

            assertThat(sessions).hasSize(3);
        }

        @Test
        @DisplayName("空仓库应该返回空列表")
        void shouldReturnEmptyListWhenNoSessions() {
            var repository = createRepository();

            var sessions = repository.findAll();

            assertThat(sessions).isEmpty();
        }
    }

    @Nested
    @DisplayName("删除会话测试")
    class DeleteSessionTest {

        @Test
        @DisplayName("应该成功删除已存在的会话")
        void shouldDeleteExistingSession() {
            var repository = createRepository();
            AgentSession session = repository.createSession("会话1");

            boolean deleted = repository.deleteById(session.getId());

            assertThat(deleted).isTrue();
            assertThat(repository.findById(session.getId())).isEmpty();
        }

        @Test
        @DisplayName("删除不存在的会话应该返回 false")
        void shouldReturnFalseWhenDeletingNonExistentSession() {
            var repository = createRepository();

            boolean deleted = repository.deleteById("non_existent_id");

            assertThat(deleted).isFalse();
        }

        @Test
        @DisplayName("删除后应该减少会话数量")
        void shouldReduceSessionCountAfterDelete() {
            var repository = createRepository();
            repository.createSession("会话1");
            repository.createSession("会话2");

            repository.deleteById(repository.findAll().get(0).getId());

            assertThat(repository.findAll()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("会话数量测试")
    class SessionCountTest {

        @Test
        @DisplayName("应该正确返回会话数量")
        void shouldReturnCorrectSessionCount() {
            var repository = createRepository();
            repository.createSession("会话1");
            repository.createSession("会话2");

            assertThat(repository.count()).isEqualTo(2);
        }

        @Test
        @DisplayName("空仓库应该返回零")
        void shouldReturnZeroForEmptyRepository() {
            var repository = createRepository();

            assertThat(repository.count()).isZero();
        }
    }
}
