package com.example.piagent.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 基于内存的 Agent 事件发布器实现
 *
 * 设计目的：
 * - 提供线程安全的事件发布和订阅
 * - 用于单实例场景
 * - 便于测试
 */
@Component
public class InMemoryAgentEventPublisher implements AgentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(InMemoryAgentEventPublisher.class);

    private final Map<AgentEventType, List<Consumer<AgentEvent>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public void publish(AgentEvent event) {
        if (event == null || event.getType() == null) {
            return;
        }

        List<Consumer<AgentEvent>> handlers = subscribers.get(event.getType());
        if (handlers != null) {
            for (Consumer<AgentEvent> handler : handlers) {
                try {
                    handler.accept(event);
                } catch (Exception e) {
                    log.error("事件处理异常", e);
                }
            }
        }
    }

    @Override
    public void subscribe(AgentEventType type, Consumer<AgentEvent> handler) {
        subscribers.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    @Override
    public void unsubscribe(AgentEventType type) {
        subscribers.remove(type);
    }

    /**
     * 清除所有订阅
     */
    public void clearAll() {
        subscribers.clear();
    }
}
