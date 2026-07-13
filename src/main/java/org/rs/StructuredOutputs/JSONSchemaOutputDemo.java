package org.rs.StructuredOutputs;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonBooleanSchema;
import dev.langchain4j.model.chat.request.json.JsonEnumSchema;
import dev.langchain4j.model.chat.request.json.JsonIntegerSchema;
import dev.langchain4j.model.chat.request.json.JsonNumberSchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.util.Arrays;
import java.util.List;

public class JSONSchemaOutputDemo {
    static void main() {
        ChatModel chatModel = getChatModel();

        PersonExtractionExample(chatModel);

//        SchemaTypesExample(chatModel);
    }

    private static void PersonExtractionExample(ChatModel chatModel) {
        // Define JSON Schema for Person
        JsonSchema jsonSchema = JsonSchema.builder()
                .name("Person")
                .rootElement(JsonObjectSchema
                        .builder()
                        .addStringProperty("name")
                        .addIntegerProperty("age")
                        .addNumberProperty("height")
                        .addBooleanProperty("married")
                        .addEnumProperty("employmentStatus", EmploymentStatus.toStringList())
                        .required("name", "age", "height", "married", "employmentStatus")
                        .build())
                .build();

        // Create response format with JSON Schema
        ResponseFormat responseFormat = ResponseFormat.builder()
                .type(ResponseFormatType.JSON)
                .jsonSchema(jsonSchema)
                .build();

        // Create user message with text to analyze
        String text = """
                John is 42 years old. 
                   He stands 1.75 meters tall and carries himself with confidence. 
                   He is employed in a local company as a Software Engineer. 
                   He's married with 2 kids.
                """;

        // Create chat request
        ChatRequest chatRequest = ChatRequest.builder()
                .responseFormat(responseFormat)
                .messages(UserMessage.from(text))
                .build();

        // Send request and get response
        ChatResponse chatResponse = chatModel.chat(chatRequest);

        // Extract and parse JSON response
        String jsonResponse = chatResponse.aiMessage().text();
        System.out.println("JSON Response: " + jsonResponse);

        // Parse JSON to Person object
        ObjectMapper mapper = new ObjectMapper();
        try {
            Person person = mapper.readValue(jsonResponse, Person.class);
            System.out.println("Person Detail: "+person);
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }

    private static ChatModel getChatModel() {
        ChatModel chatModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .logRequests(true)
                .logResponses(true)
                .build();
        return chatModel;
    }

    private static void SchemaTypesExample(ChatModel chatModel) {
        // Example 1: String schema with description
        JsonSchemaElement stringSchema = JsonStringSchema.builder()
                .description("The full name of the person")
                .build();

        // Example 2: Integer schema
        JsonSchemaElement integerSchema = JsonIntegerSchema.builder()
                .description("Age in years")
                .build();

        // Example 3: Number schema for decimal values
        JsonSchemaElement numberSchema = JsonNumberSchema.builder()
                .description("Height in meters")
                .build();

        // Example 4: Boolean schema
        JsonSchemaElement booleanSchema = JsonBooleanSchema.builder()
                .description("Marital status")
                .build();

        // Example 5: Enum schema
        JsonSchemaElement enumSchema = JsonEnumSchema.builder()
                .description("Employment status")
                .enumValues(EmploymentStatus.toStringList())
                .build();

        // Example 6: Complete object schema
        JsonSchemaElement personSchema = JsonObjectSchema.builder()
                .addProperty("name", stringSchema)
                .addProperty("age", integerSchema)
                .addProperty("height", numberSchema)
                .addProperty("married", booleanSchema)
                .addProperty("employmentStatus", enumSchema)
                .required("name", "age")
                .build();

        // Create JSON Schema
        JsonSchema jsonSchema = JsonSchema.builder()
                .name("DetailedPerson")
                .rootElement(personSchema)
                .build();

        // Create response format
        ResponseFormat responseFormat = ResponseFormat.builder()
                .type(ResponseFormatType.JSON)
                .jsonSchema(jsonSchema)
                .build();

        String text = """
                John is 42 years old and lives an independent life. 
                He stands 1.75 meters tall. He has a his own business and works from home. 
                Currently unmarried he enjoys the freedom to focus on his personal goals.
                """;

        // Create chat request
        ChatRequest chatRequest =
                ChatRequest.builder()
                        .responseFormat(responseFormat)
                        .messages(UserMessage.from(text))
                        .build();

        // Send request and get response
        ChatResponse chatResponse = chatModel.chat(chatRequest);

        // Extract and parse JSON response
        String jsonResponse = chatResponse.aiMessage().text();
        System.out.println("JSON Response: " + jsonResponse);

        // Parse JSON to Person object
        ObjectMapper mapper = new ObjectMapper();
        try {
            Person person = mapper.readValue(jsonResponse, Person.class);
            System.out.println(person);
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }


}



enum EmploymentStatus {
    EMPLOYED,
    UNEMPLOYED,
    SELF_EMPLOYED;

    public static List<String> toStringList() {
        return Arrays.stream(EmploymentStatus.values())
                .map(Enum::name)
                .toList();
    }
}

record Person(
        String name,
        int age,
        double height,
        boolean married,
        EmploymentStatus employmentStatus) {}
