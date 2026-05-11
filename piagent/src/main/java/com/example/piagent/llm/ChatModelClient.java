package com.example.piagent.llm;

/**
 * 聊天模型客户端接口
 *
 * 设计目的：
 * - 抽象 LLM 调用逻辑，支持多种模型供应商
 * - 统一请求和响应格式
 * - 便于单元测试时使用 Mock 实现
 *
 * 为什么使用接口？
 * - 解耦核心逻辑和具体实现
 * - 支持 OpenAI、Ollama、DashScope 等多种供应商
 * - 便于切换和扩展
 */
public interface ChatModelClient {

    /**
     * 发送聊天请求并获取响应
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    ChatModelResponse chat(ChatModelRequest request);

    /**
     * 获取模型名称
     */
    String getModelName();

    /**
     * 检查是否支持工具调用
     */
    default boolean supportsTools() {
        return true;
    }
}
