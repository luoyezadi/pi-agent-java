package com.example.piagent.prompt;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 默认提示词模板渲染器
 *
 * 设计目的：
 * - 实现 {{variable}} 格式的变量替换
 * - 使用正则表达式匹配和替换变量
 *
 * 实现说明：
 * - 使用 Pattern 和 Matcher 进行正则匹配
 * - 遍历所有匹配项，从变量 map 中获取值进行替换
 * - 如果变量不存在或值为 null，则替换为空字符串
 *
 * 为什么使用正则表达式？
 * - 支持灵活的变量格式
 * - 性能可控，Pattern 预编译提高效率
 */
@Component
public class DefaultPromptTemplateRenderer implements PromptTemplateRenderer {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");

    @Override
    public String render(PromptTemplate template, Map<String, Object> variables) {
        if (template == null || template.getTemplate() == null) {
            return "";
        }

        String result = template.getTemplate();
        Matcher matcher = VARIABLE_PATTERN.matcher(result);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = variables != null ? variables.get(variableName) : null;
            String replacement = value != null ? value.toString() : "";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}
