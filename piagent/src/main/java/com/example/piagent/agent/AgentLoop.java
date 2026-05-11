package com.example.piagent.agent;

import com.example.piagent.common.BizException;
import com.example.piagent.common.ErrorCode;
import com.example.piagent.event.AgentEventPublisher;
import com.example.piagent.event.AgentEventType;
import com.example.piagent.llm.ChatModelClient;
import com.example.piagent.llm.ChatModelRequest;
import com.example.piagent.llm.ChatModelResponse;
import com.example.piagent.message.Message;
import com.example.piagent.message.MessageFactory;
import com.example.piagent.message.MessageRole;
import com.example.piagent.session.AgentSession;
import com.example.piagent.session.SessionService;
import com.example.piagent.tool.ToolDefinition;
import com.example.piagent.tool.ToolExecutionRequest;
import com.example.piagent.tool.ToolExecutionResult;
import com.example.piagent.tool.ToolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Agent 执行循环核心
 *
 * 设计目的：
 * - 实现 Agent 的核心执行逻辑
 * - 循环调用 LLM 直到得到最终答案或达到最大步数
 * - 支持工具调用、多轮对话
 *
 * 执行流程（Agent Loop）：
 * 1. 接收用户输入，构建消息上下文
 * 2. 调用 LLM 获取回复
 * 3. 检查回复是否包含工具调用
 * 4. 如果有工具调用，执行工具并追加结果
 * 5. 将工具执行结果添加到上下文
 * 6. 继续循环，直到得到最终答案
 * 7. 如果达到最大步数，停止执行
 *
 * 为什么采用循环模式？
 * - Agent 可能需要多次工具调用才能得到答案
 * - 每次工具调用后需要将结果反馈给 LLM
 * - 这种模式类似 ReAct（Reasoning + Acting）模式
 */
@Component
public class AgentLoop {

    private static final Logger log = LoggerFactory.getLogger(AgentLoop.class);

    private final ChatModelClient chatModelClient;
    private final SessionService sessionService;
    private final ToolService toolService;
    private final int maxSteps;

    public AgentLoop(
            ChatModelClient chatModelClient,
            SessionService sessionService,
            ToolService toolService,
            int maxSteps
    ) {
        this.chatModelClient = chatModelClient;
        this.sessionService = sessionService;
        this.toolService = toolService;
        this.maxSteps = maxSteps;
    }

    /**
     * 执行 Agent 循环
     *
     * @param request Agent 请求
     * @return Agent 响应
     */
    public AgentResponse execute(AgentRequest request) {
        String userMessage = request.getUserMessage();
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new BizException(ErrorCode.INVALID_PARAMETER, "用户消息不能为空");
        }

        AgentSession session = getOrCreateSession(request);
        session.addMessage(MessageFactory.createUserMessage(userMessage));

        List<AgentStep> steps = new ArrayList<>();
        String finalAnswer = null;
        boolean isSuccess = true;
        boolean maxStepsReached = false;
        String errorMessage = null;

        try {
            for (int step = 0; step < maxSteps; step++) {
                log.info("执行 Agent 循环第 {} 步", step + 1);

                List<Message> messages = buildMessages(session, request);
                ChatModelRequest llmRequest = buildLlmRequest(messages, request);

                ChatModelResponse llmResponse = chatModelClient.chat(llmRequest);
                Message assistantMessage = llmResponse.getMessage();
                session.addMessage(assistantMessage);

                if (!llmResponse.hasToolCalls()) {
                    finalAnswer = assistantMessage.getContent();
                    steps.add(AgentStep.builder()
                            .stepNumber(step + 1)
                            .type(AgentStep.StepType.FINAL_ANSWER)
                            .input(null)
                            .output(assistantMessage)
                            .description("得到最终回复")
                            .build());
                    break;
                }

                List<AgentStep.ToolExecution> toolExecutions = new ArrayList<>();
                for (ChatModelResponse.ToolCall toolCall : llmResponse.getToolCalls()) {
                    ToolExecutionRequest toolRequest = ToolExecutionRequest.of(
                            toolCall.getName(),
                            toolCall.getArguments()
                    );
                    ToolExecutionResult toolResult = toolService.executeTool(toolRequest);

                    String toolResultContent = toolResult.isSuccess()
                            ? toolResult.getResult()
                            : "工具执行失败: " + toolResult.getError();

                    session.addMessage(MessageFactory.createToolResultMessage(
                            toolCall.getId(),
                            toolResultContent
                    ));

                    toolExecutions.add(new AgentStep.ToolExecution(
                            toolCall.getName(),
                            toolCall.getArguments(),
                            toolResultContent
                    ));
                }

                steps.add(AgentStep.builder()
                        .stepNumber(step + 1)
                        .type(AgentStep.StepType.MODEL_CALL)
                        .input(null)
                        .output(assistantMessage)
                        .toolExecutions(toolExecutions)
                        .description("执行了 " + toolExecutions.size() + " 个工具调用")
                        .build());

                if (step == maxSteps - 1) {
                    maxStepsReached = true;
                    finalAnswer = "已达到最大执行步数，无法继续执行。";
                }
            }
        } catch (Exception e) {
            log.error("Agent 执行异常", e);
            isSuccess = false;
            errorMessage = e.getMessage();
            finalAnswer = "执行过程中发生错误: " + e.getMessage();
        }

        return AgentResponse.builder()
                .sessionId(session.getId())
                .success(isSuccess)
                .finalAnswer(finalAnswer)
                .steps(steps)
                .maxStepsReached(maxStepsReached)
                .error(errorMessage)
                .build();
    }

    private AgentSession getOrCreateSession(AgentRequest request) {
        if (request.getSessionId() != null) {
            return sessionService.getSession(request.getSessionId());
        }
        return sessionService.createSession("Agent Session");
    }

    private List<Message> buildMessages(AgentSession session, AgentRequest request) {
        List<Message> messages = new ArrayList<>();

        if (request.getSystemPrompt() != null) {
            messages.add(MessageFactory.createSystemMessage(request.getSystemPrompt()));
        }

        messages.addAll(session.getMessages());

        return messages;
    }

    private ChatModelRequest buildLlmRequest(List<Message> messages, AgentRequest request) {
        ChatModelRequest.Builder builder = ChatModelRequest.builder()
                .messages(messages);

        List<ToolDefinition> tools = toolService.getAllTools();
        if (!tools.isEmpty()) {
            List<String> toolNames = tools.stream()
                    .map(ToolDefinition::getName)
                    .collect(Collectors.toList());
            builder.tools(toolNames);
        }

        return builder.build();
    }
}
