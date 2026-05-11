package com.example.piagent.event;

/**
 * Agent 事件类型枚举
 *
 * 定义了 Agent 执行过程中可能发生的各种事件类型
 */
public enum AgentEventType {

    /**
     * Agent 开始执行
     */
    AGENT_STARTED,

    /**
     * 发送模型请求
     */
    MODEL_REQUEST,

    /**
     * 收到模型响应
     */
    MODEL_RESPONSE,

    /**
     * 开始执行工具
     */
    TOOL_CALL_STARTED,

    /**
     * 工具执行完成
     */
    TOOL_CALL_FINISHED,

    /**
     * Agent 执行完成
     */
    AGENT_FINISHED,

    /**
     * Agent 执行失败
     */
    AGENT_FAILED
}
