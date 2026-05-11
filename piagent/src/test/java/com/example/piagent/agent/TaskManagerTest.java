package com.example.piagent.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TaskManager 任务管理器测试
 */
@DisplayName("TaskManager 测试")
class TaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new TaskManager();
    }

    @Nested
    @DisplayName("创建任务测试")
    class CreateTaskTest {

        @Test
        @DisplayName("应该成功创建任务")
        void shouldCreateTaskSuccessfully() {
            Task task = taskManager.createTask("测试任务");

            assertThat(task).isNotNull();
            assertThat(task.getDescription()).isEqualTo("测试任务");
            assertThat(task.getId()).isNotNull();
        }

        @Test
        @DisplayName("应该成功创建子任务")
        void shouldCreateSubTask() {
            Task parent = taskManager.createTask("父任务");
            Task child = taskManager.createSubTask("子任务", parent.getId());

            assertThat(child.getParentTaskId()).isEqualTo(parent.getId());
        }
    }

    @Nested
    @DisplayName("查询任务测试")
    class QueryTaskTest {

        @Test
        @DisplayName("应该能获取所有子任务")
        void shouldGetSubTasks() {
            Task parent = taskManager.createTask("父任务");
            taskManager.createSubTask("子任务1", parent.getId());
            taskManager.createSubTask("子任务2", parent.getId());

            List<Task> subTasks = taskManager.getSubTasks(parent.getId());

            assertThat(subTasks).hasSize(2);
        }

        @Test
        @DisplayName("应该能获取运行中的任务")
        void shouldGetRunningTasks() {
            Task task1 = taskManager.createTask("任务1");
            Task task2 = taskManager.createTask("任务2");
            task1.start();

            List<Task> running = taskManager.getRunningTasks();

            assertThat(running).hasSize(1);
            assertThat(running.get(0).getDescription()).isEqualTo("任务1");
        }

        @Test
        @DisplayName("应该能获取已完成的任务")
        void shouldGetCompletedTasks() {
            Task task1 = taskManager.createTask("任务1");
            Task task2 = taskManager.createTask("任务2");
            task1.complete("结果");

            List<Task> completed = taskManager.getCompletedTasks();

            assertThat(completed).hasSize(1);
        }
    }

    @Nested
    @DisplayName("删除任务测试")
    class DeleteTaskTest {

        @Test
        @DisplayName("应该能删除任务")
        void shouldDeleteTask() {
            Task task = taskManager.createTask("测试任务");

            boolean deleted = taskManager.deleteTask(task.getId());

            assertThat(deleted).isTrue();
            assertThat(taskManager.getTask(task.getId())).isEmpty();
        }

        @Test
        @DisplayName("应该能删除任务树")
        void shouldDeleteTaskTree() {
            Task parent = taskManager.createTask("父任务");
            Task child1 = taskManager.createSubTask("子任务1", parent.getId());
            Task child2 = taskManager.createSubTask("子任务2", parent.getId());

            taskManager.deleteTaskTree(parent.getId());

            assertThat(taskManager.getTask(parent.getId())).isEmpty();
            assertThat(taskManager.getTask(child1.getId())).isEmpty();
            assertThat(taskManager.getTask(child2.getId())).isEmpty();
        }
    }
}
