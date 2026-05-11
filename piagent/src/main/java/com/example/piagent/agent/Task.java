package com.example.piagent.agent;

import java.time.Instant;
import java.util.UUID;

/**
 * 任务模型
 *
 * 设计目的：
 * - 表示主智能体分配给子智能体的任务
 * - 跟踪任务执行状态
 * - 存储任务结果
 *
 * 生命周期：
 * 1. CREATED - 任务创建
 * 2. RUNNING - 任务执行中
 * 3. COMPLETED - 任务完成
 * 4. FAILED - 任务失败
 */
public class Task {

    private final String id;
    private final String description;
    private final String parentTaskId;
    private TaskStatus status;
    private String result;
    private String error;
    private final Instant createdAt;
    private Instant startedAt;
    private Instant completedAt;

    public Task(String id, String description, String parentTaskId) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.description = description;
        this.parentTaskId = parentTaskId;
        this.status = TaskStatus.CREATED;
        this.createdAt = Instant.now();
    }

    public static Task create(String description) {
        return new Task(null, description, null);
    }

    public static Task create(String description, String parentTaskId) {
        return new Task(null, description, parentTaskId);
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getParentTaskId() {
        return parentTaskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void start() {
        this.status = TaskStatus.RUNNING;
        this.startedAt = Instant.now();
    }

    public void complete(String result) {
        this.status = TaskStatus.COMPLETED;
        this.result = result;
        this.completedAt = Instant.now();
    }

    public void fail(String error) {
        this.status = TaskStatus.FAILED;
        this.error = error;
        this.completedAt = Instant.now();
    }

    public String getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == TaskStatus.FAILED;
    }

    public boolean isRunning() {
        return status == TaskStatus.RUNNING;
    }

    public enum TaskStatus {
        CREATED,
        RUNNING,
        COMPLETED,
        FAILED
    }
}
