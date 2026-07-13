package org.rs.chatmodel;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;



//Logging is essential for debugging and monitoring AI applications. LangChain4j provides built-in internal logging
//capabilities to track requests sent to AI models and responses received from them. This internal logging helps
//developers understand what's happening during API calls and troubleshoot issues effectively.

public class BasicLoggingExample {
    static void main() {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .logRequests(true)      //logging request
                .logResponses(true)     //logging response
                .build();

        String response = model.chat("Explain AI in one sentence");
        //todo process response
    }
}
