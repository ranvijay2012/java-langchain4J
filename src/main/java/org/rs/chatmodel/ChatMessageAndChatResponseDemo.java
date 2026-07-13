package org.rs.chatmodel;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.FinishReason;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatMessageAndChatResponseDemo {
    static void main() {
        ChatModel model = getChatModel();

        VarargsChatExample(model);
//        ListChatExample(model);
//        MessageBuilderExample(model);
//        ChatResponseExample(model);
//        MultiTurnConversation(model);

    }

    private static void MultiTurnConversation(ChatModel model) {
        List<ChatMessage> conversation = new ArrayList<>();

        // ---- Stage 1: Initial requirement ----
        UserMessage stage1 = UserMessage.from(
                "I want to build a REST API for managing orders."
        );
        conversation.add(stage1);

        AiMessage response1 = model.chat(conversation).aiMessage();
        conversation.add(response1);

        System.out.println("Stage 1 Response:");
        System.out.println(response1.text());
        System.out.println();

        // ---- Stage 2: Refinement based on previous turn ----
        UserMessage stage2 = UserMessage.from(
                "The API should support creating and cancelling orders."
        );
        conversation.add(stage2);

        AiMessage response2 = model.chat(conversation).aiMessage();
        conversation.add(response2);

        System.out.println("Stage 2 Response:");
        System.out.println(response2.text());
        System.out.println();

        // ---- Stage 3: Concrete output using accumulated context ----
        UserMessage stage3 = UserMessage.from(
                "Give me a simple list of REST endpoints for this API."
        );
        conversation.add(stage3);

        AiMessage response3 = model.chat(conversation).aiMessage();
        conversation.add(response3);

        System.out.println("Stage 3 Response:");
        System.out.println(response3.text());
    }

    private static void ChatResponseExample(ChatModel model) {
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


    private static void MessageBuilderExample(ChatModel model) {
        // Building UserMessage with Builder pattern for advanced control
        UserMessage userMessage =
                UserMessage.builder()
                        .attributes(Map.of("session_id", "123", "user_type", "premium"))
                        .name("User_123")
                        .contents(List.of(
                                TextContent.from("Describe this image."),
                                //loading langChain4j logo image
                                ImageContent.from("https://www.logicbig.com/tutorials/ai-tutorials"
                                        + "/lang-chain-4j/images/langChain4j.png"))
                        )
                        .build();

        ChatResponse response = model.chat(userMessage);

        System.out.println("Response: " + response.aiMessage().text());
    }

    private static void ListChatExample(ChatModel model) {
        // Using list method for dynamic conversation building
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

    private static void VarargsChatExample(ChatModel model) {
        // Using varargs method for concise one-off requests
        ChatResponse response = model.chat(
                SystemMessage.from("You are a poetic assistant."),
                UserMessage.from("Write a five-word poem about Java.")
        );

        String poem = response.aiMessage().text();
        System.out.println("Poem: " + poem);

        // Another example with different instructions
        ChatResponse response2 = model.chat(
                SystemMessage.from("You are a helpful coding assistant."),
                UserMessage.from("Explain recursion in one sentence.")
        );

        System.out.println("\nRecursion explanation: " +
                response2.aiMessage().text());
    }

    private static ChatModel getChatModel() {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("moondream:latest")
                .numCtx(4096)
                .temperature(0.7)
                .build();
        return model;
    }
}
