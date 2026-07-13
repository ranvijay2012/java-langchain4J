package org.rs.StructuredOutputs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonArraySchema;
import dev.langchain4j.model.chat.request.json.JsonEnumSchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.util.Arrays;
import java.util.List;

public class AdvancedJSONSchemaOutputDemo {
    static void main() {
        ChatModel chatModel = getChatModel();

//        PeopleArrayExample(chatModel);

//        StatusEnumExample(chatModel);

        CompanyExample(chatModel);


    }

    private static void CompanyExample(ChatModel chatModel) {
        // Create enum schema for roles
        JsonSchemaElement roleSchema = JsonEnumSchema.builder()
                .enumValues("DEVELOPER", "DESIGNER", "MANAGER", "ANALYST")
                .description("Employee role")
                .build();

        // Create schema for employee
        JsonSchemaElement employeeSchema = JsonObjectSchema.builder()
                .addStringProperty("name")
                .addStringProperty("email")
                .addProperty("role", roleSchema)
                .required("name", "email", "role")
                .build();

        // Create array schema for employees
        JsonSchemaElement employeesArraySchema = JsonArraySchema.builder()
                .items(employeeSchema)
                .description("List of company employees")
                .build();

        // Create company schema with employees array
        JsonSchemaElement companySchema = JsonObjectSchema.builder()
                .addStringProperty("name", "Company name")
                .addProperty("employees", employeesArraySchema)
                .required("name", "employees")
                .build();

        // Create JSON Schema
        JsonSchema jsonSchema = JsonSchema.builder()
                .name("Company")
                .rootElement(companySchema)
                .build();

        // Create response format
        ResponseFormat responseFormat = ResponseFormat.builder()
                .type(ResponseFormatType.JSON)
                .jsonSchema(jsonSchema)
                .build();

        // Text describing a company
        String text = """
                Tech Innovations Inc. is a software company with several employees.
                John Smith works as a DEVELOPER with email john@tech.com.
                Sarah Johnson is a DESIGNER with email sarah@tech.com.
                Mike Brown serves as a MANAGER with email mike@tech.com.
                Lisa Wang works as an ANALYST with email lisa@tech.com.
                """;

        UserMessage userMessage = UserMessage.from(text);

        // Create chat request
        ChatRequest chatRequest =
                ChatRequest.builder()
                        .responseFormat(responseFormat)
                        .messages(userMessage)
                        .build();

        System.out.println("Extracting company information...");

        // Send request and get response
        ChatResponse chatResponse = chatModel.chat(chatRequest);

        // Extract JSON response
        String jsonResponse = chatResponse.aiMessage().text();
        System.out.println("JSON Response: " + jsonResponse);

        // Parse JSON to Company object
        ObjectMapper mapper = new ObjectMapper();
        try {
            Company company = mapper.readValue(jsonResponse, Company.class);
            System.out.println(company);
            System.out.println("Number of Employees: " + company.employees().size());
            System.out.println("\nEmployees:");
            for (Employee emp : company.employees()) {
                System.out.println(emp);
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            System.out.println("\nRaw JSON output demonstrates complex nested structure.");
        }
    }

    private static void StatusEnumExample(ChatModel chatModel) {
        // Create enum schema for status
        JsonSchemaElement statusSchema =
                JsonEnumSchema.builder()
                        .enumValues(Arrays.stream(Status.values())
                                .map(Enum::name)
                                .toList())
                        .description("Current status of the item")
                        .build();

        // Create object schema with status field
        JsonSchemaElement itemSchema = JsonObjectSchema.builder()
                .addStringProperty("itemName")
                .addProperty("status", statusSchema)
                .required("itemName", "status")
                .build();

        JsonSchemaElement itemsArraySchema = JsonArraySchema.builder()
                .items(itemSchema)
                .description("List of items with their statuses")
                .build();
        // Create JSON Schema
        JsonSchema jsonSchema = JsonSchema.builder()
                .name("ItemStatusList")
                .rootElement(itemsArraySchema)
                .build();

        // Create response format
        ResponseFormat responseFormat = ResponseFormat.builder()
                .type(ResponseFormatType.JSON)
                .jsonSchema(jsonSchema)
                .build();

        // Text describing status
        String text = """
                The 'Website Redesign' is currently active and progressing well.
                The 'Database Migration' has been completed successfully.
                The 'User Authentication' is still pending review.
                """;

        UserMessage userMessage = UserMessage.from(text);

        // Create chat request
        ChatRequest chatRequest = ChatRequest.builder()
                .responseFormat(responseFormat)
                .messages(userMessage)
                .build();

        System.out.println("Extracting status information...");

        // Send request and get response
        ChatResponse chatResponse = chatModel.chat(chatRequest);

        // Extract JSON response
        String jsonResponse = chatResponse.aiMessage().text();
        System.out.println("JSON Response: " + jsonResponse);

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ItemStatus> items = mapper.readValue(jsonResponse, new TypeReference<>() {
            });

            for (ItemStatus item : items) {
                System.out.println(item);
            }

        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }

    private static void PeopleArrayExample(ChatModel chatModel) {

        // Define schema for a single person
        JsonSchemaElement personSchema = JsonObjectSchema.builder()
                .addStringProperty("name")
                .addIntegerProperty("age")
                .required("name", "age")
                .build();

        // Create array schema for multiple people
        JsonSchemaElement peopleArraySchema = JsonArraySchema.builder()
                .items(personSchema)
                .build();

        // Create JSON Schema with array as root
        JsonSchema jsonSchema = JsonSchema.builder()
                .name("People")
                .rootElement(peopleArraySchema)
                .build();

        // Create response format
        ResponseFormat responseFormat = ResponseFormat.builder()
                .type(ResponseFormatType.JSON)
                .jsonSchema(jsonSchema)
                .build();

        // Text containing multiple people
        String text = """
                In the meeting we had John who is 42 years old, 
                Sarah who is 35 years old, and Michael who is 28 years old.
                Also present was Lisa, aged 31.
                """;

        UserMessage userMessage = UserMessage.from(text);

        // Create chat request
        ChatRequest chatRequest = ChatRequest.builder()
                .responseFormat(responseFormat)
                .messages(userMessage)
                .build();

        // Send request and get response
        ChatResponse chatResponse = chatModel.chat(chatRequest);

        // Extract JSON response
        String jsonResponse = chatResponse.aiMessage().text();
        System.out.println("JSON Response: " + jsonResponse);

        // Parse JSON to List of Person objects
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Person> people = mapper.readValue(jsonResponse, new TypeReference<>() {
            });

            System.out.println("\nExtracted People:");
            for (Person person : people) {
                System.out.println(person);
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }

    private static ChatModel getChatModel() {
        // Create Ollama ChatModel with phi3
        ChatModel chatModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3:mini-128k")
                .logRequests(true)
                .logResponses(true)
                .build();
        return chatModel;
    }
}

enum Status {
    ACTIVE,
    INACTIVE,
    PENDING,
    COMPLETED
}

record ItemStatus(String itemName, Status status) {
}

record Company(String name, List<Employee> employees) {
}

record Employee(String name, String email, String role) {
}
