package com.example.piagent.prompt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DefaultPromptTemplateRenderer 提示词渲染器测试
 *
 * 测试目的：
 * 1. 验证简单变量替换功能
 * 2. 验证多变量替换功能
 * 3. 验证缺少变量时的处理
 * 4. 验证空模板和空变量场景
 */
@DisplayName("DefaultPromptTemplateRenderer 测试")
class DefaultPromptTemplateRendererTest {

    private DefaultPromptTemplateRenderer createRenderer() {
        return new DefaultPromptTemplateRenderer();
    }

    @Nested
    @DisplayName("简单变量替换测试")
    class SimpleVariableReplacementTest {

        @Test
        @DisplayName("应该正确替换单个变量")
        void shouldReplaceSingleVariable() {
            var renderer = createRenderer();
            var template = PromptTemplate.builder()
                    .template("你好，{{name}}！")
                    .build();

            String result = renderer.render(template, Map.of("name", "小明"));

            assertThat(result).isEqualTo("你好，小明！");
        }

        @Test
        @DisplayName("应该正确替换多个相同变量")
        void shouldReplaceMultipleSameVariables() {
            var renderer = createRenderer();
            var template = PromptTemplate.builder()
                    .template("{{name}} 说：{{name}} 最棒！")
                    .build();

            String result = renderer.render(template, Map.of("name", "张三"));

            assertThat(result).isEqualTo("张三 说：张三 最棒！");
        }
    }

    @Nested
    @DisplayName("多变量替换测试")
    class MultipleVariableReplacementTest {

        @Test
        @DisplayName("应该正确替换多个不同变量")
        void shouldReplaceMultipleDifferentVariables() {
            var renderer = createRenderer();
            var template = PromptTemplate.builder()
                    .template("{{greeting}}，{{name}}！今天是 {{date}}。")
                    .build();

            String result = renderer.render(template, Map.of(
                    "greeting", "你好",
                    "name", "小明",
                    "date", "2024-01-01"
            ));

            assertThat(result).isEqualTo("你好，小明！今天是 2024-01-01。");
        }

        @Test
        @DisplayName("应该只替换提供的变量")
        void shouldOnlyReplaceProvidedVariables() {
            var renderer = createRenderer();
            var template = PromptTemplate.builder()
                    .template("{{name}} - {{location}}")
                    .build();

            String result = renderer.render(template, Map.of("name", "测试"));

            assertThat(result).isEqualTo("测试 - {{location}}");
        }
    }

    @Nested
    @DisplayName("边界情况测试")
    class EdgeCaseTest {

        @Test
        @DisplayName("空模板应该返回空字符串")
        void shouldReturnEmptyStringForEmptyTemplate() {
            var renderer = createRenderer();
            var template = PromptTemplate.builder()
                    .template("")
                    .build();

            String result = renderer.render(template, Map.of());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("无变量模板应该原样返回")
        void shouldReturnOriginalForNoVariables() {
            var renderer = createRenderer();
            var template = PromptTemplate.builder()
                    .template("这是一个没有变量的模板")
                    .build();

            String result = renderer.render(template, Map.of());

            assertThat(result).isEqualTo("这是一个没有变量的模板");
        }

        @Test
        @DisplayName("空变量 map 应该原样返回")
        void shouldReturnOriginalForEmptyVariables() {
            var renderer = createRenderer();
            var template = PromptTemplate.builder()
                    .template("Hello {{name}}")
                    .build();

            String result = renderer.render(template, Map.of());

            assertThat(result).isEqualTo("Hello {{name}}");
        }

        @Test
        @DisplayName("变量值为 null 应该替换为空")
        void shouldReplaceNullValueWithEmpty() {
            var renderer = createRenderer();
            var template = PromptTemplate.builder()
                    .template("Hello {{name}}")
                    .build();

            String result = renderer.render(template, Map.of("name", null));

            assertThat(result).isEqualTo("Hello ");
        }

        @Test
        @DisplayName("连续变量应该分别替换")
        void shouldReplaceConsecutiveVariables() {
            var renderer = createRenderer();
            var template = PromptTemplate.builder()
                    .template("{{a}}{{b}}{{c}}")
                    .build();

            String result = renderer.render(template, Map.of(
                    "a", "1",
                    "b", "2",
                    "c", "3"
            ));

            assertThat(result).isEqualTo("123");
        }
    }

    @Nested
    @DisplayName("模板组合测试")
    class TemplateCompositionTest {

        @Test
        @DisplayName("应该支持组合多个模板")
        void shouldSupportComposingMultipleTemplates() {
            var renderer = createRenderer();
            var template1 = PromptTemplate.builder().template("系统：{{system_prompt}}").build();
            var template2 = PromptTemplate.builder().template("用户：{{user_message}}").build();

            String result1 = renderer.render(template1, Map.of("system_prompt", "你是一个助手"));
            String result2 = renderer.render(template2, Map.of("user_message", "你好"));

            assertThat(result1).isEqualTo("系统：你是一个助手");
            assertThat(result2).isEqualTo("用户：你好");
        }
    }
}
