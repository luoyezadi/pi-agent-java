package com.example.piagent.tool.builtin;

import com.example.piagent.tool.ToolExecutionRequest;
import com.example.piagent.tool.ToolExecutionResult;
import com.example.piagent.tool.ToolExecutor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 计算器工具执行器
 *
 * 设计目的：
 * - 提供基本的数学计算能力
 * - 支持加减乘除和括号运算
 * - 演示如何实现自定义工具
 *
 * 为什么使用正则表达式解析？
 * - 简单且足够用于演示
 * - 实际生产环境建议使用专业的表达式解析库（如 exp4j）
 */
@Component
public class CalculatorToolExecutor implements ToolExecutor {

    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("^[\\d\\s\\+\\-\\*\\/\\.\\(\\)]+$");

    @Override
    public ToolExecutionResult execute(ToolExecutionRequest request) {
        try {
            String expression = extractExpression(request.getArguments());
            if (expression == null) {
                return ToolExecutionResult.failure("缺少表达式参数");
            }

            expression = expression.trim().replaceAll("\\s+", "");

            if (!EXPRESSION_PATTERN.matcher(expression).matches()) {
                return ToolExecutionResult.failure("表达式包含非法字符");
            }

            BigDecimal result = evaluate(expression);
            return ToolExecutionResult.success(result.toPlainString());

        } catch (Exception e) {
            return ToolExecutionResult.failure("计算错误: " + e.getMessage());
        }
    }

    private String extractExpression(Object arguments) {
        if (arguments == null) {
            return null;
        }
        if (arguments instanceof String str) {
            return str;
        }
        if (arguments instanceof Map<?, ?> map) {
            Object expr = map.get("expression");
            return expr != null ? expr.toString() : null;
        }
        return arguments.toString();
    }

    private BigDecimal evaluate(String expression) {
        return parseExpression(expression);
    }

    private BigDecimal parseExpression(String expr) {
        expr = expr.trim();
        return parseAddSubtract(expr, new int[]{0});
    }

    private BigDecimal parseAddSubtract(String expr, int[] pos) {
        BigDecimal left = parseMultiplyDivide(expr, pos);

        while (pos[0] < expr.length()) {
            char op = expr.charAt(pos[0]);
            if (op != '+' && op != '-') {
                break;
            }
            pos[0]++;
            BigDecimal right = parseMultiplyDivide(expr, pos);
            if (op == '+') {
                left = left.add(right);
            } else {
                left = left.subtract(right);
            }
        }
        return left;
    }

    private BigDecimal parseMultiplyDivide(String expr, int[] pos) {
        BigDecimal left = parseNumber(expr, pos);

        while (pos[0] < expr.length()) {
            char op = expr.charAt(pos[0]);
            if (op != '*' && op != '/') {
                break;
            }
            pos[0]++;
            BigDecimal right = parseNumber(expr, pos);
            if (op == '*') {
                left = left.multiply(right);
            } else {
                if (right.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("除数不能为零");
                }
                left = left.divide(right, MathContext.DECIMAL128);
            }
        }
        return left;
    }

    private BigDecimal parseNumber(String expr, int[] pos) {
        int start = pos[0];

        if (pos[0] < expr.length() && expr.charAt(pos[0]) == '(') {
            pos[0]++;
            BigDecimal result = parseExpression(expr);
            if (pos[0] < expr.length() && expr.charAt(pos[0]) == ')') {
                pos[0]++;
            }
            return result;
        }

        StringBuilder sb = new StringBuilder();
        while (pos[0] < expr.length() && (Character.isDigit(expr.charAt(pos[0])) || expr.charAt(pos[0]) == '.')) {
            sb.append(expr.charAt(pos[0]));
            pos[0]++;
        }

        if (sb.length() == 0) {
            throw new NumberFormatException("期望数字但得到: " + expr.substring(start));
        }

        return new BigDecimal(sb.toString());
    }
}
