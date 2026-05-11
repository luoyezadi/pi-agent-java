package com.example.piagent.common;

/**
 * 业务异常基类
 *
 * 设计目的：
 * - 统一业务异常处理，避免直接抛出 RuntimeException
 * - 异常携带错误码，便于前端区分处理
 * - 继承自 RuntimeException，简化抛出语法
 */
public class BizException extends RuntimeException {

    private final int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BizException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
