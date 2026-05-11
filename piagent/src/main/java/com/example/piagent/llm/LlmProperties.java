package com.example.piagent.llm;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * LLM 配置属性
 *
 * 设计目的：
 * - 绑定 application.yml 中的 llm 配置
 * - 支持多种模型供应商配置
 */
@Component
@ConfigurationProperties(prefix = "piagent.llm")
public class LlmProperties {

    private String provider = "openai";
    private OpenAiConfig openai = new OpenAiConfig();
    private OllamaConfig ollama = new OllamaConfig();
    private DashScopeConfig dashscope = new DashScopeConfig();
    private DeepSeekConfig deepseek = new DeepSeekConfig();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public OpenAiConfig getOpenai() {
        return openai;
    }

    public void setOpenai(OpenAiConfig openai) {
        this.openai = openai;
    }

    public OllamaConfig getOllama() {
        return ollama;
    }

    public void setOllama(OllamaConfig ollama) {
        this.ollama = ollama;
    }

    public DashScopeConfig getDashscope() {
        return dashscope;
    }

    public void setDashscope(DashScopeConfig dashscope) {
        this.dashscope = dashscope;
    }

    public DeepSeekConfig getDeepseek() {
        return deepseek;
    }

    public void setDeepseek(DeepSeekConfig deepseek) {
        this.deepseek = deepseek;
    }

    public static class OpenAiConfig {
        private String apiKey = "sk-demo";
        private String modelName = "gpt-4o-mini";
        private String baseUrl = "https://api.openai.com/v1";
        private int timeout = 60;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
    }

    public static class OllamaConfig {
        private String baseUrl = "http://localhost:11434";
        private String modelName = "llama3.2";
        private int timeout = 120;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
    }

    public static class DashScopeConfig {
        private String apiKey;
        private String modelName = "qwen-plus";
        private String baseUrl = "https://dashscope.aliyuncs.com/api/v1";
        private int timeout = 60;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
    }

    public static class DeepSeekConfig {
        private String apiKey;
        private String modelName = "deepseek-chat";
        private String baseUrl = "https://api.deepseek.com/v1";
        private int timeout = 60;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
    }
}
