package com.example.piagent.api;

import com.example.piagent.common.ApiResponse;
import com.example.piagent.tool.ToolDefinition;
import com.example.piagent.tool.ToolRegistry;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Tool REST API 控制器
 *
 * 提供工具管理相关接口
 */
@RestController
@RequestMapping("/api/v1/tools")
public class ToolController {

    private final ToolRegistry toolRegistry;

    public ToolController(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }

    @GetMapping
    public ApiResponse<List<ToolDefinition>> listTools() {
        List<ToolDefinition> tools = toolRegistry.getAll();
        return ApiResponse.success(tools);
    }

    @GetMapping("/{name}")
    public ApiResponse<ToolDefinition> getTool(@PathVariable String name) {
        return toolRegistry.get(name)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "工具不存在: " + name));
    }
}
