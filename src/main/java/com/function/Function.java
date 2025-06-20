package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Azure Functions with HTTP Trigger for JSON POST requests.
 */
public class Function {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * This function accepts POST requests with JSON body containing firstName and lastName,
     * and responds with a personalized greeting in JSON format.
     */
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
            // Get the request body
            Optional<String> requestBody = request.getBody();
            
            if (!requestBody.isPresent() || requestBody.get().isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"Request body is required\"}")
                    .build();
            }
            
            // Parse JSON from request body
            JsonNode jsonNode = objectMapper.readTree(requestBody.get());
            
            // Extract firstName and lastName
            String firstName = jsonNode.get("firstName") != null ? jsonNode.get("firstName").asText() : null;
            String lastName = jsonNode.get("lastName") != null ? jsonNode.get("lastName").asText() : null;
            
            // Validate required fields
            if (firstName == null || lastName == null || firstName.isEmpty() || lastName.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"Both firstName and lastName are required\"}")
                    .build();
            }
            
            // Create response JSON
            String jsonResponse = "{\"message\":\"Hello, " + firstName + " " + lastName + "\"}";
            
            return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(jsonResponse)
                .build();
                
        } catch (Exception e) {
            context.getLogger().severe("Error processing request: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("{\"error\":\"Invalid JSON format\"}")
                .build();
        }
    }
}