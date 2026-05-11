package com.example.piagent.agent;

import java.util.ArrayList;
import java.util.List;

/**
 * Agent 响应
 *
 * 设计目的：
 * - 封装 Agent 执行的结果
 * - 包含最终回复和执行步骤
 * - 支持判断是否成功、是否达到最大步数
 */
public class AgentResponse {

    private final String sessionId;
    private final boolean success;
    private final String finalAnswer;
    private final List<AgentStep> steps;
    private final boolean maxStepsReached;
    private final String error;

    private AgentResponse(Builder builder) {
        this.sessionId = builder.sessionId;
        this.success = builder.success;
        this.finalAnswer = builder.finalAnswer;
        this.steps = builder.steps != null ? new ArrayList<>(builder.steps) : new ArrayList<>();
        this.maxStepsReached = builder.maxStepsReached;
        this.error = builder.error;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFinalAnswer() {
        return finalAnswer;
    }

    public List<AgentStep> getSteps() {
        return new ArrayList<>(steps);
    }

    public boolean isMaxStepsReached() {
        return maxStepsReached;
    }

    public String getError() {
        return error;
    }

    public static class Builder {
        private String sessionId;
        private boolean success;
        private String finalAnswer;
        private List<AgentStep> steps;
        private boolean maxStepsReached;
        private String error;

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder finalAnswer(String finalAnswer) {
            this.finalAnswer = finalAnswer;
            return this;
        }

        public Builder steps(List<AgentStep> steps) {
            this.steps = steps;
            return this;
        }

        public Builder maxStepsReached(boolean maxStepsReached) {
            this.maxStepsReached = maxStepsReached;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public AgentResponse build() {
            return new AgentResponse(this);
        }
    }
}
