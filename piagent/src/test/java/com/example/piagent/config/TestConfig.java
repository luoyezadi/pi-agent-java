package com.example.piagent.config;

import com.example.piagent.agent.AgentLoop;
import com.example.piagent.agent.AgentProperties;
import com.example.piagent.common.BizException;
import com.example.piagent.llm.ChatModelClient;
import com.example.piagent.llm.MockChatModelClient;
import com.example.piagent.session.SessionService;
import com.example.piagent.tool.ToolService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Test 配置
 *
 * 测试时使用 MockChatModelClient，避免调用真实 API
 */
@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public ChatModelClient chatModelClient() {
        return new MockChatModelClient("test-model");
    }

    @Bean
    @Primary
    public AgentLoop agentLoop(
            ChatModelClient chatModelClient,
            SessionService sessionService,
            ToolService toolService,
            AgentProperties agentProperties
    ) {
        return new AgentLoop(chatModelClient, sessionService, toolService, agentProperties.getMaxSteps());
    }
}
