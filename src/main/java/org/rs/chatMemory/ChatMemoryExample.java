package org.rs.chatMemory;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

public class ChatMemoryExample {
    static void main() {
        // 1. Create model
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .numCtx(4096)
                .temperature(0.0)
                .build();

        // 2. Create chat memory (keeps last 10 messages)
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(20);

        memory.add(SystemMessage.from("You are a calculator "
                + "please return only "
                + "the result of the calculation," +
                "no wordings"));
        // ---- Turn 1 ----
        UserMessage user1 = UserMessage.from("Given x=3 and y=4, "
                + "what is x+y?");
        memory.add(user1);

        AiMessage ai1 = model.chat(memory.messages()).aiMessage();
        printConversation(user1, ai1);
        memory.add(ai1);

        // ---- Turn 2 ----
        UserMessage user2 = UserMessage.from("What is x*y?");
        memory.add(user2);

        AiMessage ai2 = model.chat(memory.messages()).aiMessage();
        memory.add(ai2);
        printConversation(user2, ai2);

        UserMessage user3 = UserMessage.from("what is the sum of "
                + "all previous calculations?");
        memory.add(user3);

        AiMessage ai3 = model.chat(memory.messages()).aiMessage();
        memory.add(ai3);
        printConversation(user3, ai3);
    }

    private static void printConversation(UserMessage userMessage, AiMessage aiResponse) {
        System.out.println("-------");
        System.out.println("User message: " + userMessage.singleText());
        System.out.println("AI response: " + aiResponse.text());
    }
}
