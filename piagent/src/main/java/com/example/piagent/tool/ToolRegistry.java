package com.example.piagent.tool;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 工具注册表
 *
 * 设计目的：
 * - 管理所有可用工具的注册和查询
 * - 使用 ConcurrentHashMap 保证线程安全
 * - 支持运行时动态注册和注销工具
 *
 * 为什么使用 ConcurrentHashMap？
 * - 多线程环境下并发安全
 * - 比 synchronized 性能更好
 * - 适合 Web 应用场景
 */
@Component
public class ToolRegistry {

    private final Map<String, ToolDefinition> tools = new ConcurrentHashMap<>();
    private final AtomicInteger toolCounter = new AtomicInteger(0);

    /**
     * 注册工具
     *
     * @param tool 工具定义
     * @return 是否注册成功（如果工具已存在则返回 false）
     */
    public boolean register(ToolDefinition tool) {
        if (tool == null || tool.getName() == null) {
            return false;
        }
        String name = tool.getName();
        if (tools.containsKey(name)) {
            return false;
        }
        tools.put(name, tool);
        toolCounter.incrementAndGet();
        return true;
    }

    /**
     * 根据名称获取工具定义
     */
    public Optional<ToolDefinition> get(String name) {
        return Optional.ofNullable(tools.get(name));
    }

    /**
     * 获取所有已注册的工具
     */
    public List<ToolDefinition> getAll() {
        return tools.values().stream().toList();
    }

    /**
     * 注销工具
     *
     * @param name 工具名称
     * @return 是否成功注销
     */
    public boolean unregister(String name) {
        return tools.remove(name) != null;
    }

    /**
     * 返回已注册工具数量
     */
    public int count() {
        return tools.size();
    }
}
