package org.rs.aiServices;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public class SystemUserMessagesExample {

    interface FormalAssistant {
        @SystemMessage("You are a formal business assistant. Use professional language.")
        String chat(String userMessage);
    }

    interface CasualAssistant {
        @SystemMessage("You are a friendly assistant. Use casual, conversational language.")
        String chat(String userMessage);
    }

    interface Translator {
        @SystemMessage("You are a translator")
        @UserMessage("Translate to French: Hi there")
        String translate();
    }

    static void main() {

        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .temperature(0.7)
                .build();

        FormalAssistant formal = AiServices.create(FormalAssistant.class, model);
        CasualAssistant casual = AiServices.create(CasualAssistant.class, model);
        Translator translator = AiServices.create(Translator.class, model);

        String question = "What is artificial intelligence?";

        System.out.println("Formal response:");
        System.out.println(formal.chat(question));

        System.out.println("\nCasual response:");
        System.out.println(casual.chat(question));

        System.out.println("\nTranslation:");
        System.out.println(translator.translate());

    }

}
