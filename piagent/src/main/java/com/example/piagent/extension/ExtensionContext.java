package com.example.piagent.extension;

import com.example.piagent.skill.SkillRegistry;
import com.example.piagent.tool.ToolRegistry;
import com.example.piagent.tool.builtin.ToolExecutorRegistry;

/**
 * Extension 上下文
 *
 * 设计目的：
 * - 封装扩展访问框架组件的接口
 * - 隔离扩展和核心模块
 * - 便于扩展注册工具、技能等
 */
public class ExtensionContext {

    private final ToolRegistry toolRegistry;
    private final ToolExecutorRegistry executorRegistry;
    private final SkillRegistry skillRegistry;

    public ExtensionContext(
            ToolRegistry toolRegistry,
            ToolExecutorRegistry executorRegistry,
            SkillRegistry skillRegistry
    ) {
        this.toolRegistry = toolRegistry;
        this.executorRegistry = executorRegistry;
        this.skillRegistry = skillRegistry;
    }

    public ToolRegistry getToolRegistry() {
        return toolRegistry;
    }

    public ToolExecutorRegistry getExecutorRegistry() {
        return executorRegistry;
    }

    public SkillRegistry getSkillRegistry() {
        return skillRegistry;
    }
}
