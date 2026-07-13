package org.rs.Agent;

import dev.langchain4j.agent.tool.Tool;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentExample {

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
        messages.add(SystemMessage.from("You are a helpful assistant with access to tools. " +
                "You may call tools when needed."));
        messages.add(UserMessage.from("What is the current system time in milliseconds?"));

        ChatRequest request = ChatRequest.builder()
                .messages(messages)
                .toolSpecifications(specs)
                .build();

        ChatResponse response = model.chat(request);
        AiMessage aiMessage = response.aiMessage();

        if (aiMessage.hasToolExecutionRequests()) {
            messages.add(aiMessage);
            for (ToolExecutionRequest r : aiMessage.toolExecutionRequests()) {
                String result = executors.get(r.name()).execute(r, r.id());
                messages.add(ToolExecutionResultMessage.from(r, result));
            }
        }

        //final message
        messages.add(model.chat(messages).aiMessage());

        for (ChatMessage chatMessage : messages) {
            System.out.println("-- %s --".formatted(chatMessage.type()));
            switch (chatMessage.type()) {
                case SYSTEM -> System.out.println(((SystemMessage) chatMessage).text());
                case USER -> System.out.println(((UserMessage) chatMessage).singleText());
                case TOOL_EXECUTION_RESULT -> System.out.println(
                        ((ToolExecutionResultMessage) chatMessage)
                                .text());
                case AI -> {
                    AiMessage aiMsg = (AiMessage) chatMessage;
                    if (aiMsg.text() != null) {
                        System.out.println(aiMsg.text());
                    }
                    if (aiMsg.hasToolExecutionRequests()) {
                        System.out.println(aiMsg.toolExecutionRequests());
                    }
                }
            }
        }

    }
}

class SystemTools {

    @Tool("Returns the current system time in milliseconds")
    public long systemMillis() {
        return System.currentTimeMillis();
    }

    @Tool("Converts epoch milliseconds to a UTC timestamp")
    public String formatMillis(long millis) {
        return Instant.ofEpochMilli(millis).toString();
    }
}

