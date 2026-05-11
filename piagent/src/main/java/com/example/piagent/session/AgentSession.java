package com.example.piagent.session;

import com.example.piagent.message.Message;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Agent 会话模型
 *
 * 设计目的：
 * - 管理单个会话的消息历史
 * - 会话是可变对象，允许动态添加消息
 * - 支持内存存储，便于快速访问
 *
 * 为什么设计为可变对象？
 * - Session 需要动态添加消息
 * - 与 Message 的不可变设计形成对比，因为两者的使用场景不同
 * - Session 类似于数据库中的记录，需要支持更新
 *
 * 为什么不使用线程安全的 List？
 * - 简化实现，降低复杂度
 * - 在单会话场景下，线程安全不是首要考虑
 * - 如需线程安全，可在 SessionRepository 层加锁
 */
public class AgentSession {

    private final String id;
    private final String name;
    private final Instant createdAt;
    private Instant updatedAt;
    private final List<Message> messages;

    private AgentSession(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.name = builder.name;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.messages = new ArrayList<>();
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        this.updatedAt = Instant.now();
    }
}
