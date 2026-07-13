package org.rs.StreamingChatModel;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;

import java.util.concurrent.CountDownLatch;

public class StreamingChatExample {
    static void main() throws InterruptedException {
        CountDownLatch done = new CountDownLatch(1);
        StreamingChatModel model = OllamaStreamingChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .numCtx(4096)
                .temperature(0.7)
                .build();

        System.out.println("Starting stream...\n");

        model.chat("Write a very short poem about Java concurrency.",
                new StreamingChatResponseHandler() {
                    @Override
                    public void onPartialResponse(String token) {
                        // This is called every time a new token is generated
                        System.out.print(token);
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse response) {
                        System.out.println("\n\nDone!");
                        done.countDown();
                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                        done.countDown();
                    }
                });

        // Keeping the main thread alive for the async response
        done.await();
    }
}
