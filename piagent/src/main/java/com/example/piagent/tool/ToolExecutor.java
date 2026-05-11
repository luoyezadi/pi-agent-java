package com.example.piagent.tool;

import java.util.Map;

/**
 * 工具执行器接口
 *
 * 设计目的：
 * - 抽象工具执行逻辑，便于单元测试和扩展
 * - 执行器负责根据参数执行实际逻辑
 * - 返回统一的执行结果
 *
 * 为什么使用接口？
 * - 便于使用 Mock 对象进行测试
 * - 便于扩展不同实现的工具
 */
public interface ToolExecutor {

    /**
     * 执行工具
     *
     * @param request 执行请求，包含工具名称和参数
     * @return 执行结果
     */
    ToolExecutionResult execute(ToolExecutionRequest request);
}
