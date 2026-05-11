package com.example.piagent.api;

import com.example.piagent.agent.AgentProperties;
import com.example.piagent.config.TestConfig;
import com.example.piagent.llm.MockChatModelClient;
import com.example.piagent.session.InMemorySessionRepository;
import com.example.piagent.session.SessionService;
import com.example.piagent.tool.ToolExecutorRegistry;
import com.example.piagent.tool.ToolRegistry;
import com.example.piagent.tool.builtin.CalculatorToolExecutor;
import com.example.piagent.tool.builtin.CurrentTimeToolExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * REST API 集成测试
 *
 * 测试目的：
 * 1. 验证各个 API 端点是否正常工作
 * 2. 验证请求参数和响应格式
 * 3. 验证 SSE 流式输出
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("REST API 集成测试")
class AgentApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Configuration
    static class TestConfig {
        @Bean
        public InMemorySessionRepository sessionRepository() {
            return new InMemorySessionRepository();
        }

        @Bean
        public SessionService sessionService(InMemorySessionRepository repository) {
            return new SessionService(repository);
        }

        @Bean
        public ToolRegistry toolRegistry() {
            return new ToolRegistry();
        }

        @Bean
        public ToolExecutorRegistry executorRegistry() {
            return new ToolExecutorRegistry();
        }

        @Bean
        public ToolExecutorRegistry toolExecutorRegistry() {
            var registry = new ToolExecutorRegistry();
            registry.register("calculator", new CalculatorToolExecutor());
            registry.register("current_time", new CurrentTimeToolExecutor());
            return registry;
        }

        @Bean
        public com.example.piagent.tool.ToolService toolService(
                ToolRegistry toolRegistry,
                ToolExecutorRegistry executorRegistry
        ) {
            return new com.example.piagent.tool.ToolService(toolRegistry, executorRegistry);
        }

        @Bean
        public com.example.piagent.agent.AgentProperties agentProperties() {
            var props = new AgentProperties();
            props.setMaxSteps(10);
            return props;
        }

        @Bean
        public com.example.piagent.llm.ChatModelClient chatModelClient() {
            return new MockChatModelClient("test-model");
        }
    }

    @Nested
    @DisplayName("Agent API 测试")
    class AgentApiTest {

        @Test
        @DisplayName("POST /api/v1/agent/chat - 应该成功执行聊天")
        void shouldChatSuccessfully() throws Exception {
            String requestBody = """
                {
                    "message": "你好"
                }
                """;

            mockMvc.perform(post("/api/v1/agent/chat")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.sessionId").exists())
                    .andExpect(jsonPath("$.data.success").value(true));
        }

        @Test
        @DisplayName("POST /api/v1/agent/chat - 缺少消息应返回错误")
        void shouldReturnErrorWhenMessageMissing() throws Exception {
            String requestBody = """
                {
                    "message": ""
                }
                """;

            mockMvc.perform(post("/api/v1/agent/chat")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1002));
        }

        @Test
        @DisplayName("POST /api/v1/agent/chat/stream - SSE 应正常返回")
        void shouldReturnSseStream() throws Exception {
            String requestBody = """
                {
                    "message": "你好"
                }
                """;

            MvcResult result = mockMvc.perform(post("/api/v1/agent/chat/stream")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andReturn();

            String content = result.getResponse().getContentAsString();
            org.assertj.core.api.Assertions.assertThat(content).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Session API 测试")
    class SessionApiTest {

        @Test
        @DisplayName("POST /api/v1/sessions - 应该创建会话")
        void shouldCreateSession() throws Exception {
            mockMvc.perform(post("/api/v1/sessions")
                            .param("name", "测试会话"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.name").value("测试会话"))
                    .andExpect(jsonPath("$.data.id").exists());
        }

        @Test
        @DisplayName("GET /api/v1/sessions - 应该返回会话列表")
        void shouldListSessions() throws Exception {
            mockMvc.perform(post("/api/v1/sessions")
                            .param("name", "会话1"));
            mockMvc.perform(post("/api/v1/sessions")
                            .param("name", "会话2"));

            mockMvc.perform(get("/api/v1/sessions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        @DisplayName("GET /api/v1/sessions/{id} - 应该返回指定会话")
        void shouldGetSession() throws Exception {
            MvcResult createResult = mockMvc.perform(post("/api/v1/sessions")
                            .param("name", "测试"))
                    .andReturn();

            String sessionId = com.jayway.jsonpath.JsonPath
                    .read(createResult.getResponse().getContentAsString(), "$.data.id");

            mockMvc.perform(get("/api/v1/sessions/" + sessionId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(sessionId));
        }

        @Test
        @DisplayName("GET /api/v1/sessions/{id} - 不存在的会话应返回错误")
        void shouldReturnErrorForNonExistentSession() throws Exception {
            mockMvc.perform(get("/api/v1/sessions/non_existent_id"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(2001));
        }
    }

    @Nested
    @DisplayName("Tool API 测试")
    class ToolApiTest {

        @Test
        @DisplayName("GET /api/v1/tools - 应该返回工具列表")
        void shouldListTools() throws Exception {
            mockMvc.perform(get("/api/v1/tools"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("Skill API 测试")
    class SkillApiTest {

        @Test
        @DisplayName("GET /api/v1/skills - 应该返回技能列表")
        void shouldListSkills() throws Exception {
            mockMvc.perform(get("/api/v1/skills"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }
}
