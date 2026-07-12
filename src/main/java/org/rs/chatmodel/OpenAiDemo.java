package org.rs.chatmodel;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class OpenAiDemo {
     static void main (String[] args) {
        ChatModel model = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .modelName("gpt-4o-mini")
                .apiKey("demo")
                .build();

        String response = model.chat("Hi there!");
        System.out.println(response);
    }
}
