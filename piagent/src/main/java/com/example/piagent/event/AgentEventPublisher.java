package com.example.piagent.event;

import java.util.function.Consumer;

/**
 * Agent 事件发布器接口
 *
 * 设计目的：
 * - 抽象事件发布机制
 * - 支持同步和异步事件处理
 * - 便于 SSE、WebSocket 等多种输出方式
 */
public interface AgentEventPublisher {

    /**
     * 发布事件
     */
    void publish(AgentEvent event);

    /**
     * 订阅事件
     */
    void subscribe(AgentEventType type, Consumer<AgentEvent> handler);

    /**
     * 取消订阅
     */
    void unsubscribe(AgentEventType type);
}
