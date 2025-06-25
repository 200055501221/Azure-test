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
    final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

    final String jsonBody = "{\"firstName\":\"John\",\"lastName\":\"Dawson\",\"city\":\"Colombo\",\"age\":25}";
    final Optional<String> requestBody = Optional.of(jsonBody);
    doReturn(requestBody).when(req).getBody();

    doReturn(HttpMethod.POST).when(req).getHttpMethod();  // 👈 added this line

    final Map<String, String> queryParams = new HashMap<>();
    doReturn(queryParams).when(req).getQueryParameters();

    final HttpResponseMessage.Builder responseBuilder = mock(HttpResponseMessage.Builder.class);
    doReturn(responseBuilder).when(req).createResponseBuilder(any(HttpStatus.class));
    doReturn(responseBuilder).when(responseBuilder).body(any());
    doReturn(responseBuilder).when(responseBuilder).header(anyString(), anyString());

    final HttpResponseMessage response = mock(HttpResponseMessage.class);
    doReturn(response).when(responseBuilder).build();
    doReturn(HttpStatus.OK).when(response).getStatus();

    final ExecutionContext context = mock(ExecutionContext.class);
    doReturn(Logger.getGlobal()).when(context).getLogger();

    final Function function = new Function();
    final HttpResponseMessage ret = function.run(req, context);

    assertEquals(HttpStatus.OK, ret.getStatus());
}


@Test
public void testHttpTriggerJavaWithMissingBody() throws Exception {
    final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

    final Optional<String> requestBody = Optional.empty();
    doReturn(requestBody).when(req).getBody();

    doReturn(HttpMethod.POST).when(req).getHttpMethod();  // 👈 added this line

    final Map<String, String> queryParams = new HashMap<>();
    doReturn(queryParams).when(req).getQueryParameters();

    final HttpResponseMessage.Builder responseBuilder = mock(HttpResponseMessage.Builder.class);
    doReturn(responseBuilder).when(req).createResponseBuilder(HttpStatus.BAD_REQUEST);
    doReturn(responseBuilder).when(responseBuilder).body(any());
    doReturn(responseBuilder).when(responseBuilder).header(anyString(), anyString());

    final HttpResponseMessage response = mock(HttpResponseMessage.class);
    doReturn(response).when(responseBuilder).build();
    doReturn(HttpStatus.BAD_REQUEST).when(response).getStatus();

    final ExecutionContext context = mock(ExecutionContext.class);
    doReturn(Logger.getGlobal()).when(context).getLogger();

    final Function function = new Function();
    final HttpResponseMessage ret = function.run(req, context);

    assertEquals(HttpStatus.BAD_REQUEST, ret.getStatus());
}


@Test
public void testHttpTriggerJavaWithInvalidJson() throws Exception {
    final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

    final String invalidJsonBody = "{invalid json}";
    final Optional<String> requestBody = Optional.of(invalidJsonBody);
    doReturn(requestBody).when(req).getBody();

    doReturn(HttpMethod.POST).when(req).getHttpMethod();  // 👈 added this line

    final Map<String, String> queryParams = new HashMap<>();
    doReturn(queryParams).when(req).getQueryParameters();

    final HttpResponseMessage.Builder responseBuilder = mock(HttpResponseMessage.Builder.class);
    doReturn(responseBuilder).when(req).createResponseBuilder(HttpStatus.BAD_REQUEST);
    doReturn(responseBuilder).when(responseBuilder).body(any());
    doReturn(responseBuilder).when(responseBuilder).header(anyString(), anyString());

    final HttpResponseMessage response = mock(HttpResponseMessage.class);
    doReturn(response).when(responseBuilder).build();
    doReturn(HttpStatus.BAD_REQUEST).when(response).getStatus();

    final ExecutionContext context = mock(ExecutionContext.class);
    doReturn(Logger.getGlobal()).when(context).getLogger();

    final Function function = new Function();
    final HttpResponseMessage ret = function.run(req, context);

    assertEquals(HttpStatus.BAD_REQUEST, ret.getStatus());
}


@Test
public void testHttpTriggerJavaWithMissingFields() throws Exception {
    final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

    final String jsonBody = "{\"firstName\":\"John\"}";
    final Optional<String> requestBody = Optional.of(jsonBody);
    doReturn(requestBody).when(req).getBody();

    doReturn(HttpMethod.POST).when(req).getHttpMethod();  // 👈 added this line

    final Map<String, String> queryParams = new HashMap<>();
    doReturn(queryParams).when(req).getQueryParameters();

    final HttpResponseMessage.Builder responseBuilder = mock(HttpResponseMessage.Builder.class);
    doReturn(responseBuilder).when(req).createResponseBuilder(HttpStatus.BAD_REQUEST);
    doReturn(responseBuilder).when(responseBuilder).body(any());
    doReturn(responseBuilder).when(responseBuilder).header(anyString(), anyString());

    final HttpResponseMessage response = mock(HttpResponseMessage.class);
    doReturn(response).when(responseBuilder).build();
    doReturn(HttpStatus.BAD_REQUEST).when(response).getStatus();

    final ExecutionContext context = mock(ExecutionContext.class);
    doReturn(Logger.getGlobal()).when(context).getLogger();

    final Function function = new Function();
    final HttpResponseMessage ret = function.run(req, context);

    assertEquals(HttpStatus.BAD_REQUEST, ret.getStatus());
}

}
