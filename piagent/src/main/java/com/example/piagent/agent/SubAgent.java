package com.example.piagent.agent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 子智能体定义
 *
 * 设计目的：
 * - 定义子智能体的元数据
 * - 支持不同类型的子智能体（researcher、coder、analyst等）
 * - 关联对应的工具和技能
 *
 * 使用场景：
 * - 主智能体根据任务类型分配给合适的子智能体
 * - 子智能体可以并行执行不同子任务
 */
public class SubAgent {

    private final String id;
    private final String name;
    private final String type;
    private final List<String> expertise;
    private final List<String> tools;
    private final List<String> skills;
    private final AgentLoop agentLoop;
    private AgentStatus status;
    private final Instant createdAt;

    public SubAgent(
            String id,
            String name,
            String type,
            List<String> expertise,
            List<String> tools,
            List<String> skills,
            AgentLoop agentLoop
    ) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.expertise = expertise != null ? List.copyOf(expertise) : List.of();
        this.tools = tools != null ? List.copyOf(tools) : List.of();
        this.skills = skills != null ? List.copyOf(skills) : List.of();
        this.agentLoop = agentLoop;
        this.status = AgentStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<String> getExpertise() {
        return expertise;
    }

    public List<String> getTools() {
        return tools;
    }

    public List<String> getSkills() {
        return skills;
    }

    public AgentLoop getAgentLoop() {
        return agentLoop;
    }

    public AgentStatus getStatus() {
        return status;
    }

    public void setStatus(AgentStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isAvailable() {
        return status == AgentStatus.ACTIVE && agentLoop != null;
    }

    public enum AgentStatus {
        ACTIVE,
        BUSY,
        OFFLINE
    }

    public static class Builder {
        private String id;
        private String name;
        private String type;
        private List<String> expertise;
        private List<String> tools;
        private List<String> skills;
        private AgentLoop agentLoop;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder expertise(List<String> expertise) {
            this.expertise = expertise;
            return this;
        }

        public Builder tools(List<String> tools) {
            this.tools = tools;
            return this;
        }

        public Builder skills(List<String> skills) {
            this.skills = skills;
            return this;
        }

        public Builder agentLoop(AgentLoop agentLoop) {
            this.agentLoop = agentLoop;
            return this;
        }

        public SubAgent build() {
            return new SubAgent(id, name, type, expertise, tools, skills, agentLoop);
        }
    }
}
