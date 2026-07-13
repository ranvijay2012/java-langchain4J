package org.rs.chatMemory;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

public class MessageWindowChatMemoryDemo {
    static void main() {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .numCtx(4096)
                .temperature(0.2)
                .modelName("phi3:mini-128k")
                .build();

        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(2);

        // Add three messages to memory
        memory.add(UserMessage.from("My name is Alice."));
        memory.add(UserMessage.from("I live in New York."));
        memory.add(UserMessage.from("What is my name and where do I live?"));

        // Send to LLM and get response
        String response = model.chat(memory.messages()).aiMessage().text();
        System.out.println("\nLLM Response: " + response);

    }
}
