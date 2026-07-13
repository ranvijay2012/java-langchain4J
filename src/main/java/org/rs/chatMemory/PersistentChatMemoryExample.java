package org.rs.chatMemory;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;

public class PersistentChatMemoryExample {
    static void main() {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .numCtx(4096)
                .temperature(0.7)
                .build();

        InMemoryChatMemoryStore store = new InMemoryChatMemoryStore();
        ChatMemory memory = MessageWindowChatMemory.builder()
                .id("user-1")
                .maxMessages(5)
                .chatMemoryStore(store)
                .build();

        memory.add(UserMessage.from("Hi there!"));

        AiMessage aiMessage = model.chat(memory.messages()).aiMessage();

        memory.add(aiMessage);

        System.out.println("-- retrieving messages from the store --");
        for (ChatMessage message : store.getMessages(memory.id())) {
            System.out.println(message);
        }
    }
}
