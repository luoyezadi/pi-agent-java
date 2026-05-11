package com.example.piagent.api;

import com.example.piagent.common.ApiResponse;
import com.example.piagent.session.AgentSession;
import com.example.piagent.session.SessionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Session REST API 控制器
 *
 * 提供会话管理相关接口
 */
@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ApiResponse<AgentSession> createSession(@RequestParam(defaultValue = "新会话") String name) {
        AgentSession session = sessionService.createSession(name);
        return ApiResponse.success("会话创建成功", session);
    }

    @GetMapping("/{sessionId}")
    public ApiResponse<AgentSession> getSession(@PathVariable String sessionId) {
        AgentSession session = sessionService.getSession(sessionId);
        return ApiResponse.success(session);
    }

    @GetMapping
    public ApiResponse<List<AgentSession>> listSessions() {
        List<AgentSession> sessions = sessionService.listSessions();
        return ApiResponse.success(sessions);
    }

    @DeleteMapping("/{sessionId}")
    public ApiResponse<Void> deleteSession(@PathVariable String sessionId) {
        boolean deleted = sessionService.deleteSession(sessionId);
        if (deleted) {
            return ApiResponse.success("会话已删除", null);
        } else {
            return ApiResponse.error(404, "会话不存在");
        }
    }
}
