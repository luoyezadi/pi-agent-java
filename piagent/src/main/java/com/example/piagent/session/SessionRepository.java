package com.example.piagent.session;

import java.util.List;
import java.util.Optional;

/**
 * Session 仓库接口
 *
 * 设计目的：
 * - 定义会话存储的标准操作
 * - 预留数据库持久化扩展点
 * - 使用 Optional 处理可能不存在的情况，避免 NPE
 *
 * 为什么使用 Optional？
 * - 明确表示"可能不存在"的情况
 * - 强制调用方处理不存在的情况
 *
 * 为什么返回 List 而非 Collection？
 * - List 是最常用的集合类型
 * - 调用方通常需要有序的会话列表
 */
public interface SessionRepository {

    /**
     * 创建新会话
     */
    AgentSession createSession(String name);

    /**
     * 创建带自定义 ID 的会话
     */
    AgentSession createSession(String id, String name);

    /**
     * 根据 ID 查找会话
     */
    Optional<AgentSession> findById(String id);

    /**
     * 查找所有会话
     */
    List<AgentSession> findAll();

    /**
     * 根据 ID 删除会话
     */
    boolean deleteById(String id);

    /**
     * 返回会话总数
     */
    long count();
}
