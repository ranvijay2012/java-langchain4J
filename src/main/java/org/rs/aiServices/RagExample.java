package org.rs.aiServices;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RagExample {
    private static EmbeddingModel embeddingModel;
    private static EmbeddingStore<TextSegment> embeddingStore;

    //creating EmbeddingStore/EmbeddingModel with custom documents
    static {
        embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("all-minilm")
                .build();

        embeddingStore = new InMemoryEmbeddingStore<>();
        storeDocuments();
    }

    private static void storeDocuments() {
        //document 1
        Document document = Document.from(
                """
                        MySimpleRestFramework is old framework for creating REST APIs.
                        It provides auto-configuration and embedded servers.
                        """);

        DocumentSplitter splitter = DocumentSplitters.recursive(200, 20);
        List<TextSegment> segments = splitter.split(document);

        embeddingStore.addAll(embeddingModel.embedAll(segments).content(), segments);

        //document 2
        Document document2 = Document.from(
                """
                        MySimpleAiFramework is a Java framework for building 
                         LLM-powered applications.
                         It supports chat models, embeddings, and 
                         retrieval-augmented generation. 
                        """
        );

        List<TextSegment> segments2 = splitter.split(document2);

        embeddingStore.addAll(embeddingModel.embedAll(segments2).content(), segments2);
    }

    interface DocumentAssistant {
        @UserMessage("{{it}}")
        String answer(String question);
    }

    static void main(String[] args) {
        // Create models
        OllamaChatModel chatModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .temperature(0.3)
                .numCtx(1096)
                .timeout(Duration.of(3, ChronoUnit.MINUTES))
                .build();

        EmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("all-minilm")
                .build();

        ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(1)
                .build();

        // Create AI Service with RAG
        DocumentAssistant assistant = AiServices.builder(DocumentAssistant.class)
                .chatModel(chatModel)
                .contentRetriever(retriever)
                .build();

        String question = "What is MySimpleAiFramework?";
        System.out.println("User: " + question);
        String response = assistant.answer(question);
        System.out.println(response);
    }
}
