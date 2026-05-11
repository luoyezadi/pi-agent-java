package com.example.piagent.api;

import com.example.piagent.agent.AgentLoop;
import com.example.piagent.agent.AgentRequest;
import com.example.piagent.agent.AgentResponse;
import com.example.piagent.common.ApiResponse;
import com.example.piagent.event.AgentEvent;
import com.example.piagent.event.AgentEventPublisher;
import com.example.piagent.event.AgentEventType;
import com.example.piagent.session.AgentSession;
import com.example.piagent.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Agent REST API 控制器
 *
 * 设计目的：
 * - 提供 Agent 的 HTTP 接口
 * - 支持同步和 SSE 异步调用
 * - 统一返回格式
 */
@RestController
@RequestMapping("/api/v1/agent")
public class AgentController {

    private static final Logger log = LoggerFactory.getLogger(AgentController.class);

    private final AgentLoop agentLoop;
    private final SessionService sessionService;
    private final Map<String, SseEmitter> sessionEmitters = new ConcurrentHashMap<>();

    public AgentController(AgentLoop agentLoop, SessionService sessionService) {
        this.agentLoop = agentLoop;
        this.sessionService = sessionService;
    }

    @PostMapping("/chat")
    public ApiResponse<AgentResponse> chat(@RequestBody ChatRequest request) {
        log.info("收到聊天请求: {}", request.getMessage());

        AgentRequest agentRequest = AgentRequest.builder()
                .sessionId(request.getSessionId())
                .userMessage(request.getMessage())
                .systemPrompt(request.getSystemPrompt())
                .skillName(request.getSkillName())
                .build();

        AgentResponse response = agentLoop.execute(agentRequest);

        return ApiResponse.success(response);
    }

    @PostMapping("/chat/stream")
    public SseEmitter chatStream(@RequestBody ChatRequest request) {
        log.info("收到 SSE 聊天请求: {}", request.getMessage());

        String sessionId = request.getSessionId() != null
                ? request.getSessionId()
                : "temp_" + System.currentTimeMillis();

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sessionEmitters.put(sessionId, emitter);

        emitter.onCompletion(() -> sessionEmitters.remove(sessionId));
        emitter.onTimeout(() -> sessionEmitters.remove(sessionId));
        emitter.onError(e -> sessionEmitters.remove(sessionId));

        new Thread(() -> {
            try {
                sendEvent(emitter, "session_id", sessionId);

                AgentRequest agentRequest = AgentRequest.builder()
                        .sessionId(sessionId)
                        .userMessage(request.getMessage())
                        .systemPrompt(request.getSystemPrompt())
                        .skillName(request.getSkillName())
                        .build();

                sendEvent(emitter, "started", "Agent 开始执行...");

                AgentResponse response = agentLoop.execute(agentRequest);

                for (var step : response.getSteps()) {
                    String stepJson = String.format(
                            "{\"step\":%d,\"type\":\"%s\",\"description\":\"%s\"}",
                            step.getStepNumber(),
                            step.getType(),
                            step.getDescription()
                    );
                    sendEvent(emitter, "step", stepJson);
                }

                sendEvent(emitter, "finished", response.getFinalAnswer());
                emitter.complete();

            } catch (Exception e) {
                log.error("SSE 执行异常", e);
                try {
                    sendEvent(emitter, "error", e.getMessage());
                    emitter.completeWithError(e);
                } catch (IOException ex) {
                    log.error("发送错误事件失败", ex);
                }
            } finally {
                sessionEmitters.remove(sessionId);
            }
        }).start();

        return emitter;
    }

    private void sendEvent(SseEmitter emitter, String eventName, Object data) throws IOException {
        emitter.send(SseEmitter.event()
                .name(eventName)
                .data(data));
    }

    @GetMapping("/session/{sessionId}")
    public ApiResponse<AgentSession> getSession(@PathVariable String sessionId) {
        AgentSession session = sessionService.getSession(sessionId);
        return ApiResponse.success(session);
    }

    @GetMapping("/sessions")
    public ApiResponse<List<AgentSession>> listSessions() {
        List<AgentSession> sessions = sessionService.listSessions();
        return ApiResponse.success(sessions);
    }

    public static class ChatRequest {
        private String sessionId;
        private String message;
        private String systemPrompt;
        private String skillName;

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSystemPrompt() {
            return systemPrompt;
        }

        public void setSystemPrompt(String systemPrompt) {
            this.systemPrompt = systemPrompt;
        }

        public String getSkillName() {
            return skillName;
        }

        public void setSkillName(String skillName) {
            this.skillName = skillName;
        }
    }
}
