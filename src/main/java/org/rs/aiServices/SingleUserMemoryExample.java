package org.rs.aiServices;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.memory.ChatMemoryAccess;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class SingleUserMemoryExample {

    // Simple assistant with memory
    interface Assistant {
        @SystemMessage("You are a helpful personal assistant. "
                + "Remember details about the user and "
                + "maintain conversation context. Always give short answers, in 1 sentence")
        String chat(@UserMessage String message);
    }

    // Create assistant that extends ChatMemoryAccess to inspect memory
    interface InspectableAssistant extends ChatMemoryAccess {
        String chat(@UserMessage String message);
    }

    static void main(String[] args) {
        // Create model
        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .timeout(Duration.of(5, ChronoUnit.MINUTES))
                .numCtx(4096)
                .temperature(0.7)
                .build();

        System.out.println("=== Example 1: Basic Conversation Memory ===");

        // Create assistant with memory (keeps last 10 messages)
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        // Conversation demonstrating memory
        String msg = "List primitives types in Java in one sentence.";
        System.out.println("User :" + msg);
        String response1 = assistant.chat(msg);
        System.out.println("Assistant: " + response1);

        String msg2 = "If I create a whole number less than 100, "
                + "which primitive data type should I use, no explanation needed.";
        System.out.println("\nUser: " + msg2);
        String response2 = assistant.chat(msg2);
        System.out.println("Assistant: " + response2);

        String msg3 = "Which ones is good for floating point calculations, no explanation needed.";
        System.out.println("\nUser: " + msg3);
        String response3 = assistant.chat(msg3);
        System.out.println("Assistant: " + response3);

        System.out.println("\n=== Example 2: Task-Oriented Memory ===");

        // Reset with new memory
        Assistant taskAssistant =
                AiServices.builder(Assistant.class)
                        .chatModel(model)
                        .chatMemory(MessageWindowChatMemory.withMaxMessages(5))
                        .build();

        System.out.println("User: I need to buy groceries");
        String task1 = taskAssistant.chat("I need to buy groceries");
        System.out.println("Assistant: " + task1);

        System.out.println("\nUser: Add milk to the list");
        String task2 = taskAssistant.chat("Add milk to the list");
        System.out.println("Assistant: " + task2);

        System.out.println("\nUser: Also add bread and eggs");
        String task3 = taskAssistant.chat("Also add bread and eggs");
        System.out.println("Assistant: " + task3);

        System.out.println("\nUser: What's on my grocery list?");
        String task4 = taskAssistant.chat("What's on my grocery list?");
        System.out.println("Assistant: " + task4);

        System.out.println("\n=== Example 3: Inspecting Memory ===");
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);
        InspectableAssistant inspectableAssistant =
                AiServices.builder(InspectableAssistant.class)
                        .chatModel(model)
                        .chatMemory(memory)
                        .build();

        inspectableAssistant.chat("Remember that my favorite color is blue");
        inspectableAssistant.chat("And I live in New York");

        // Access memory directly
        System.out.println("\nCurrent conversation messages:");
        List<ChatMessage> messages = memory.messages();
        for (ChatMessage message : messages) {
            System.out.println(message.type() + ": "
                    + (message instanceof TextContent tc ?
                    tc.text() : message.toString()));
        }

        // Clear memory if needed
        memory.clear();
        System.out.println("\nMemory cleared. Messages count: " + memory.messages().size());
    }

}
