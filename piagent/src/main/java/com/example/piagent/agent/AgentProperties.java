package com.example.piagent.agent;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Agent 核心配置属性
 *
 * 设计目的：
 * - 使用 @ConfigurationProperties 将 application.yml 中的配置绑定到 Java 对象
 * - 配置集中管理，便于修改和维护
 * - 支持 IDE 自动补全和类型安全
 */
@Component
@ConfigurationProperties(prefix = "piagent.agent")
public class AgentProperties {

    /**
     * Agent Loop 最大执行步数
     * 防止 Agent 在无法得出结论时无限循环调用工具
     */
    private int maxSteps = 10;

    /**
     * 默认系统提示词
     * 当用户没有指定系统提示词时使用
     */
    private String defaultSystemPrompt = "你是一个智能助手，名为 PiAgent。尽你所能帮助用户。";

    public int getMaxSteps() {
        return maxSteps;
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    public String getDefaultSystemPrompt() {
        return defaultSystemPrompt;
    }

    public void setDefaultSystemPrompt(String defaultSystemPrompt) {
        this.defaultSystemPrompt = defaultSystemPrompt;
    }
}
