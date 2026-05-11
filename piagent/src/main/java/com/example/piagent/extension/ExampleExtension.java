package com.example.piagent.extension;

import com.example.piagent.tool.ToolDefinition;
import com.example.piagent.tool.ToolExecutor;
import com.example.piagent.tool.ToolExecutionRequest;
import com.example.piagent.tool.ToolExecutionResult;
import com.example.piagent.tool.ToolParameter;
import com.example.piagent.tool.ToolParameterSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 示例扩展
 *
 * 演示如何创建和使用扩展
 * 此扩展注册了一个"搜索"工具
 */
@Component
public class ExampleExtension implements Extension {

    private static final Logger log = LoggerFactory.getLogger(ExampleExtension.class);

    @Override
    public String getName() {
        return "ExampleExtension";
    }

    @Override
    public String getDescription() {
        return "示例扩展，注册了一个模拟搜索工具";
    }

    @Override
    public void onLoad(ExtensionContext context) {
        log.info("ExampleExtension 开始加载...");

        ToolDefinition searchTool = ToolDefinition.builder()
                .name("search")
                .description("搜索网络获取信息。请提供搜索关键词。")
                .parameterSchema(new ToolParameterSchema(
                        List.of(new ToolParameter("keyword", "搜索关键词", true, "string")),
                        "object"
                ))
                .build();

        context.getToolRegistry().register(searchTool);
        context.getExecutorRegistry().register("search", new SearchToolExecutor());

        log.info("ExampleExtension 加载完成");
    }

    /**
     * 示例搜索工具执行器
     * 注意：这是一个 mock 实现，实际生产环境需要调用真实的搜索 API
     */
    static class SearchToolExecutor implements ToolExecutor {

        @Override
        public ToolExecutionResult execute(ToolExecutionRequest request) {
            String keyword = extractKeyword(request.getArguments());

            if (keyword == null || keyword.isEmpty()) {
                return ToolExecutionResult.failure("缺少搜索关键词");
            }

            String mockResult = "搜索结果：关于「" + keyword + "」的信息...\n" +
                    "1. 相关信息一\n" +
                    "2. 相关信息二\n" +
                    "3. 相关信息三";

            return ToolExecutionResult.success(mockResult);
        }

        private String extractKeyword(Object arguments) {
            if (arguments == null) {
                return null;
            }
            if (arguments instanceof String str) {
                return str.replaceAll("[{}\"]", "").replaceAll("keyword:", "").trim();
            }
            if (arguments instanceof java.util.Map<?, ?> map) {
                Object keyword = map.get("keyword");
                return keyword != null ? keyword.toString() : null;
            }
            return arguments.toString();
        }
    }
}
