package org.rs.aiServices;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import java.time.Duration;

public class ChainingExample {
    // Service 1: Query Classifier
    interface QueryClassifier {
        @UserMessage("Classify this query into one of these classifiers: "
                + "GREETING, QUESTION, COMMAND, or OTHER. Only return the classifier. "
                + "No explanation needed. Query: {{it}}")
        String classify(String query);
    }

    // Service 2: Greeting Responder (using model for simple task)
    interface GreetingResponder {
        @SystemMessage("You are a friendly assistant. "
                + "Respond to greetings warmly but briefly.")
        String respondToGreeting(String greeting);
    }

    // Service 3: Technical Expert (more capable model for complex questions)
    interface TechnicalExpert {
        @SystemMessage("You are a technical expert. Provide short, "
                + "accurate answers to technical questions.")
        String answerTechnicalQuestion(String question);
    }

    static class CommandTools {
        @Tool("Book a flight from one city to another")
        String bookFlight(String from,
                          String to) {
            System.out.printf("\n[TOOL CALLED] 'bookFlight' with params: "
                    + "from='%s', to='%s'\n", from, to);
            //simulating booking flight
            return String.format("Flight booked from %s to %s at 3pm tomorrow. "
                    + "Confirmation code: ABC123", from, to);
        }
    }

    // Service 4: Command Processor
    interface CommandProcessor {
        String processCommand(String command);
    }

    // Main orchestrator that chains services
    static class SmartAssistant {
        private final QueryClassifier classifier;
        private final GreetingResponder greeter;
        private final TechnicalExpert expert;
        private final CommandProcessor commandProcessor;

        public SmartAssistant(QueryClassifier classifier,
                              GreetingResponder greeter,
                              TechnicalExpert expert,
                              CommandProcessor commandProcessor) {
            this.classifier = classifier;
            this.greeter = greeter;
            this.expert = expert;
            this.commandProcessor = commandProcessor;
        }

        public String handleQuery(String query) {
            System.out.println("Query: " + query);
            // Step 1: Classify the query
            String classification = classifier.classify(query);
            System.out.println("Classification: " + classification);

            // Step 2: Route to appropriate service
            return switch (classification.toUpperCase()) {
                case "GREETING" -> {
                    System.out.println("Routing to Greeting Responder");
                    yield greeter.respondToGreeting(query);
                }
                case "QUESTION" -> {
                    System.out.println("Routing to Technical Expert");
                    yield expert.answerTechnicalQuestion(query);
                }
                case "COMMAND" -> {
                    System.out.println("Routing to Command processor");
                    yield commandProcessor.processCommand(query);
                }
                default -> {
                    System.out.println("Using default response");
                    yield "I'm not sure how to handle that. Could you rephrase?";
                }
            };
        }
    }

    static void main(String[] args) {
        // Create different models for different tasks
        ChatModel cheapModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .numCtx(4096)
                .timeout(Duration.ofMinutes(3))
                .temperature(0.1) // Low temp for classification
                .build();

        ChatModel capableModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2:latest")
                .numCtx(4096)
                .timeout(Duration.ofMinutes(3))
                .temperature(0.7) // Higher temp for creative responses
                .build();

        // Create all services
        GreetingResponder greeter = AiServices.create(GreetingResponder.class, cheapModel);
        QueryClassifier classifier = AiServices.create(QueryClassifier.class, capableModel);
        TechnicalExpert expert = AiServices.create(TechnicalExpert.class, capableModel);
        CommandProcessor commandProcessor = AiServices.builder(CommandProcessor.class)
                .chatModel(capableModel)
                .tools(new CommandTools())
                .build();

        // Create orchestrator
        SmartAssistant assistant = new SmartAssistant(classifier, greeter, expert, commandProcessor);

        // Test different queries
        System.out.println("-- Example 1: Greeting --");
        String response1 = assistant.handleQuery("Hello there!");
        System.out.println("Response: " + response1);

        System.out.println("\n-- Example 2: Technical Question --");
        String response2 = assistant.handleQuery("Explain how neural networks work "
                + "in 2 sentences");
        System.out.println("Response: " + response2);
        System.out.println("\n-- Example 3: Command --");
        String response3 = assistant.handleQuery("Please book a flight from Seoul to Singapore.");
        System.out.println("Response: " + response3);

        System.out.println("\n-- Example 4: Question  --");
        String response4 = assistant.handleQuery("What's an AI agent "
                + "in 2 sentences?");
        System.out.println("Response: " + response4);
    }

}
