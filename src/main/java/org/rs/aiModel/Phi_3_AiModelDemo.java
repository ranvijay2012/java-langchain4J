package org.rs.aiModel;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;


public class Phi_3_AiModelDemo {

    static void main() {
        System.out.println("--------");

        // Create the Ollama model
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .temperature(0.7)
                .numCtx(4096)
                .build();

        // Simple chat
        String response = model.chat("Hello");
        System.out.println("Response: " + response);

         response = model.chat("My Name is Ranvijay Singh");
        System.out.println("Response: " + response);

        // Another example
        String javaCode = model.chat("Write a Java program to print \"Hello World\" on the console.");
        System.out.println(javaCode);
    }
}
