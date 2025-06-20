// package com.function;

// import com.microsoft.azure.functions.*;
// import org.mockito.invocation.InvocationOnMock;
// import org.mockito.stubbing.Answer;

// import java.util.*;
// import java.util.logging.Logger;

// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;


// /**
//  * Unit test for Function class.
//  */
// public class FunctionTest {
//     /**
//      * Unit test for HttpTriggerJava method.
//      */
//     @Test
//     public void testHttpTriggerJava() throws Exception {
//         // Setup
//         @SuppressWarnings("unchecked")
//         final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

//         final Map<String, String> queryParams = new HashMap<>();
//         queryParams.put("name", "Azure");
//         doReturn(queryParams).when(req).getQueryParameters();

//         final Optional<String> queryBody = Optional.empty();
//         doReturn(queryBody).when(req).getBody();

//         doAnswer(new Answer<HttpResponseMessage.Builder>() {
//             @Override
//             public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
//                 HttpStatus status = (HttpStatus) invocation.getArguments()[0];
//                 return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
//             }
//         }).when(req).createResponseBuilder(any(HttpStatus.class));

//         final ExecutionContext context = mock(ExecutionContext.class);
//         doReturn(Logger.getGlobal()).when(context).getLogger();

//         // Invoke
//         final HttpResponseMessage ret = new Function().run(req, context);

//         // Verify
//         assertEquals(HttpStatus.OK, ret.getStatus());
//     }
// }
package com.function;

import com.microsoft.azure.functions.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for Function class.
 */
public class FunctionTest {
    /**
     * Unit test for HttpTriggerJava method.
     */
    @Test
    public void testHttpTriggerJava() throws Exception {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        // Create JSON request body
        final String jsonBody = "{\"firstName\":\"John\",\"lastName\":\"Dawson\"}";
        final Optional<String> requestBody = Optional.of(jsonBody);
        doReturn(requestBody).when(req).getBody();

        // Mock query parameters (empty since we're using POST body)
        final Map<String, String> queryParams = new HashMap<>();
        doReturn(queryParams).when(req).getQueryParameters();

        // Mock response builder
        final HttpResponseMessage.Builder responseBuilder = mock(HttpResponseMessage.Builder.class);
        doReturn(responseBuilder).when(req).createResponseBuilder(any(HttpStatus.class));
        doReturn(responseBuilder).when(responseBuilder).body(any());
        doReturn(responseBuilder).when(responseBuilder).header(anyString(), anyString());

        // Create mock response
        final HttpResponseMessage response = mock(HttpResponseMessage.class);
        doReturn(response).when(responseBuilder).build();
        doReturn(HttpStatus.OK).when(response).getStatus();
        doReturn("{\"message\":\"Hello, John Dawson\"}").when(response).getBody();

        // Mock execution context
        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Invoke
        final Function function = new Function();
        final HttpResponseMessage ret = function.run(req, context);

        // Verify
        assertEquals(HttpStatus.OK, ret.getStatus());
        assertEquals("{\"message\":\"Hello, John Dawson\"}", ret.getBody());
    }

    /**
     * Test for missing request body
     */
    @Test
    public void testHttpTriggerJavaWithMissingBody() throws Exception {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        // Empty request body
        final Optional<String> requestBody = Optional.empty();
        doReturn(requestBody).when(req).getBody();

        // Mock query parameters
        final Map<String, String> queryParams = new HashMap<>();
        doReturn(queryParams).when(req).getQueryParameters();

        // Mock response builder for BAD_REQUEST
        final HttpResponseMessage.Builder responseBuilder = mock(HttpResponseMessage.Builder.class);
        doReturn(responseBuilder).when(req).createResponseBuilder(HttpStatus.BAD_REQUEST);
        doReturn(responseBuilder).when(responseBuilder).body(any());

        // Create mock response
        final HttpResponseMessage response = mock(HttpResponseMessage.class);
        doReturn(response).when(responseBuilder).build();
        doReturn(HttpStatus.BAD_REQUEST).when(response).getStatus();

        // Mock execution context
        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Invoke
        final Function function = new Function();
        final HttpResponseMessage ret = function.run(req, context);

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, ret.getStatus());
    }

    /**
     * Test for invalid JSON
     */
    @Test
    public void testHttpTriggerJavaWithInvalidJson() throws Exception {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        // Invalid JSON request body
        final String invalidJsonBody = "{invalid json}";
        final Optional<String> requestBody = Optional.of(invalidJsonBody);
        doReturn(requestBody).when(req).getBody();

        // Mock query parameters
        final Map<String, String> queryParams = new HashMap<>();
        doReturn(queryParams).when(req).getQueryParameters();

        // Mock response builder for BAD_REQUEST
        final HttpResponseMessage.Builder responseBuilder = mock(HttpResponseMessage.Builder.class);
        doReturn(responseBuilder).when(req).createResponseBuilder(HttpStatus.BAD_REQUEST);
        doReturn(responseBuilder).when(responseBuilder).body(any());

        // Create mock response
        final HttpResponseMessage response = mock(HttpResponseMessage.class);
        doReturn(response).when(responseBuilder).build();
        doReturn(HttpStatus.BAD_REQUEST).when(response).getStatus();

        // Mock execution context
        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Invoke
        final Function function = new Function();
        final HttpResponseMessage ret = function.run(req, context);

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, ret.getStatus());
    }

    /**
     * Test for missing firstName or lastName
     */
    @Test
    public void testHttpTriggerJavaWithMissingFields() throws Exception {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        // JSON with missing lastName
        final String jsonBody = "{\"firstName\":\"John\"}";
        final Optional<String> requestBody = Optional.of(jsonBody);
        doReturn(requestBody).when(req).getBody();

        // Mock query parameters
        final Map<String, String> queryParams = new HashMap<>();
        doReturn(queryParams).when(req).getQueryParameters();

        // Mock response builder for BAD_REQUEST
        final HttpResponseMessage.Builder responseBuilder = mock(HttpResponseMessage.Builder.class);
        doReturn(responseBuilder).when(req).createResponseBuilder(HttpStatus.BAD_REQUEST);
        doReturn(responseBuilder).when(responseBuilder).body(any());

        // Create mock response
        final HttpResponseMessage response = mock(HttpResponseMessage.class);
        doReturn(response).when(responseBuilder).build();
        doReturn(HttpStatus.BAD_REQUEST).when(response).getStatus();

        // Mock execution context
        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Invoke
        final Function function = new Function();
        final HttpResponseMessage ret = function.run(req, context);

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, ret.getStatus());
    }
}