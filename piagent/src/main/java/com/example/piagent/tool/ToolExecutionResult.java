package com.example.piagent.tool;

/**
 * 工具执行结果
 *
 * 设计目的：
 * - 封装工具执行的输出数据
 * - 区分成功和失败情况
 * - 包含执行结果内容和错误信息
 *
 * 为什么使用 isSuccess 而非抛出异常？
 * - 工具执行失败是正常的业务场景，不应该用异常处理
 * - 便于 Agent 框架统一处理成功和失败
 */
public class ToolExecutionResult {

    private final boolean success;
    private final String result;
    private final String error;

    private ToolExecutionResult(boolean success, String result, String error) {
        this.success = success;
        this.result = result;
        this.error = error;
    }

    public static ToolExecutionResult success(String result) {
        return new ToolExecutionResult(true, result, null);
    }

    public static ToolExecutionResult failure(String error) {
        return new ToolExecutionResult(false, null, error);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getResult() {
        return result;
    }

    public String getError() {
        return error;
    }
}
