package com.example.piagent.common;

/**
 * 错误码枚举
 *
 * 设计目的：
 * - 集中管理错误码，便于统一维护
 * - 错误码格式：业务模块 * 1000 + 具体错误编号
 * - 便于快速定位问题所在模块
 */
public enum ErrorCode {

    // 通用错误 (1000-1999)
    UNKNOWN_ERROR(1001, "未知错误"),
    INVALID_PARAMETER(1002, "参数无效"),
    NOT_FOUND(1003, "资源不存在"),

    // Session 相关错误 (2000-2999)
    SESSION_NOT_FOUND(2001, "会话不存在"),
    SESSION_CREATE_FAILED(2002, "创建会话失败"),
    SESSION_MESSAGE_EMPTY(2003, "会话消息为空"),

    // Agent 相关错误 (3000-3999)
    AGENT_LOOP_TIMEOUT(3001, "Agent 执行超时（超过最大步数）"),
    AGENT_INITIALIZATION_FAILED(3002, "Agent 初始化失败"),
    AGENT_LLM_CALL_FAILED(3003, "LLM 调用失败"),
    AGENT_MAX_STEPS_EXCEEDED(3004, "Agent 执行超过最大步数限制"),

    // Tool 相关错误 (4000-4999)
    TOOL_NOT_FOUND(4001, "工具不存在"),
    TOOL_EXECUTION_FAILED(4002, "工具执行失败"),
    TOOL_REGISTRATION_FAILED(4003, "工具注册失败"),
    TOOL_PARAMETER_INVALID(4004, "工具参数无效"),

    // Skill 相关错误 (5000-5999)
    SKILL_NOT_FOUND(5001, "技能不存在"),
    SKILL_REGISTRATION_FAILED(5002, "技能注册失败"),

    // LLM 相关错误 (6000-6999)
    LLM_PROVIDER_NOT_FOUND(6001, "LLM 提供商不存在"),
    LLM_CONFIGURATION_INVALID(6002, "LLM 配置无效"),
    LLM_API_CALL_FAILED(6003, "LLM API 调用失败"),

    // Prompt 相关错误 (7000-7999)
    PROMPT_TEMPLATE_NOT_FOUND(7001, "提示词模板不存在"),
    PROMPT_VARIABLE_MISSING(7002, "提示词变量缺失"),

    // Extension 相关错误 (8000-8999)
    EXTENSION_LOAD_FAILED(8001, "扩展加载失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
