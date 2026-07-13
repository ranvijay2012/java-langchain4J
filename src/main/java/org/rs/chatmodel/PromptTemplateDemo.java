package org.rs.chatmodel;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;

public class PromptTemplateDemo {
    static void main() {
        // Create Ollama model with Phi3
        ChatModel model = getChatModel();

//        RestaurantReviewAnalyzer(model);
//        ProductRecommendationGenerator(model);
        NamedClockTemplateExample(model);
    }

    private static void NamedClockTemplateExample(ChatModel model) {
        // 2. Define a template that uses automatic date variables
        // The name "daily-task-assistant" helps you identify this template in logs
        String template = "Current Date: {{current_date}}. \n" +
                "Task: Write a short to-do list for a {{user_role}}.";

        String templateName = "daily-task-assistant";

        // 3. Use the specific method: from(template, name, clock)
        // We'll use a fixed clock here to demonstrate how to control 'current_date'
        Clock fixedClock = Clock.fixed(Instant.parse("2026-01-18T10:00:00Z"), ZoneId.of("UTC"));

        PromptTemplate promptTemplate = PromptTemplate.from(template, templateName, fixedClock);

        // 4. Apply the template
        // Notice we only provide 'user_role'. 'current_date' is handled by the Clock.
        Prompt prompt = promptTemplate.apply(Map.of("user_role", "Software Engineer"));

        // 5. Execute with Phi-3
        String response = model.chat(prompt.text());

        System.out.println("-- Prompt (Named: " + templateName + ") --");
        System.out.println(prompt.text());
        System.out.println("\n-- Phi-3 Response --");
        System.out.println(response);

    }


    private static void ProductRecommendationGenerator(ChatModel model) {
        // Define template with multiple variables
        String template = "Recommend {{number}} {{category}} products " +
                "within a budget of {{budget}}. " +
                "The user prefers: {{preferences}}.";

        Prompt prompt = PromptTemplate.from(template)
                .apply(Map.of("number", "3",
                        "category", "smartphone",
                        "budget", "$500-$700",
                        "preferences", "good camera, "
                                + "long battery life, and "
                                + "5G support"));


        ChatResponse response = model.chat(prompt.toUserMessage());

        System.out.println("=== Product Recommendations ===");
        System.out.println("Template: " + template);
        System.out.println("\nGenerated Prompt: " + prompt.text());
        System.out.println("\nRecommendations:\n" + response.aiMessage().text());

    }

    private static void RestaurantReviewAnalyzer(ChatModel model) {
        // Define prompt template for review analysis
        String template = "The time is {{current_date_time}}.\n"
                + "Analyze the following restaurant review and provide:\n" +
                "1. Sentiment (positive/negative/neutral)\n" +
                "2. Key points mentioned\n" +
                "3. Suggestions for improvement\n\n" +
                "Review: {{it}}";

        PromptTemplate promptTemplate = PromptTemplate.from(template);

        Prompt prompt = promptTemplate.apply(
                "The food was excellent but "
                        + "the service was very slow. " +
                        "We waited 45 minutes for our main course. " +
                        "The dessert, however, was amazing.");

        // Generate response
        UserMessage userMessage = prompt.toUserMessage();
        ChatResponse response = model.chat(userMessage);

        System.out.println("=== Restaurant Review Analysis ===");
        System.out.println("Prompt:\n" + prompt.text());
        System.out.println("\nAnalysis:\n" + response.aiMessage().text());
    }

    private static ChatModel getChatModel() {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .numCtx(4096)
                .temperature(0.7)
                .build();
        return model;
    }
}
