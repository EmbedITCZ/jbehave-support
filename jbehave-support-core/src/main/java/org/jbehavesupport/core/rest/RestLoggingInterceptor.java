package org.jbehavesupport.core.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Slf4j
public class RestLoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest req, byte[] reqBody, ClientHttpRequestExecution ex) throws IOException {
        ClientHttpResponse response = ex.execute(req, reqBody);
        if (log.isTraceEnabled()) {
            log.trace("Request body: {} with headers: {}", new String(reqBody, StandardCharsets.UTF_8), req.getHeaders());
            InputStreamReader isr = new InputStreamReader(
                response.getBody(), StandardCharsets.UTF_8);
            String body = new BufferedReader(isr).lines()
                .collect(Collectors.joining("\n"));
            log.trace("Response body: {} with headers: {}", body, response.getHeaders());
        }
        return response;
    }
}
