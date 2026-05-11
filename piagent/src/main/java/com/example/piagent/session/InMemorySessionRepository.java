package com.example.piagent.session;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 基于内存的 Session 仓库实现
 *
 * 设计目的：
 * - 提供快速的内存存储实现，用于开发和测试
 * - 使用 ConcurrentHashMap 保证线程安全
 * - 实现 SessionRepository 接口，便于后续切换到数据库实现
 *
 * 为什么使用 ConcurrentHashMap？
 * - 支持高并发读写操作
 * - 比 synchronized 性能更好
 * - 适合多线程 Web 应用场景
 *
 * 局限性：
 * - 重启后数据丢失
 * - 不适合大规模数据存储
 * - 不支持分布式场景
 */
@Repository
public class InMemorySessionRepository implements SessionRepository {

    private final Map<String, AgentSession> sessions = new ConcurrentHashMap<>();
    private final AtomicLong sessionCounter = new AtomicLong(0);

    @Override
    public AgentSession createSession(String name) {
        String id = "session_" + sessionCounter.incrementAndGet();
        return createSession(id, name);
    }

    @Override
    public AgentSession createSession(String id, String name) {
        AgentSession session = AgentSession.builder()
                .id(id)
                .name(name)
                .build();
        sessions.put(id, session);
        return session;
    }

    @Override
    public Optional<AgentSession> findById(String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    @Override
    public List<AgentSession> findAll() {
        return sessions.values().stream()
                .toList();
    }

    @Override
    public boolean deleteById(String id) {
        return sessions.remove(id) != null;
    }

    @Override
    public long count() {
        return sessions.size();
    }
}
