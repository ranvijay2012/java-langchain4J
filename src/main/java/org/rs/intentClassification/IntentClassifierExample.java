package org.rs.intentClassification;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;

public class IntentClassifierExample {
    static void main() {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .numCtx(4096)
                .temperature(0.5)
                .build();

        SystemMessage systemInstruction = SystemMessage.from(
                "Identify the intent and extract entities from the user's request.\n"
                        + "Intents: [CHECK_ORDER, CANCEL_ORDER, REFUND_REQUEST, UNKNOWN]\n"
                        + "Entities to find: [order_id, item_name, reason]\n"
                        + "Return the result ONLY as a JSON object.");

        UserMessage userMessage = UserMessage.from(
                "I need a refund for the fries in my order #9921 because they are soggy");

        ChatResponse response = model.chat(systemInstruction, userMessage);

        AiMessage aiMessage = response.aiMessage();
        System.out.println(aiMessage.text());
    }
}
