package com.example.piagent.session;

import com.example.piagent.common.BizException;
import com.example.piagent.common.ErrorCode;
import com.example.piagent.message.Message;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会话服务
 *
 * 设计目的：
 * - 提供会话的业务操作封装
 * - 统一异常处理，返回业务异常而非技术异常
 * - 隔离 Repository 和 Controller，便于后续扩展
 *
 * 为什么需要服务层？
 * - Controller 负责 HTTP 请求处理，服务层负责业务逻辑
 * - 便于后续添加事务管理、缓存等功能
 * - 单一职责原则：每个类只做一件事
 */
@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    /**
     * 创建新会话
     */
    public AgentSession createSession(String name) {
        return sessionRepository.createSession(name);
    }

    /**
     * 获取会话
     *
     * @throws BizException 会话不存在时抛出
     */
    public AgentSession getSession(String sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BizException(ErrorCode.SESSION_NOT_FOUND,
                        "会话不存在: " + sessionId));
    }

    /**
     * 添加消息到会话
     *
     * @throws BizException 会话不存在时抛出
     */
    public void addMessage(String sessionId, Message message) {
        AgentSession session = getSession(sessionId);
        session.addMessage(message);
    }

    /**
     * 获取会话消息历史
     *
     * @throws BizException 会话不存在时抛出
     */
    public List<Message> getMessageHistory(String sessionId) {
        AgentSession session = getSession(sessionId);
        return session.getMessages();
    }

    /**
     * 删除会话
     */
    public boolean deleteSession(String sessionId) {
        return sessionRepository.deleteById(sessionId);
    }

    /**
     * 列出所有会话
     */
    public List<AgentSession> listSessions() {
        return sessionRepository.findAll();
    }
}
