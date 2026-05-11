package com.example.piagent.agent;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 子智能体注册表
 *
 * 设计目的：
 * - 管理所有子智能体的注册和查询
 * - 提供按类型、专长查询功能
 * - 线程安全实现
 */
@Component
public class SubAgentRegistry {

    private final Map<String, SubAgent> agents = new ConcurrentHashMap<>();

    /**
     * 注册子智能体
     */
    public boolean register(SubAgent agent) {
        if (agent == null || agent.getId() == null) {
            return false;
        }
        if (agents.containsKey(agent.getId())) {
            return false;
        }
        agents.put(agent.getId(), agent);
        return true;
    }

    /**
     * 注销子智能体
     */
    public boolean unregister(String agentId) {
        SubAgent agent = agents.remove(agentId);
        if (agent != null) {
            agent.setStatus(SubAgent.AgentStatus.OFFLINE);
            return true;
        }
        return false;
    }

    /**
     * 根据 ID 获取子智能体
     */
    public Optional<SubAgent> get(String agentId) {
        return Optional.ofNullable(agents.get(agentId));
    }

    /**
     * 获取所有子智能体
     */
    public List<SubAgent> getAll() {
        return new ArrayList<>(agents.values());
    }

    /**
     * 获取所有可用的子智能体
     */
    public List<SubAgent> getAvailable() {
        return agents.values().stream()
                .filter(SubAgent::isAvailable)
                .collect(Collectors.toList());
    }

    /**
     * 根据类型获取子智能体
     */
    public List<SubAgent> getByType(String type) {
        return agents.values().stream()
                .filter(a -> type.equals(a.getType()))
                .filter(SubAgent::isAvailable)
                .collect(Collectors.toList());
    }

    /**
     * 根据专长获取子智能体
     */
    public List<SubAgent> getByExpertise(String expertise) {
        return agents.values().stream()
                .filter(a -> a.getExpertise().contains(expertise))
                .filter(SubAgent::isAvailable)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有子智能体数量
     */
    public int count() {
        return agents.size();
    }

    /**
     * 获取可用子智能体数量
     */
    public int availableCount() {
        return (int) agents.values().stream()
                .filter(SubAgent::isAvailable)
                .count();
    }

    /**
     * 更新子智能体状态
     */
    public void updateStatus(String agentId, SubAgent.AgentStatus status) {
        SubAgent agent = agents.get(agentId);
        if (agent != null) {
            agent.setStatus(status);
        }
    }
}
