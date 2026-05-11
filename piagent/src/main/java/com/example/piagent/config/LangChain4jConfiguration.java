package com.example.piagent.config;

import com.example.piagent.llm.LlmProperties;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.oai.ChatCompletionLanguageModel;
import dev.langchain4j.model.chat.oai.ChatCompletionRequest;
import dev.langchain4j.model.chat.oai.ChatCompletionResponse;
import dev.langchain4j.model.chat.oai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * LangChain4j 配置
 *
 * 设计目的：
 * - 配置 LangChain4j 的 ChatLanguageModel
 * - 支持多种模型供应商
 * - 通过 @Profile 控制不同环境的配置
 */
@Configuration
public class LangChain4jConfiguration {

    @Bean
    @Profile("!test")
    public ChatLanguageModel chatLanguageModel(LlmProperties llmProperties) {
        String provider = llmProperties.getProvider();

        return switch (provider.toLowerCase()) {
            case "openai" -> createOpenAiModel(llmProperties);
            case "ollama" -> createOllamaModel(llmProperties);
            default -> createOpenAiModel(llmProperties);
        };
    }

    private ChatLanguageModel createOpenAiModel(LlmProperties properties) {
        LlmProperties.OpenAiConfig config = properties.getOpenai();

        return OpenAiChatModel.builder()
                .apiKey(config.getApiKey())
                .modelName(config.getModelName())
                .baseUrl(config.getBaseUrl())
                .timeout(java.time.Duration.ofSeconds(config.getTimeout()))
                .build();
    }

    private ChatLanguageModel createOllamaModel(LlmProperties properties) {
        LlmProperties.OllamaConfig config = properties.getOllama();

        return dev.langchain4j.model.ollama.chat.OllamaChatModel.builder()
                .baseUrl(config.getBaseUrl())
                .modelName(config.getModelName())
                .timeout(java.time.Duration.ofSeconds(config.getTimeout()))
                .build();
    }
}
