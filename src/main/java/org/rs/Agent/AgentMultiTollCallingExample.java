package org.rs.Agent;


import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import dev.langchain4j.service.tool.ToolExecutor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentMultiTollCallingExample {

    private static final Map<String, ToolExecutor> executors = new HashMap<>();
    private static final List<ToolSpecification> specs = new ArrayList<>();

    static {
        SystemTools tools = new SystemTools();
        for (Method method : SystemTools.class.getDeclaredMethods()) {
            ToolSpecification spec = ToolSpecifications.toolSpecificationFrom(method);
            specs.add(spec);
            executors.put(spec.name(), new DefaultToolExecutor(tools, method));
        }
    }

    static void main() {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2:latest")
                .temperature(0.0)
                .numCtx(4096)
                .build();

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(SystemMessage.from(
                "You are a helpful assistant with access to tools. " +
                        "You may call multiple tools in sequence. " +
                        "Use tool outputs as inputs to subsequent tools when needed."
        ));

        sendUserMessageAndHandleToolCall(model, "What is the current system time?", messages);
        sendUserMessageAndHandleToolCall(model, "Now convert the system time into a human-readable UTC format.", messages);

        //final message
        messages.add(model.chat(messages).aiMessage());

        for (ChatMessage chatMessage : messages) {
            System.out.println("-- %s --".formatted(chatMessage.type()));
            switch (chatMessage.type()) {
                case SYSTEM -> System.out.println(((SystemMessage) chatMessage).text());
                case USER -> System.out.println(((UserMessage) chatMessage).singleText());
                case TOOL_EXECUTION_RESULT -> System.out.println(((ToolExecutionResultMessage) chatMessage).text());
                case AI -> {
                    AiMessage aiMessage = (AiMessage) chatMessage;
                    if (aiMessage.text() != null) {
                        System.out.println(aiMessage.text());
                    }
                    if (aiMessage.hasToolExecutionRequests()) {
                        System.out.println(aiMessage.toolExecutionRequests());
                    }
                }
            }
        }
    }

    private static void sendUserMessageAndHandleToolCall(ChatModel chatModel,
                                                         String myMessage,
                                                         List<ChatMessage> messages) {
        messages.add(UserMessage.from(myMessage));

        ChatRequest request = ChatRequest.builder()
                .messages(messages)
                .toolSpecifications(specs)
                .build();

        ChatResponse response = chatModel.chat(request);
        AiMessage aiMessage = response.aiMessage();
        messages.add(aiMessage);
        if (aiMessage.hasToolExecutionRequests()) {
            for (ToolExecutionRequest r : aiMessage.toolExecutionRequests()) {
                String result = executors.get(r.name()).execute(r, r.id());
                messages.add(ToolExecutionResultMessage.from(r, result));
            }
        }
    }

}

