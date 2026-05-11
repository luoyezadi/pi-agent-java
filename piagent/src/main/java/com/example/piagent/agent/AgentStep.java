package com.example.piagent.agent;

import com.example.piagent.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Agent 执行步骤
 *
 * 设计目的：
 * - 记录 Agent 执行过程中的每个步骤
 * - 便于调试和追踪执行流程
 * - 支持 SSE 流式输出
 */
public class AgentStep {

    private final int stepNumber;
    private final StepType type;
    private final Message input;
    private final Message output;
    private final List<ToolExecution> toolExecutions;
    private final String description;

    private AgentStep(Builder builder) {
        this.stepNumber = builder.stepNumber;
        this.type = builder.type;
        this.input = builder.input;
        this.output = builder.output;
        this.toolExecutions = builder.toolExecutions != null ? new ArrayList<>(builder.toolExecutions) : null;
        this.description = builder.description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public StepType getType() {
        return type;
    }

    public Message getInput() {
        return input;
    }

    public Message getOutput() {
        return output;
    }

    public List<ToolExecution> getToolExecutions() {
        return toolExecutions != null ? new ArrayList<>(toolExecutions) : null;
    }

    public String getDescription() {
        return description;
    }

    public enum StepType {
        MODEL_CALL,
        TOOL_CALL,
        FINAL_ANSWER
    }

    public static class ToolExecution {
        private final String toolName;
        private final String arguments;
        private final String result;

        public ToolExecution(String toolName, String arguments, String result) {
            this.toolName = toolName;
            this.arguments = arguments;
            this.result = result;
        }

        public String getToolName() {
            return toolName;
        }

        public String getArguments() {
            return arguments;
        }

        public String getResult() {
            return result;
        }
    }

    public static class Builder {
        private int stepNumber;
        private StepType type;
        private Message input;
        private Message output;
        private List<ToolExecution> toolExecutions;
        private String description;

        public Builder stepNumber(int stepNumber) {
            this.stepNumber = stepNumber;
            return this;
        }

        public Builder type(StepType type) {
            this.type = type;
            return this;
        }

        public Builder input(Message input) {
            this.input = input;
            return this;
        }

        public Builder output(Message output) {
            this.output = output;
            return this;
        }

        public Builder toolExecutions(List<ToolExecution> toolExecutions) {
            this.toolExecutions = toolExecutions;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public AgentStep build() {
            return new AgentStep(this);
        }
    }
}
