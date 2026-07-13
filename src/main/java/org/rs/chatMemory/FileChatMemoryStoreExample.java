package org.rs.chatMemory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileChatMemoryStoreExample {
    static void main() throws IOException {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .numCtx(4096)
                .temperature(0.7)
                .build();

        Path tempFile = Files.createTempFile("chat-memory-", ".txt");

        ChatMemoryStore store = new FileChatMemoryStore(tempFile);

        ChatMemory memory = MessageWindowChatMemory.builder()
                .id("file-session")
                .maxMessages(5)
                .chatMemoryStore(store)
                .build();

        memory.add(SystemMessage.from("You are a poet."));

        memory.add(UserMessage.from("Write a short poem on "
                + "Java programming language."));

        ChatResponse response = model.chat(memory.messages());
        memory.add(response.aiMessage());

        memory.add(UserMessage.from("Give a name to the poem."));

        ChatResponse response2 = model.chat(memory.messages());
        memory.add(response2.aiMessage());

        System.out.println("-- file json content --");
        Files.lines(tempFile).forEach(System.out::println);

    }
}




class FileChatMemoryStore implements ChatMemoryStore {
    private final Path file;
    private final ObjectMapper mapper = new ObjectMapper();

    FileChatMemoryStore(Path file) {
        this.file = file;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        try {
            if (!Files.exists(file) || Files.size(file) == 0) {
                return new ArrayList<>();
            }

            List<JsonEntry> entries =
                    mapper.readValue(file.toFile(),
                            new TypeReference<>() {});

            List<ChatMessage> messages = new ArrayList<>();

            for (JsonEntry entry : entries) {
                switch (entry.type) {
                    case "userMessage" ->
                            messages.add(UserMessage.from(entry.message));
                    case "systemMessage" ->
                            messages.add(SystemMessage.from(entry.message));
                    case "aiMessage" ->
                            messages.add(AiMessage.from(entry.message));
                }
            }

            return messages;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> newMessages) {
        try {
            List<JsonEntry> entries = new ArrayList<>();

            // append new entries
            for (ChatMessage message : newMessages) {
                entries.add(toEntry(message));
            }

            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file.toFile(), entries);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteMessages(Object memoryId) {
        try {
            Files.deleteIfExists(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JsonEntry toEntry(ChatMessage message) {
        if (message instanceof UserMessage) {
            return new JsonEntry("userMessage", ((UserMessage) message).singleText());
        }
        if (message instanceof SystemMessage) {
            return new JsonEntry("systemMessage", ((SystemMessage) message).text());
        }
        if (message instanceof AiMessage) {
            return new JsonEntry("aiMessage", ((AiMessage) message).text());
        }
        throw new IllegalArgumentException("Unsupported message type");
    }

    static class JsonEntry {
        public String type;
        public String message;

        // required by Jackson
        public JsonEntry() {
        }

        JsonEntry(String type, String message) {
            this.type = type;
            this.message = message;
        }
    }
}
