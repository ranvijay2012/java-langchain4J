package org.rs.chatmodel;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.FinishReason;

public class ChatResponseExample {
    static void main() {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("moondream:latest")
                .numCtx(4096)
                .temperature(0.7)
                .build();

        UserMessage userMessage = UserMessage.from("Explain object-oriented programming.");
        ChatResponse response = model.chat(userMessage);

        // Extracting text content
        String text = response.aiMessage().text();
        System.out.println("Response: " + text);

        // Accessing metadata
        int inputTokens = response.tokenUsage().inputTokenCount();
        int outputTokens = response.tokenUsage().outputTokenCount();
        int totalTokens = response.tokenUsage().totalTokenCount();
        FinishReason finishReason = response.finishReason();

        System.out.println("\nToken Usage:");
        System.out.println("Input tokens: " + inputTokens);
        System.out.println("Output tokens: " + outputTokens);
        System.out.println("Total tokens: " + totalTokens);
        System.out.println("Finish reason: " + finishReason);
    }
}
