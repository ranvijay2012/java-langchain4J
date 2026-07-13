package org.rs.aiServices;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class PromptTemplatesExample {

    interface EmailGenerator {
        @UserMessage("Write a professional email to {{recipient}} "
                + "about {{topic}}. Keep it under 100 words.")
        String generateEmail(@V("recipient") String recipientName,
                             @V("topic") String emailTopic);
    }

    interface ProductDescriber {
        @UserMessage("Create a marketing description for a {{product}} "
                + "that costs ${{price}}. Highlight its {{feature}}."
                + " Keep it less than 100 words")
        String describeProduct(@V("product") String product,
                               @V("price") double price,
                               @V("feature") String feature);
    }

    interface TemplateWithSingleParam {
        @UserMessage("Summarize this text in one sentence: {{it}}")
        String summarize(String text);
    }

    static void main() {
        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .temperature(0.7)
                .numCtx(4096)
                .timeout(Duration.of(5, ChronoUnit.MINUTES))
                .build();

        EmailGenerator emailGen = AiServices.create(EmailGenerator.class, model);
        ProductDescriber productDesc = AiServices.create(ProductDescriber.class, model);
        TemplateWithSingleParam summarizer = AiServices.create(TemplateWithSingleParam.class, model);

        System.out.println("Generated Email:");
        String email = emailGen.generateEmail("John Smith", "project deadline");
        System.out.println(email);

        System.out.println("\nProduct Description:");
        String description = productDesc.describeProduct("smartphone",
                799.99, "camera quality");
        System.out.println(description);

        System.out.println("\nText Summary:");
        String summary = summarizer.summarize(
                "Artificial Intelligence is transforming industries "
                        + "by automating complex tasks and providing "
                        + "insights from large datasets.");
        System.out.println(summary);

    }

}
