package com.example.piagent.tool.builtin;

import com.example.piagent.tool.ToolDefinition;
import com.example.piagent.tool.ToolExecutionRequest;
import com.example.piagent.tool.ToolExecutionResult;
import com.example.piagent.tool.ToolExecutor;
import com.example.piagent.tool.ToolParameter;
import com.example.piagent.tool.ToolParameterSchema;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 搜索工具执行器
 *
 * 设计目的：
 * - 提供网络搜索能力
 * - 演示如何为研究型子智能体添加工具
 *
 * 注意：这是一个 Mock 实现，实际使用时需要接入真实的搜索 API
 */
@Component
public class SearchToolExecutor implements ToolExecutor {

    @Override
    public ToolExecutionResult execute(ToolExecutionRequest request) {
        try {
            String query = extractQuery(request.getArguments());

            if (query == null || query.trim().isEmpty()) {
                return ToolExecutionResult.failure("搜索关键词不能为空");
            }

            String result = performMockSearch(query);

            return ToolExecutionResult.success(result);

        } catch (Exception e) {
            return ToolExecutionResult.failure("搜索失败: " + e.getMessage());
        }
    }

    private String extractQuery(Object arguments) {
        if (arguments == null) {
            return null;
        }
        if (arguments instanceof String str) {
            return str;
        }
        if (arguments instanceof java.util.Map<?, ?> map) {
            Object query = map.get("query");
            return query != null ? query.toString() : null;
        }
        return arguments.toString();
    }

    private String performMockSearch(String query) {
        return String.format("""
            搜索结果: "%s"

            相关信息:
            1. 维基百科: 关于 "%s" 的详细信息
            2. GitHub: 相关开源项目
            3. 技术博客: 最佳实践指南
            4. 官方文档: 入门教程

            注意: 这是模拟搜索结果。实际使用时需要接入真实的搜索 API（如 Google、Bing、DuckDuckGo 等）
            """, query, query);
    }

    public static ToolDefinition createToolDefinition() {
        return ToolDefinition.builder()
                .name("search")
                .description("搜索网络获取相关信息。支持关键词搜索，返回相关网页、文档和资料。")
                .parameterSchema(new ToolParameterSchema(
                        List.of(new ToolParameter(
                                "query",
                                "搜索关键词",
                                true,
                                "string"
                        )),
                        "object"
                ))
                .build();
    }
}
