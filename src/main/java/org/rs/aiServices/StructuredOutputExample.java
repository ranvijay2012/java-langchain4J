package org.rs.aiServices;

import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.model.output.structured.Description;

import java.time.LocalDate;
import java.util.List;

public class StructuredOutputExample {

    record Person(
            @Description("First name of the person")
            String firstName,
            @Description("Last name of the person")
            String lastName,
            @Description("Birth date in YYYY-MM-DD format, e.g., 1985-03-15")
            LocalDate birthDate) {}

    record Meeting(
            @Description("Meeting title")
            String title,
            @Description("List of participants as array of strings")
            List<String> participants,
            @Description("Meeting date in YYYY-MM-DD format, e.g., 2024-12-10")
            LocalDate date) {}

    interface PersonExtractor {
        @UserMessage("Extract person information from: {{it}}. "
                + "Return birth date in YYYY-MM-DD format only.")
        Person extractPerson(String text);
    }

    interface MeetingExtractor {
        @UserMessage("Extract meeting details from: {{it}}. "
                + "Return date in YYYY-MM-DD format only.")
        Meeting extractMeeting(String text);
    }

    interface SentimentAnalyzer {
        @UserMessage("Analyze sentiment of: {{it}}. "
                + "Return one of: POSITIVE, NEUTRAL, or NEGATIVE.")
        String analyzeSentiment(String text);
    }

    // Alternative with Result<T> for version 1.10.0
    interface PersonExtractorWithResult {
        @UserMessage("Extract person information from: {{it}}. "
                + "Return birth date in YYYY-MM-DD format only.")
        Result<Person> extractPersonWithResult(String text);
    }

    static void main() {
        // Enable JSON mode for structured output
        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .responseFormat(ResponseFormat.JSON)
                .temperature(0.1)
                .build();

        PersonExtractor personExtractor = AiServices.create(PersonExtractor.class, model);
        MeetingExtractor meetingExtractor = AiServices.create(MeetingExtractor.class, model);
        SentimentAnalyzer sentimentAnalyzer = AiServices.create(SentimentAnalyzer.class, model);

        try {
            // Example 1: Person extraction
            String personText = "John Doe was born on 1985-03-15 in New York.";
            Person person = personExtractor.extractPerson(personText);
            System.out.println("Extracted Person: " + person);
        } catch (Exception e) {
            System.out.println("Error extracting person: " + e.getMessage());
        }

        try {
            // Example 2: Meeting extraction with problematic date
            String meetingText = "Team meeting about Q4 planning with "
                    + "Alice, Bob, and Charlie scheduled for December 10, 2024.";
            Meeting meeting = meetingExtractor.extractMeeting(meetingText);
            System.out.println("\nExtracted Meeting: " + meeting);

        } catch (Exception e) {
            System.out.println("Error extracting meeting: " + e.getMessage());
        }

        try {
            // Example 3: Sentiment analysis
            String review = "This product is absolutely amazing! I love it.";
            String sentiment = sentimentAnalyzer.analyzeSentiment(review);
            System.out.println("\nSentiment: " + sentiment);
        } catch (Exception e) {
            System.out.println("Error analyzing sentiment: " + e.getMessage());
        }

        // Alternative approach with Result<T> in 1.10.0
        System.out.println("\n=== Alternative Approach (with Result<T>) ===");

        PersonExtractorWithResult personExtractorWithResult =
                AiServices.create(PersonExtractorWithResult.class, model);

        try {
            String personText2 = "Jane Smith's birthday is 1990-08-22.";
            Result<Person> result = personExtractorWithResult.extractPersonWithResult(personText2);

            // In 1.10.0, Result<T> has these methods:
            Person person = result.content(); // Get the content
            System.out.println("Extracted Person: " + person);

            // You can also get metadata
            if (result.tokenUsage() != null) {
                System.out.println("Token Usage - Input: " + result.tokenUsage().inputTokenCount()
                        + ", Output: " + result.tokenUsage().outputTokenCount());
            }

            if (result.finishReason() != null) {
                System.out.println("Finish Reason: " + result.finishReason());
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }


}
