package com.example.piagent.tool.builtin;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具执行器注册表
 *
 * 设计目的：
 * - 管理所有工具执行器的注册和查询
 * - 与 ToolRegistry 配合使用
 * - ToolRegistry 存储工具定义，ToolExecutorRegistry 存储执行逻辑
 */
@Component
public class ToolExecutorRegistry {

    private final Map<String, ToolExecutor> executors = new ConcurrentHashMap<>();

    /**
     * 注册工具执行器
     */
    public void register(String toolName, ToolExecutor executor) {
        executors.put(toolName, executor);
    }

    /**
     * 获取工具执行器
     */
    public ToolExecutor get(String toolName) {
        return executors.get(toolName);
    }

    /**
     * 注销工具执行器
     */
    public void unregister(String toolName) {
        executors.remove(toolName);
    }

    /**
     * 检查工具执行器是否存在
     */
    public boolean exists(String toolName) {
        return executors.containsKey(toolName);
    }
}
