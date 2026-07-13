package org.rs.chatmodel;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;

public class ChatRequestExample {
    static void main(String[] args) {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .numCtx(4096)
                .temperature(0.0)
                .build();

        ChatRequest chatRequest = ChatRequest.builder()
                .messages(UserMessage.from("What is Java?"))
                .temperature(0.7)
                .maxOutputTokens(50)
                .build();

        ChatResponse response = model.chat(chatRequest);
        System.out.println("Response: " + response.aiMessage().text());
    }
}
