package org.rs.chatmodel;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.util.ArrayList;
import java.util.List;

public class ListChatExample {
    static void main() {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("moondream:latest")
                .numCtx(4096)
                .temperature(0.7)
                .build();

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(SystemMessage.from("You are a helpful support agent "
                + "who can take orders but cannot cancel order."));
        messages.add(UserMessage.from("I need help with my order."));

        ChatResponse response = model.chat(messages);
        System.out.println("Support agent: " + response.aiMessage().text());
        // Add follow-up question
        messages.add(response.aiMessage());
        messages.add(UserMessage.from("Can I cancel my order?"));

        ChatResponse followUp = model.chat(messages);
        System.out.println("\nFollow-up response: " + followUp.aiMessage().text());

    }
}
