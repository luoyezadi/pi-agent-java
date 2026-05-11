package com.example.piagent.tool;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ToolRegistry 工具注册表测试
 *
 * 测试目的：
 * 1. 验证工具注册功能
 * 2. 验证工具查询功能
 * 3. 验证工具不存在时的处理
 * 4. 验证工具列表获取功能
 */
@DisplayName("ToolRegistry 工具注册表测试")
class ToolRegistryTest {

    private ToolRegistry createRegistry() {
        return new ToolRegistry();
    }

    @Nested
    @DisplayName("注册工具测试")
    class RegisterToolTest {

        @Test
        @DisplayName("应该成功注册工具")
        void shouldRegisterToolSuccessfully() {
            var registry = createRegistry();
            var tool = createSampleTool("calculator", "执行数学计算");

            boolean registered = registry.register(tool);

            assertThat(registered).isTrue();
        }

        @Test
        @DisplayName("应该能够获取已注册的工具")
        void shouldGetRegisteredTool() {
            var registry = createRegistry();
            var tool = createSampleTool("calculator", "执行数学计算");
            registry.register(tool);

            var found = registry.get("calculator");

            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("calculator");
        }

        @Test
        @DisplayName("注册同名工具应该返回 false")
        void shouldReturnFalseWhenRegisteringDuplicateTool() {
            var registry = createRegistry();
            var tool1 = createSampleTool("calculator", "计算器");
            var tool2 = createSampleTool("calculator", "另一个计算器");
            registry.register(tool1);

            boolean registered = registry.register(tool2);

            assertThat(registered).isFalse();
        }

        @Test
        @DisplayName("应该能够获取所有已注册的工具")
        void shouldGetAllRegisteredTools() {
            var registry = createRegistry();
            registry.register(createSampleTool("tool1", "工具1"));
            registry.register(createSampleTool("tool2", "工具2"));

            List<ToolDefinition> tools = registry.getAll();

            assertThat(tools).hasSize(2);
        }
    }

    @Nested
    @DisplayName("查询工具测试")
    class FindToolTest {

        @Test
        @DisplayName("应该能找到已注册的工具")
        void shouldFindRegisteredTool() {
            var registry = createRegistry();
            registry.register(createSampleTool("time", "获取当前时间"));

            Optional<ToolDefinition> found = registry.get("time");

            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("time");
        }

        @Test
        @DisplayName("查询不存在的工具应该返回 Optional.empty")
        void shouldReturnEmptyForNonExistentTool() {
            var registry = createRegistry();

            Optional<ToolDefinition> found = registry.get("non_existent");

            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("空注册表应该返回空列表")
        void shouldReturnEmptyListForEmptyRegistry() {
            var registry = createRegistry();

            List<ToolDefinition> tools = registry.getAll();

            assertThat(tools).isEmpty();
        }
    }

    @Nested
    @DisplayName("注销工具测试")
    class UnregisterToolTest {

        @Test
        @DisplayName("应该成功注销已注册的工具")
        void shouldUnregisterToolSuccessfully() {
            var registry = createRegistry();
            registry.register(createSampleTool("temp", "临时工具"));

            boolean unregistered = registry.unregister("temp");

            assertThat(unregistered).isTrue();
            assertThat(registry.get("temp")).isEmpty();
        }

        @Test
        @DisplayName("注销不存在的工具应该返回 false")
        void shouldReturnFalseWhenUnregisteringNonExistentTool() {
            var registry = createRegistry();

            boolean unregistered = registry.unregister("non_existent");

            assertThat(unregistered).isFalse();
        }
    }

    @Nested
    @DisplayName("工具计数测试")
    class ToolCountTest {

        @Test
        @DisplayName("应该正确返回已注册工具数量")
        void shouldReturnCorrectToolCount() {
            var registry = createRegistry();
            registry.register(createSampleTool("tool1", "工具1"));
            registry.register(createSampleTool("tool2", "工具2"));

            assertThat(registry.count()).isEqualTo(2);
        }

        @Test
        @DisplayName("空注册表应该返回零")
        void shouldReturnZeroForEmptyRegistry() {
            var registry = createRegistry();

            assertThat(registry.count()).isZero();
        }
    }

    private ToolDefinition createSampleTool(String name, String description) {
        return ToolDefinition.builder()
                .name(name)
                .description(description)
                .parameterSchema(new ToolParameterSchema(
                        List.of(new ToolParameter("input", "输入", true, "string")),
                        "object"
                ))
                .build();
    }
}
