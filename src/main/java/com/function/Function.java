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
 * Azure Functions with HTTP Trigger for JSON requests.
 */
public class Function {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE},
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Java HTTP trigger processed a " + request.getHttpMethod().name() + " request.");

        try {
            Optional<String> requestBody = request.getBody();
            if (!requestBody.isPresent() || requestBody.get().isEmpty()) {
                return errorResponse(request, "Empty request body");
            }

            JsonNode jsonNode = objectMapper.readTree(requestBody.get());
            HttpMethod method = request.getHttpMethod();

            if (method == HttpMethod.POST) {
                // POST: Insert new record
                String firstName = jsonNode.path("firstName").asText(null);
                String lastName = jsonNode.path("lastName").asText(null);
                String city = jsonNode.path("city").asText(null);
                Integer age = jsonNode.has("age") && jsonNode.get("age").canConvertToInt() ? jsonNode.get("age").asInt() : null;

                if (firstName == null || lastName == null || city == null || age == null ||
                        firstName.isEmpty() || lastName.isEmpty() || city.isEmpty() ||
                        !firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+")) {
                    return errorResponse(request, "Invalid or missing data for insert");
                }

                try (Connection conn = DatabaseUtil.getConnection()) {
                    String sql = "INSERT INTO users (first_name, last_name, city, age) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, firstName);
                    stmt.setString(2, lastName);
                    stmt.setString(3, city);
                    stmt.setInt(4, age);
                    stmt.executeUpdate();
                }

                return successResponse(request, "User inserted successfully");

            } else if (method == HttpMethod.PUT) {
                // PUT: Update existing record
                int id = jsonNode.path("id").asInt(-1);
                String firstName = jsonNode.path("firstName").asText(null);
                String lastName = jsonNode.path("lastName").asText(null);
                String city = jsonNode.path("city").asText(null);
                Integer age = jsonNode.has("age") && jsonNode.get("age").canConvertToInt() ? jsonNode.get("age").asInt() : null;

                if (id <= 0 || firstName == null || lastName == null || city == null || age == null ||
                        firstName.isEmpty() || lastName.isEmpty() || city.isEmpty() ||
                        !firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+")) {
                    return errorResponse(request, "Invalid or missing data for update");
                }

                try (Connection conn = DatabaseUtil.getConnection()) {
                    String sql = "UPDATE users SET first_name=?, last_name=?, city=?, age=? WHERE id=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, firstName);
                    stmt.setString(2, lastName);
                    stmt.setString(3, city);
                    stmt.setInt(4, age);
                    stmt.setInt(5, id);
                    int rows = stmt.executeUpdate();
                    if (rows == 0) {
                        return errorResponse(request, "No user found with given id");
                    }
                }

                return successResponse(request, "User updated successfully");

            } else if (method == HttpMethod.DELETE) {
                // DELETE: Remove record by id
                int id = jsonNode.path("id").asInt(-1);
                if (id <= 0) {
                    return errorResponse(request, "Invalid or missing id for delete");
                }

                try (Connection conn = DatabaseUtil.getConnection()) {
                    String sql = "DELETE FROM users WHERE id=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, id);
                    int rows = stmt.executeUpdate();
                    if (rows == 0) {
                        return errorResponse(request, "No user found with given id");
                    }
                }

                return successResponse(request, "User deleted successfully");
            }

            return errorResponse(request, "Unsupported HTTP method");

        } catch (Exception e) {
            context.getLogger().severe("Error processing request: " + e.getMessage());
            return errorResponse(request, "Exception occurred");
        }
    }

    private HttpResponseMessage successResponse(HttpRequestMessage<Optional<String>> request, String message) {
        String response = "{\"StatusCode\":0,\"Message\":\"" + message + "\"}";
        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response)
                .build();
    }

    private HttpResponseMessage errorResponse(HttpRequestMessage<Optional<String>> request, String message) {
        String response = "{\"StatusCode\":-1,\"Message\":\"" + message + "\"}";
        return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .header("Content-Type", "application/json")
                .body(response)
                .build();
    }
}
