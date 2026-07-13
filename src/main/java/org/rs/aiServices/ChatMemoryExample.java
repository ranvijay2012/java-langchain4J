package org.rs.aiServices;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class ChatMemoryExample {

    @SystemMessage("Always reply in English.")
    interface Assistant {
        String chat(@MemoryId String userId, @UserMessage String message);
    }

    static void main() {
        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .temperature(0.7)
                .numCtx(4096)
                .timeout(Duration.of(5, ChronoUnit.MINUTES))
                .build();

        // Create AI Service with per-user memory (max 5 messages per user)
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemoryProvider(userId -> MessageWindowChatMemory.withMaxMessages(5))
                .build();

        // User 1 conversation
        System.out.println("=== User 1 Conversation ===");
        String response1 = assistant.chat("user123", "Hello, my name is Alice.");
        System.out.println("Response 1: " + response1);

        String response2 = assistant.chat("user123", "What's my name?");
        System.out.println("Response 2: " + response2);

        // User 2 conversation (separate memory)
        System.out.println("\n=== User 2 Conversation ===");
        String response3 = assistant.chat("user456", "Hi, I'm Bob");
        System.out.println("Response 3: " + response3);

        String response4 = assistant.chat("user456", "Who am I?");
        System.out.println("Response 4: " + response4);

        // User 1 continues their conversation
        System.out.println("\n=== User 1 Continues ===");
        String response5 = assistant.chat("user123", "What programming languages do you know?");
        System.out.println("Response 5: " + response5);

        // Demonstrate memory doesn't mix between users
        System.out.println("\n=== Testing Memory Isolation ===");
        String response6 = assistant.chat("user123", "What's my name again?");
        System.out.println("User 123 (Alice): " + response6);

        String response7 = assistant.chat("user456", "What's my name?");
        System.out.println("User 456 (Bob): " + response7);

    }

}
