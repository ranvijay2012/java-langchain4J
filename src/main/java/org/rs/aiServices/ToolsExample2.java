package org.rs.aiServices;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

import java.time.Instant;

public class ToolsExample2 {
    static class SystemTools {

        @Tool("Returns the current system time in milliseconds")
        public long systemMillis() {
            System.out.println("tool called systemMillis");
            long epoch = System.currentTimeMillis();
            System.out.println("tool systemMillis return value: "+epoch);
            return epoch;
        }

        @Tool("Converts epoch milliseconds in long to a UTC timestamp")
        public String utcFormat(long millis) {
            System.out.println("tool called utcFormat param: "+millis);
            String string = Instant.ofEpochMilli(millis).toString();
            System.out.println("tool utcFormat return value: "+string);
            return string;
        }
    }

    interface TimeAgent {

        @SystemMessage("""
                You are a helpful assistant with access to tools.
                You may call multiple tools in sequence.
                Use tool outputs as inputs to subsequent tools when needed.
                Return short answers.
                """)
        String chat(String userMessage);
    }

    static void main(String[] args) {

        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2:latest")
                .temperature(0.0)
                .numCtx(4096)
                .build();

        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        TimeAgent agent = AiServices.builder(TimeAgent.class)
                .chatModel(model)
                .tools(new SystemTools())
                .chatMemory(memory)
                .build();

        String response1 = agent.chat("What is the current system time?");
        System.out.println("response1: '"+response1+"'");

        String response2 = agent.chat(
                "Now convert the system time into a human-readable UTC format."
        );
        System.out.println("response2 "+response2);

    }
}
