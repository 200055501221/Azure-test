package com.function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

                // Validate presence
                if (firstName == null || lastName == null || firstName.isEmpty() || lastName.isEmpty()) {
                String errorResponse = "{\"StatusCode\":-1,\"Message\":\"Unsuccessful: Missing fields\"}";
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .header("Content-Type", "application/json")
                        .body(errorResponse)
                        .build();
                }

                // Validate content: must contain only letters
                if (!firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+")) {
                String errorResponse = "{\"StatusCode\":-1,\"Message\":\"Unsuccessful: Invalid characters in names\"}";
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .header("Content-Type", "application/json")
                        .body(errorResponse)
                        .build();
                }

                try (Connection conn = DatabaseUtil.getConnection()) {
                String sql = "INSERT INTO users (first_name, last_name) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                int rowsInserted = stmt.executeUpdate();

                if (rowsInserted > 0) {
                        context.getLogger().info("User inserted successfully.");
                } else {
                        context.getLogger().warning("User insert failed.");
                }
                } catch (SQLException e) {
                context.getLogger().severe("Database error: " + e.getMessage());
                String errorResponse = "{\"StatusCode\":-1,\"Message\":\"Database Error\"}";
                return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
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
