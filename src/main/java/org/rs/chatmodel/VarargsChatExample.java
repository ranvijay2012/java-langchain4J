package org.rs.chatmodel;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;

public class VarargsChatExample {
    static void main() {
        ChatModel chatModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("moondream:latest")
                .numCtx(4096)
                .temperature(0.7)
                .build();

        ChatResponse chatResponse = chatModel.chat(
                SystemMessage.from("You are a poetic assistant."),
                UserMessage.from("Write a five-word poem about Java.")
        );

        String poem = chatResponse.aiMessage().text();
        System.out.println("Poem: " + poem);

        // Another example with different instructions
        ChatResponse response2 = chatModel.chat(
                SystemMessage.from("You are a helpful coding assistant."),
                UserMessage.from("Explain recursion in one sentence.")
        );

        System.out.println("\nRecursion explanation: " +
                response2.aiMessage().text());
    }
}
