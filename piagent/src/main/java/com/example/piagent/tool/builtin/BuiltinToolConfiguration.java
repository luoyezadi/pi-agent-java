package com.example.piagent.tool.builtin;

import com.example.piagent.tool.ToolDefinition;
import com.example.piagent.tool.ToolExecutor;
import com.example.piagent.tool.ToolParameter;
import com.example.piagent.tool.ToolParameterSchema;
import com.example.piagent.tool.ToolRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 内置工具自动注册配置
 *
 * 设计目的：
 * - 在应用启动时自动注册内置工具
 * - 使用 CommandLineRunner 确保在 Spring 容器完全初始化后执行
 * - 集中管理所有内置工具的注册
 */
@Configuration
public class BuiltinToolConfiguration {

    @Bean
    public CommandLineRunner registerBuiltinTools(ToolRegistry toolRegistry, ToolExecutorRegistry executorRegistry) {
        return args -> {
            registerCalculatorTool(toolRegistry, executorRegistry);
            registerCurrentTimeTool(toolRegistry, executorRegistry);
        };
    }

    private void registerCalculatorTool(ToolRegistry toolRegistry, ToolExecutorRegistry executorRegistry) {
        ToolDefinition definition = ToolDefinition.builder()
                .name("calculator")
                .description("执行数学计算。支持加(+)、减(-)、乘(*)、除(/)、括号。例如：1+2, (3+4)*2")
                .parameterSchema(new ToolParameterSchema(
                        List.of(new ToolParameter("expression", "要计算的数学表达式", true, "string")),
                        "object"
                ))
                .build();
        toolRegistry.register(definition);
        executorRegistry.register("calculator", new CalculatorToolExecutor());
    }

    private void registerCurrentTimeTool(ToolRegistry toolRegistry, ToolExecutorRegistry executorRegistry) {
        ToolDefinition definition = ToolDefinition.builder()
                .name("current_time")
                .description("获取当前日期和时间")
                .parameterSchema(new ToolParameterSchema(List.of(), "object"))
                .build();
        toolRegistry.register(definition);
        executorRegistry.register("current_time", new CurrentTimeToolExecutor());
    }
}
