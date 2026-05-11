package com.example.piagent.prompt;

import java.util.Map;

/**
 * 提示词模板渲染器接口
 *
 * 设计目的：
 * - 抽象提示词渲染逻辑，便于扩展不同的渲染实现
 * - 统一渲染入口，调用方无需关心具体实现
 *
 * 为什么使用接口？
 * - 便于单元测试时使用 Mock 实现
 * - 便于后续扩展更复杂的渲染逻辑（如条件渲染、循环渲染）
 */
public interface PromptTemplateRenderer {

    /**
     * 渲染提示词模板
     *
     * @param template  提示词模板
     * @param variables 变量映射
     * @return 渲染后的字符串
     */
    String render(PromptTemplate template, Map<String, Object> variables);
}
