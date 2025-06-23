package com.function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger for JSON POST requests.
 */
public class Function {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Java HTTP trigger processed a POST request.");

        try {
            Optional<String> requestBody = request.getBody();

            if (!requestBody.isPresent() || requestBody.get().isEmpty()) {
                String errorResponse = "{\"StatusCode\":-1,\"Message\":\"Unsuccessful\"}";
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .header("Content-Type", "application/json")
                        .body(errorResponse)
                        .build();
            }

            JsonNode jsonNode = objectMapper.readTree(requestBody.get());

            String firstName = jsonNode.has("firstName") ? jsonNode.get("firstName").asText() : null;
            String lastName = jsonNode.has("lastName") ? jsonNode.get("lastName").asText() : null;

            if (firstName == null || lastName == null || firstName.isEmpty() || lastName.isEmpty()) {
                String errorResponse = "{\"StatusCode\":-1,\"Message\":\"Unsuccessful\"}";
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .header("Content-Type", "application/json")
                        .body(errorResponse)
                        .build();
            }

            String successResponse = "{"
                    + "\"firstname\":\"" + firstName + "\","
                    + "\"lastname\":\"" + lastName + "\","
                    + "\"StatusCode\":0,"
                    + "\"Message\":\"Success\""
                    + "}";

            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(successResponse)
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Error processing request: " + e.getMessage());
            String errorResponse = "{\"StatusCode\":-1,\"Message\":\"Unsuccessful\"}";
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .header("Content-Type", "application/json")
                    .body(errorResponse)
                    .build();
        }
    }
}
