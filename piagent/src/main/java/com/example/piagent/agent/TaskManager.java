package com.example.piagent.agent;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 任务管理器
 *
 * 设计目的：
 * - 管理所有任务的创建、跟踪和查询
 * - 支持子任务管理
 * - 提供任务状态查询
 */
@Component
public class TaskManager {

    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    /**
     * 创建新任务
     */
    public Task createTask(String description) {
        Task task = Task.create(description);
        tasks.put(task.getId(), task);
        return task;
    }

    /**
     * 创建子任务
     */
    public Task createSubTask(String description, String parentTaskId) {
        Task task = Task.create(description, parentTaskId);
        tasks.put(task.getId(), task);
        return task;
    }

    /**
     * 获取任务
     */
    public Optional<Task> getTask(String taskId) {
        return Optional.ofNullable(tasks.get(taskId));
    }

    /**
     * 获取所有任务
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * 获取父任务的子任务
     */
    public List<Task> getSubTasks(String parentTaskId) {
        return tasks.values().stream()
                .filter(t -> parentTaskId.equals(t.getParentTaskId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取任务的所有后代任务（递归）
     */
    public List<Task> getAllDescendantTasks(String parentTaskId) {
        List<Task> descendants = new ArrayList<>();
        List<Task> children = getSubTasks(parentTaskId);
        for (Task child : children) {
            descendants.add(child);
            descendants.addAll(getAllDescendantTasks(child.getId()));
        }
        return descendants;
    }

    /**
     * 删除任务
     */
    public boolean deleteTask(String taskId) {
        return tasks.remove(taskId) != null;
    }

    /**
     * 删除任务及其所有子任务
     */
    public void deleteTaskTree(String taskId) {
        deleteTask(taskId);
        getSubTasks(taskId).forEach(t -> deleteTaskTree(t.getId()));
    }

    /**
     * 获取正在运行的任务
     */
    public List<Task> getRunningTasks() {
        return tasks.values().stream()
                .filter(Task::isRunning)
                .collect(Collectors.toList());
    }

    /**
     * 获取已完成的任务
     */
    public List<Task> getCompletedTasks() {
        return tasks.values().stream()
                .filter(Task::isCompleted)
                .collect(Collectors.toList());
    }

    /**
     * 获取失败的任务
     */
    public List<Task> getFailedTasks() {
        return tasks.values().stream()
                .filter(Task::isFailed)
                .collect(Collectors.toList());
    }

    /**
     * 清理已完成的任务
     */
    public void cleanupCompleted() {
        tasks.entrySet().removeIf(e ->
                e.getValue().isCompleted() || e.getValue().isFailed()
        );
    }

    /**
     * 任务数量
     */
    public int count() {
        return tasks.size();
    }
}
