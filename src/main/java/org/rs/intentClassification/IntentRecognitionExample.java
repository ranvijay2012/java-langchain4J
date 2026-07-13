package org.rs.intentClassification;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

public class IntentRecognitionExample {
    static void main() {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .numCtx(4096)
                .temperature(0.0)
                .build();

        String userMessage = "Where is my order?";

        String prompt = """
                You are an intent classification system.

                Classify the user message into ONE of the following intents:
                - GREETING
                - CHECK_ORDER
                - CANCEL_ORDER
                - REFUND_REQUEST
                - UNKNOWN

                Return ONLY the intent name. No explanation.

                User message: "%s"
                """.formatted(userMessage);

        String response = model.chat(prompt).trim();
        System.out.println("Detected intent: " + response);

        try {
            Intent intent = Intent.valueOf(response.toUpperCase());
            switch (intent) {
                case CHECK_ORDER:
                    System.out.println("start process: looking up order status...");
                    break;
                case GREETING:
                    System.out.println("start process: responding to greeting...");
                    break;
                case CANCEL_ORDER:
                    System.out.println("start process: responding to Cancel order...");
                    break;
                case REFUND_REQUEST:
                    System.out.println("start process: responding to refund request...");
                    break;
                case UNKNOWN:
                default:
                    System.out.println("start process: generic help flow...");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Process: handle unknown intent");
        }
    }
}

enum Intent {
    GREETING,
    CHECK_ORDER,
    CANCEL_ORDER,
    REFUND_REQUEST,
    UNKNOWN
}
