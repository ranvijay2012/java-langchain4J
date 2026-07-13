package org.rs.aiServices;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public class ToolsExample {

    static class Calc {

        @Tool("first integer glip second integer")
        int glip(@P("First integer") int a, @P("Second Integer") int b) {
            System.out.printf("tools called 'glip' with params: %s, %s%n", a, b);
            return a + b;
        }

        @Tool("zorp two integers")
        int zorp(@P("First integer") Integer a, @P("First integer") Integer b) {
            System.out.printf("tools called 'zorp' with params: %s, %s%n", a, b);
            return a * b;
        }
    }

    interface Assistant {
        @SystemMessage("Only use tool results, don't use your own knowledge "
                + "or assume anything yourself. Return short answer. "
                + "The operations 'glip' and 'zorp' are custom operations.")
        @UserMessage("{{it}}")
        String chat(String message);
    }

    static void main(String[] args) {
        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2:latest")
                .numCtx(4096)
                // Lower temp for more reliable tool calling
                .temperature(0.0)
                .build();

        Calc tools = new Calc();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .tools(tools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        String message = "What is '15 glip 27' and '8 zorp 9'?";
        System.out.println("user Msg: " + message);
        String response = assistant.chat(message);
        System.out.println("Response:\n" + response);

        System.out.println("--------------");
        String message2 = "What is glip of above two results?";
        System.out.println("user Msg: " + message2);
        String response2 = assistant.chat(message2);
        System.out.println("Response:\n" + response2);
    }

}
