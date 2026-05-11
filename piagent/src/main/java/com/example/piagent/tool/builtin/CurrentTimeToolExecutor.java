package com.example.piagent.tool.builtin;

import com.example.piagent.tool.ToolExecutionRequest;
import com.example.piagent.tool.ToolExecutionResult;
import com.example.piagent.tool.ToolExecutor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 获取当前时间工具执行器
 *
 * 设计目的：
 * - 提供获取当前日期时间的能力
 * - 演示无参数工具的实现方式
 */
@Component
public class CurrentTimeToolExecutor implements ToolExecutor {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public ToolExecutionResult execute(ToolExecutionRequest request) {
        LocalDateTime now = LocalDateTime.now();
        String formatted = now.format(FORMATTER);
        return ToolExecutionResult.success("当前时间: " + formatted);
    }
}
