package org.rs.RAG;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.List;

public class RagIntroExample {

    private static EmbeddingModel embeddingModel;
    private static EmbeddingStore<TextSegment> embeddingStore;

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

        List<TextSegment> segments1 = splitter.split(document2);
        embeddingStore.addAll(embeddingModel.embedAll(segments1).content(), segments1);


    }

    static void main() {
        ChatModel chatModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .build();

        ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(1)
                .build();

        String question = "What is MySimpleAiFramework?";

        List<Content> contents = retriever.retrieve(Query.from(question));
        String context = contents.get(0).textSegment().text();

        System.out.println("Context retrieved: " + context);

        String prompt = """
                Use the following context to answer the question:          %s
                
                Question: %s
                """.formatted(context, question);

        String answer = chatModel.chat(prompt);

        System.out.println("LLM response: " + answer);
    }
}
