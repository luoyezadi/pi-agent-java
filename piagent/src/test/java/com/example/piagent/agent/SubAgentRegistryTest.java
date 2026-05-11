package com.example.piagent.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SubAgentRegistry 子智能体注册表测试
 */
@DisplayName("SubAgentRegistry 测试")
class SubAgentRegistryTest {

    private SubAgentRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new SubAgentRegistry();
    }

    @Nested
    @DisplayName("注册子智能体测试")
    class RegisterAgentTest {

        @Test
        @DisplayName("应该成功注册子智能体")
        void shouldRegisterAgentSuccessfully() {
            SubAgent agent = SubAgent.builder()
                    .id("agent-1")
                    .name("测试助手")
                    .type("tester")
                    .build();

            boolean registered = registry.register(agent);

            assertThat(registered).isTrue();
            assertThat(registry.count()).isEqualTo(1);
        }

        @Test
        @DisplayName("注册同名智能体应该返回 false")
        void shouldReturnFalseForDuplicateAgent() {
            SubAgent agent1 = SubAgent.builder().id("agent-1").name("助手1").build();
            SubAgent agent2 = SubAgent.builder().id("agent-1").name("助手2").build();

            registry.register(agent1);
            boolean registered = registry.register(agent2);

            assertThat(registered).isFalse();
            assertThat(registry.count()).isEqualTo(1);
        }

        @Test
        @DisplayName("应该能够获取已注册的智能体")
        void shouldGetRegisteredAgent() {
            SubAgent agent = SubAgent.builder()
                    .id("agent-1")
                    .name("研究助手")
                    .build();
            registry.register(agent);

            Optional<SubAgent> found = registry.get("agent-1");

            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("研究助手");
        }

        @Test
        @DisplayName("获取不存在的智能体应返回 empty")
        void shouldReturnEmptyForNonExistentAgent() {
            Optional<SubAgent> found = registry.get("non-existent");

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("注销子智能体测试")
    class UnregisterAgentTest {

        @Test
        @DisplayName("应该成功注销智能体")
        void shouldUnregisterAgent() {
            SubAgent agent = SubAgent.builder()
                    .id("agent-1")
                    .name("助手")
                    .build();
            registry.register(agent);

            boolean unregistered = registry.unregister("agent-1");

            assertThat(unregistered).isTrue();
            assertThat(registry.count()).isZero();
        }

        @Test
        @DisplayName("注销不存在的智能体应返回 false")
        void shouldReturnFalseForNonExistentAgent() {
            boolean unregistered = registry.unregister("non-existent");

            assertThat(unregistered).isFalse();
        }
    }

    @Nested
    @DisplayName("获取所有智能体测试")
    class GetAllAgentsTest {

        @Test
        @DisplayName("应该返回所有已注册的智能体")
        void shouldReturnAllRegisteredAgents() {
            registry.register(SubAgent.builder().id("agent-1").name("助手1").build());
            registry.register(SubAgent.builder().id("agent-2").name("助手2").build());

            List<SubAgent> agents = registry.getAll();

            assertThat(agents).hasSize(2);
        }

        @Test
        @DisplayName("空注册表应返回空列表")
        void shouldReturnEmptyListForEmptyRegistry() {
            List<SubAgent> agents = registry.getAll();

            assertThat(agents).isEmpty();
        }
    }
}
