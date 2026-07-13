package org.rs.chatmodel;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.util.ArrayList;
import java.util.List;

public class MultiTurnConversation {
    static void main() {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("moondream:latest")
                .numCtx(4096)
                .build();

        List<ChatMessage> conversation = new ArrayList<>();

        // ---- Stage 1: Initial requirement ----
        UserMessage stage1 = UserMessage.from("I want to build a REST API for managing orders.");
        conversation.add(stage1);

        AiMessage response1 = model.chat(conversation).aiMessage();
        conversation.add(response1);

        System.out.println("Stage 1 Response:");
        System.out.println(response1.text());
        System.out.println();

        // ---- Stage 2: Refinement based on previous turn ----
        UserMessage stage2 = UserMessage.from("The API should support creating and cancelling orders.");
        conversation.add(stage2);

        AiMessage response2 = model.chat(conversation).aiMessage();
        conversation.add(response2);

        System.out.println("Stage 2 Response:");
        System.out.println(response2.text());
        System.out.println();

        // ---- Stage 3: Concrete output using accumulated context ----
        UserMessage stage3 = UserMessage.from("Give me a simple list of REST endpoints for this API.");
        conversation.add(stage3);

        AiMessage response3 = model.chat(conversation).aiMessage();
        conversation.add(response3);

        System.out.println("Stage 3 Response:");
        System.out.println(response3.text());
    }
}
