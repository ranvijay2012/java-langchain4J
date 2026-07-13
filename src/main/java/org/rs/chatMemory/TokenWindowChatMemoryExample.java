package org.rs.chatMemory;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;

public class TokenWindowChatMemoryExample {
    static void main() {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .numCtx(4096)
                .temperature(0.7)
                .build();

        ChatMemory memory = TokenWindowChatMemory.withMaxTokens(50, new MyCustomTokenEstimator());

        memory.add(UserMessage.from("My favorite color is Aquamarine. Remember this."));

        // This message is long enough (having a lot to tokens)
        // to force the eviction of the first message.
        memory.add(UserMessage.from("I want to talk about space exploration. " +
                "The James Webb Space Telescope is amazing because it uses infrared " +
                "to see through cosmic dust clouds and find early stars."));

        UserMessage finalQuestion = UserMessage.from("What is my favorite color?");
        memory.add(finalQuestion);

        ChatResponse response1 = model.chat(memory.messages());
        AiMessage aiMessage = response1.aiMessage();
        String response = aiMessage.text();
        System.out.println("LLM Response: " + response);

    }
}


class MyCustomTokenEstimator implements TokenCountEstimator {

    @Override
    public int estimateTokenCountInText(String text) {
        if (text == null) return 0;
        // Approximation: 4 characters per token
        return (int) Math.ceil(text.length() / 4.0);
    }

    @Override
    public int estimateTokenCountInMessage(ChatMessage message) {
        return estimateTokenCountInText(((UserMessage) message).singleText());
    }

    @Override
    public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
        int total = 0;
        for (ChatMessage message : messages) {
            total += estimateTokenCountInMessage(message);
        }
        return total;
    }
}