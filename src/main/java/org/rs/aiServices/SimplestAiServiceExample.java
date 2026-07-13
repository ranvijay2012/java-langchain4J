package org.rs.aiServices;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

public class SimplestAiServiceExample {
    static void main() {
        // Create Ollama model with Phi3:mini
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .temperature(0.7)
                .build();

        // Create AI Service proxy
        Assistant assistant = AiServices.create(Assistant.class, model);

        // Use the AI Service
        String response = assistant.chat("Hello, how are you?");
        System.out.println("Response: " + response);

        // Another interaction
        String joke = assistant.chat("Tell me a short joke");
        System.out.println("\nJoke: " + joke);
    }
}
