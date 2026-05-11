package com.example.piagent.agent;

import com.example.piagent.common.BizException;
import com.example.piagent.common.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 主智能体管理器（Manager Agent）
 *
 * 设计目的：
 * - 统一管理所有子智能体
 * - 实现任务分解和分配
 * - 支持并行执行和结果汇总
 *
 * 核心功能：
 * 1. 子智能体注册和管理
 * 2. 任务自动分解（Task Decomposition）
 * 3. 智能任务分配（基于专长和负载）
 * 4. 结果聚合和汇总
 * 5. 支持同步和异步执行
 *
 * 任务分配策略：
 * - 技能匹配：优先选择有相关技能的子智能体
 * - 负载均衡：选择当前空闲的子智能体
 * - 类型匹配：根据任务类型分配对应类型的子智能体
 */
@Component
public class AgentManager {

    private static final Logger log = LoggerFactory.getLogger(AgentManager.class);

    private final SubAgentRegistry subAgentRegistry;
    private final TaskManager taskManager;
    private final ExecutorService executorService;

    public AgentManager(SubAgentRegistry subAgentRegistry, TaskManager taskManager) {
        this.subAgentRegistry = subAgentRegistry;
        this.taskManager = taskManager;
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * 注册子智能体
     */
    public boolean registerSubAgent(SubAgent agent) {
        if (agent == null) {
            throw new BizException(ErrorCode.INVALID_PARAMETER, "子智能体不能为空");
        }
        if (agent.getAgentLoop() == null) {
            throw new BizException(ErrorCode.INVALID_PARAMETER, "子智能体必须包含 AgentLoop");
        }
        boolean registered = subAgentRegistry.register(agent);
        if (registered) {
            log.info("子智能体注册成功: {} ({})", agent.getName(), agent.getId());
        }
        return registered;
    }

    /**
     * 注销子智能体
     */
    public boolean unregisterSubAgent(String agentId) {
        boolean unregistered = subAgentRegistry.unregister(agentId);
        if (unregistered) {
            log.info("子智能体已注销: {}", agentId);
        }
        return unregistered;
    }

    /**
     * 获取子智能体
     */
    public SubAgent getSubAgent(String agentId) {
        return subAgentRegistry.get(agentId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "子智能体不存在: " + agentId));
    }

    /**
     * 列出所有子智能体
     */
    public List<SubAgent> listSubAgents() {
        return subAgentRegistry.getAll();
    }

    /**
     * 执行单任务（同步）
     *
     * @param taskDescription 任务描述
     * @param subAgentId      子智能体 ID（可选，为空则自动选择）
     * @return 任务结果
     */
    public TaskResult executeTask(String taskDescription, String subAgentId) {
        Task task = taskManager.createTask(taskDescription);

        try {
            SubAgent agent = selectBestAgent(subAgentId, taskDescription);
            if (agent == null) {
                task.fail("没有可用的子智能体");
                return TaskResult.failure("没有可用的子智能体");
            }

            task.start();
            subAgentRegistry.updateStatus(agent.getId(), SubAgent.AgentStatus.BUSY);

            log.info("任务分配给子智能体: {} ({})", agent.getName(), agent.getId());

            AgentRequest request = AgentRequest.builder()
                    .userMessage(taskDescription)
                    .build();

            AgentResponse response = agent.getAgentLoop().execute(request);

            if (response.isSuccess()) {
                task.complete(response.getFinalAnswer());
                log.info("任务完成: {}", task.getId());
            } else {
                task.fail(response.getError());
                log.error("任务失败: {}", task.getId());
            }

            subAgentRegistry.updateStatus(agent.getId(), SubAgent.AgentStatus.ACTIVE);

            return new TaskResult(task.getId(), response.isSuccess(), response.getFinalAnswer(), task.getError());

        } catch (Exception e) {
            task.fail(e.getMessage());
            log.error("任务执行异常: {}", task.getId(), e);
            return TaskResult.failure(e.getMessage());
        }
    }

    /**
     * 执行并行任务（多子智能体协作）
     *
     * @param taskDescriptions 多个任务描述
     * @return 多个任务结果
     */
    public List<TaskResult> executeParallelTasks(List<String> taskDescriptions) {
        List<CompletableFuture<TaskResult>> futures = taskDescriptions.stream()
                .map(desc -> CompletableFuture.supplyAsync(
                        () -> executeTask(desc, null),
                        executorService
                ))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    /**
     * 执行研究任务（Deep Research）
     *
     * 自动分解任务给多个研究子智能体
     */
    public ResearchResult executeResearch(String topic) {
        log.info("开始深度研究: {}", topic);

        Task mainTask = taskManager.createTask("深度研究: " + topic);
        mainTask.start();

        List<String> subTasks = decomposeResearchTask(topic);

        log.info("任务分解为 {} 个子任务", subTasks.size());

        List<TaskResult> results = executeParallelTasks(subTasks);

        String summary = aggregateResults(results);

        mainTask.complete(summary);

        return new ResearchResult(mainTask.getId(), topic, subTasks, results, summary);
    }

    /**
     * 分解研究任务
     *
     * 将复杂的研究主题分解为多个子主题
     */
    private List<String> decomposeResearchTask(String topic) {
        return List.of(
                "搜索 " + topic + " 的最新发展动态",
                "分析 " + topic + " 的核心技术原理",
                "调研 " + topic + " 的主要应用场景",
                "整理 " + topic + " 的优缺点分析",
                "总结 " + topic + " 的发展趋势和展望"
        );
    }

    /**
     * 聚合结果
     */
    private String aggregateResults(List<TaskResult> results) {
        StringBuilder summary = new StringBuilder();
        summary.append("# 研究结果汇总\n\n");

        for (int i = 0; i < results.size(); i++) {
            TaskResult result = results.get(i);
            summary.append("## 子任务 ").append(i + 1).append("\n");
            if (result.isSuccess()) {
                summary.append(result.getResult()).append("\n\n");
            } else {
                summary.append("*任务失败: ").append(result.getError()).append("*\n\n");
            }
        }

        summary.append("## 综合结论\n");
        summary.append("基于以上研究，");
        summary.append(results.stream()
                .filter(TaskResult::isSuccess)
                .count());
        summary.append("/");
        summary.append(results.size());
        summary.append(" 个子任务完成。");

        return summary.toString();
    }

    /**
     * 选择最佳子智能体
     */
    private SubAgent selectBestAgent(String preferredAgentId, String taskDescription) {
        if (preferredAgentId != null) {
            Optional<SubAgent> agent = subAgentRegistry.get(preferredAgentId);
            if (agent.isPresent() && agent.get().isAvailable()) {
                return agent.get();
            }
        }

        List<SubAgent> availableAgents = subAgentRegistry.getAvailable();
        if (availableAgents.isEmpty()) {
            return null;
        }

        String lowerTask = taskDescription.toLowerCase();

        for (SubAgent agent : availableAgents) {
            for (String expertise : agent.getExpertise()) {
                if (lowerTask.contains(expertise.toLowerCase())) {
                    return agent;
                }
            }
        }

        return availableAgents.get(0);
    }

    /**
     * 获取任务状态
     */
    public Task getTask(String taskId) {
        return taskManager.getTask(taskId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "任务不存在: " + taskId));
    }

    /**
     * 获取子任务的执行结果
     */
    public List<Task> getSubTasks(String parentTaskId) {
        return taskManager.getSubTasks(parentTaskId);
    }

    /**
     * 任务结果模型
     */
    public static class TaskResult {
        private final String taskId;
        private final boolean success;
        private final String result;
        private final String error;

        public TaskResult(String taskId, boolean success, String result, String error) {
            this.taskId = taskId;
            this.success = success;
            this.result = result;
            this.error = error;
        }

        public static TaskResult failure(String error) {
            return new TaskResult(null, false, null, error);
        }

        public String getTaskId() {
            return taskId;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getResult() {
            return result;
        }

        public String getError() {
            return error;
        }
    }

    /**
     * 研究结果模型
     */
    public static class ResearchResult {
        private final String taskId;
        private final String topic;
        private final List<String> subTasks;
        private final List<TaskResult> results;
        private final String summary;

        public ResearchResult(String taskId, String topic, List<String> subTasks,
                             List<TaskResult> results, String summary) {
            this.taskId = taskId;
            this.topic = topic;
            this.subTasks = subTasks;
            this.results = results;
            this.summary = summary;
        }

        public String getTaskId() {
            return taskId;
        }

        public String getTopic() {
            return topic;
        }

        public List<String> getSubTasks() {
            return subTasks;
        }

        public List<TaskResult> getResults() {
            return results;
        }

        public String getSummary() {
            return summary;
        }

        public int getSuccessCount() {
            return (int) results.stream().filter(TaskResult::isSuccess).count();
        }
    }
}
