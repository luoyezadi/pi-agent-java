package com.example.piagent.api;

import com.example.piagent.agent.AgentManager;
import com.example.piagent.agent.AgentRequest;
import com.example.piagent.agent.AgentResponse;
import com.example.piagent.agent.AgentLoop;
import com.example.piagent.agent.SubAgent;
import com.example.piagent.agent.SubAgentRegistry;
import com.example.piagent.agent.Task;
import com.example.piagent.common.ApiResponse;
import com.example.piagent.llm.ChatModelClient;
import com.example.piagent.session.SessionService;
import com.example.piagent.tool.ToolService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主智能体管理器 REST API
 *
 * 提供子智能体管理和任务执行的 HTTP 接口
 */
@RestController
@RequestMapping("/api/v1/manager")
public class AgentManagerController {

    private final AgentManager agentManager;
    private final SubAgentRegistry subAgentRegistry;
    private final SessionService sessionService;
    private final ToolService toolService;
    private final ChatModelClient chatModelClient;

    public AgentManagerController(
            AgentManager agentManager,
            SubAgentRegistry subAgentRegistry,
            SessionService sessionService,
            ToolService toolService,
            ChatModelClient chatModelClient
    ) {
        this.agentManager = agentManager;
        this.subAgentRegistry = subAgentRegistry;
        this.sessionService = sessionService;
        this.toolService = toolService;
        this.chatModelClient = chatModelClient;
    }

    /**
     * 注册新的子智能体
     */
    @PostMapping("/agents")
    public ApiResponse<SubAgent> registerAgent(@RequestBody RegisterAgentRequest request) {
        SubAgent agent = SubAgent.builder()
                .name(request.getName())
                .type(request.getType())
                .expertise(request.getExpertise())
                .tools(request.getTools())
                .skills(request.getSkills())
                .agentLoop(createAgentLoop())
                .build();

        boolean success = agentManager.registerSubAgent(agent);

        if (success) {
            return ApiResponse.success("子智能体注册成功", agent);
        } else {
            return ApiResponse.error(400, "子智能体注册失败，可能已存在同名智能体");
        }
    }

    /**
     * 注销子智能体
     */
    @DeleteMapping("/agents/{agentId}")
    public ApiResponse<Void> unregisterAgent(@PathVariable String agentId) {
        boolean success = agentManager.unregisterSubAgent(agentId);
        if (success) {
            return ApiResponse.success("子智能体已注销", null);
        } else {
            return ApiResponse.error(404, "子智能体不存在");
        }
    }

    /**
     * 获取所有子智能体
     */
    @GetMapping("/agents")
    public ApiResponse<List<SubAgent>> listAgents() {
        List<SubAgent> agents = agentManager.listSubAgents();
        return ApiResponse.success(agents);
    }

    /**
     * 获取指定子智能体
     */
    @GetMapping("/agents/{agentId}")
    public ApiResponse<SubAgent> getAgent(@PathVariable String agentId) {
        try {
            SubAgent agent = agentManager.getSubAgent(agentId);
            return ApiResponse.success(agent);
        } catch (Exception e) {
            return ApiResponse.error(404, "子智能体不存在: " + agentId);
        }
    }

    /**
     * 执行单任务
     */
    @PostMapping("/tasks")
    public ApiResponse<AgentManager.TaskResult> executeTask(@RequestBody TaskRequest request) {
        AgentManager.TaskResult result = agentManager.executeTask(
                request.getDescription(),
                request.getAgentId()
        );
        return ApiResponse.success(result);
    }

    /**
     * 执行并行任务
     */
    @PostMapping("/tasks/parallel")
    public ApiResponse<List<AgentManager.TaskResult>> executeParallelTasks(
            @RequestBody ParallelTaskRequest request
    ) {
        List<AgentManager.TaskResult> results = agentManager.executeParallelTasks(
                request.getTasks()
        );
        return ApiResponse.success(results);
    }

    /**
     * 执行深度研究任务
     */
    @PostMapping("/research")
    public ApiResponse<AgentManager.ResearchResult> executeResearch(@RequestBody ResearchRequest request) {
        AgentManager.ResearchResult result = agentManager.executeResearch(request.getTopic());
        return ApiResponse.success(result);
    }

    /**
     * 获取任务状态
     */
    @GetMapping("/tasks/{taskId}")
    public ApiResponse<Task> getTask(@PathVariable String taskId) {
        try {
            Task task = agentManager.getTask(taskId);
            return ApiResponse.success(task);
        } catch (Exception e) {
            return ApiResponse.error(404, "任务不存在: " + taskId);
        }
    }

    /**
     * 获取子任务列表
     */
    @GetMapping("/tasks/{taskId}/subtasks")
    public ApiResponse<List<Task>> getSubTasks(@PathVariable String taskId) {
        List<Task> subTasks = agentManager.getSubTasks(taskId);
        return ApiResponse.success(subTasks);
    }

    /**
     * 获取管理器统计信息
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAgents", subAgentRegistry.count());
        stats.put("availableAgents", subAgentRegistry.availableCount());
        stats.put("agentTypes", subAgentRegistry.getAll().stream()
                .map(SubAgent::getType)
                .distinct()
                .toList());
        return ApiResponse.success(stats);
    }

    private AgentLoop createAgentLoop() {
        return new AgentLoop(
                chatModelClient,
                sessionService,
                toolService,
                10
        );
    }

    public static class RegisterAgentRequest {
        private String name;
        private String type;
        private List<String> expertise;
        private List<String> tools;
        private List<String> skills;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getExpertise() {
            return expertise;
        }

        public void setExpertise(List<String> expertise) {
            this.expertise = expertise;
        }

        public List<String> getTools() {
            return tools;
        }

        public void setTools(List<String> tools) {
            this.tools = tools;
        }

        public List<String> getSkills() {
            return skills;
        }

        public void setSkills(List<String> skills) {
            this.skills = skills;
        }
    }

    public static class TaskRequest {
        private String description;
        private String agentId;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAgentId() {
            return agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }
    }

    public static class ParallelTaskRequest {
        private List<String> tasks;

        public List<String> getTasks() {
            return tasks;
        }

        public void setTasks(List<String> tasks) {
            this.tasks = tasks;
        }
    }

    public static class ResearchRequest {
        private String topic;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }
    }
}
