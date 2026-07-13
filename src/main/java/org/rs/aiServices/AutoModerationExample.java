package org.rs.aiServices;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.model.moderation.Moderation;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.ModerationException;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import java.util.List;

public class AutoModerationExample {

    interface Assistant {
        @SystemMessage("You are a helpful assistant.")
        String chat(@UserMessage String message);
    }

    static class SimpleModerationModel implements ModerationModel {
        @Override
        public Response<Moderation> moderate(String text) {
            String lowerText = text.toLowerCase();

            if (lowerText.contains("hate") || lowerText.contains("violence") ||
                    lowerText.contains("explicit") || lowerText.contains("stupid")) {
                return Response.from(Moderation.flagged(text));
            }
            return Response.from(Moderation.notFlagged());
        }

        @Override
        public Response<Moderation> moderate(List<ChatMessage> messages) {
            StringBuilder combined = new StringBuilder();
            for (ChatMessage msg : messages) {
                if (msg instanceof TextContent) {
                    combined.append(((TextContent) msg).text()).append(" ");
                }
            }
            return moderate(combined.toString().trim());
        }
    }

    static void main(String[] args) {
        var chatModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .build();

        var moderationModel = new SimpleModerationModel();

        // With moderation
        Assistant moderatedAssistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .moderationModel(moderationModel)
                .build();

        // Without moderation
        Assistant unmoderatedAssistant = AiServices.create(Assistant.class, chatModel);

        System.out.println("=== With Auto-Moderation ===");
        try {
            String response = moderatedAssistant.chat("This is hate speech");
            System.out.println("Response: " + response);
        } catch (ModerationException e) {
            System.out.println("Blocked: " + e.moderation().flaggedText());
        }

        System.out.println("\n=== Without Moderation ===");
        String response = unmoderatedAssistant.chat("This is hate speech");
        System.out.println("Response: " + response);
    }
}
