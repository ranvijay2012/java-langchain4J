package org.rs.StreamingChatModel;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.PartialResponse;
import dev.langchain4j.model.chat.response.PartialResponseContext;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;

import java.util.concurrent.CountDownLatch;

public class StreamingCancelExample {
    static void main() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        StreamingChatModel model = OllamaStreamingChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .numCtx(4096)
                .temperature(0.7)
                .build();

        System.out.println("Streaming started...");

        model.chat("What are the prime numbers between 1 to 13. Ony return numbers.",
                new StreamingChatResponseHandler() {

                    @Override
                    public void onPartialResponse(PartialResponse partialResponse,
                                                  PartialResponseContext context) {

                        String text = partialResponse.text();
                        System.out.print(text);
                        if (text.contains("7")) {
                            System.out.println("\n[Condition met. Cancelling...]");
                            context.streamingHandle().cancel();
                            latch.countDown();
                        }
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse response) {
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable error) {
                        System.out.println("\nStream stopped.");
                        latch.countDown();
                    }
                });

        latch.await();
    }
}
