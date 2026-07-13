package org.rs.chatmodel;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.util.List;
import java.util.Map;

public class MessageBuilderExample {
    static void main() {

        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("moondream:latest")
                .numCtx(4096)
                .temperature(0.7)
                .build();

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
}
