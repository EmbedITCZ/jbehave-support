package org.jbehavesupport.core.report.extension;

import static org.springframework.util.StringUtils.isEmpty;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.internal.FileNameResolver;
import org.jbehavesupport.core.report.ReportContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.MimeType;

@RequiredArgsConstructor
public class RestXmlReporterExtension extends AbstractXmlReporterExtension implements ClientHttpRequestInterceptor {

    @Value("${rest.directory:./target/reports}")
    private String restDirectory;

    private final TestContext testContext;

    private final FileNameResolver fileNameResolver;

    private static final String REST_XML_REPORTER_EXTENSION = "rest";
    private static final String REQUEST_RESPONSE_TAG = "requestResponse";
    private static final String REQUEST_TAG = "request";
    private static final String BODY_TAG = "body";
    private static final String RESPONSE_TAG = "response";
    private static final String HEADERS_TAG = "headers";
    private static final String HEADER_TAG = "header";
    private static MimeType[] compatibleMimeTypes =
        new MimeType[]{new MimeType("text"), new MimeType("application", "json"), new MimeType("application", "xml")};
    private static MimeType multipartFormDataType = new MimeType("multipart", "form-data");
    private static final String FILE_NAME_PATTERN = "multipart_%s.log";

    private final Set<RestMessageContext> messages = new LinkedHashSet<>();

    @Override
    public String getName() {
        return REST_XML_REPORTER_EXTENSION;
    }

    @Override
    public void print(final Writer writer, final ReportContext reportContext) {
        messages.forEach(message -> printRestMessage(writer, message));
        messages.clear();
    }

    @Override
    public ClientHttpResponse intercept(final HttpRequest httpRequest, final byte[] bytes, final ClientHttpRequestExecution clientHttpRequestExecution)
        throws IOException {
        RestMessageContext.RestMessageContextBuilder restMessageContextBuilder = RestMessageContext.builder();
        ClientHttpResponse clientHttpResponse;
        try {
            restMessageContextBuilder
                .requestTimeStamp(LocalDateTime.now())
                .method(httpRequest.getMethod())
                .url(httpRequest.getURI())
                .requestHeaders(httpRequest.getHeaders());
            MimeType requestContentType = httpRequest.getHeaders().getContentType();
            for (MimeType mimeType : compatibleMimeTypes) {
                if (requestContentType == null || requestContentType.isCompatibleWith(mimeType)) {
                    restMessageContextBuilder.requestJsonBody(((ClientHttpRequest) httpRequest).getBody().toString());
                    break;
                }
            }
            if (multipartFormDataType.isCompatibleWith(requestContentType)) {
                restMessageContextBuilder.requestJsonBody(handleMultipart(((ClientHttpRequest) httpRequest).getBody().toString()));
            }

            clientHttpResponse = clientHttpRequestExecution.execute(httpRequest, bytes);
            String responseBodyAsString = IOUtils.toString(clientHttpResponse.getBody(), StandardCharsets.UTF_8);
            restMessageContextBuilder
                .responseTimeStamp(LocalDateTime.now())
                .responseStatus(clientHttpResponse.getStatusCode())
                .responseHeaders(clientHttpResponse.getHeaders());
            MimeType responseContentType = clientHttpResponse.getHeaders().getContentType();
            for (MimeType mimeType : compatibleMimeTypes) {
                if (responseContentType == null || responseContentType.isCompatibleWith(mimeType)) {
                    restMessageContextBuilder.responseJsonBody(responseBodyAsString);
                    break;
                }
            }
            if (multipartFormDataType.isCompatibleWith(responseContentType)) {
                restMessageContextBuilder.responseJsonBody(handleMultipart(responseBodyAsString));
            }
        } finally {
            messages.add(restMessageContextBuilder.build());
        }
        return clientHttpResponse;
    }

    private void printRestMessage(final Writer writer, final RestMessageContext message) {
        printBegin(writer, REQUEST_RESPONSE_TAG);

        printBegin(writer, REQUEST_TAG, getRequestMessageAttributes(message));
        printHeaders(writer, message.getRequestHeaders());
        printJsonBody(writer, message.getRequestJsonBody());
        printEnd(writer, REQUEST_TAG);

        printBegin(writer, RESPONSE_TAG, getResponseMessageAttributes(message));
        printHeaders(writer, message.getResponseHeaders());
        printJsonBody(writer, message.getResponseJsonBody());
        printEnd(writer, RESPONSE_TAG);

        printEnd(writer, REQUEST_RESPONSE_TAG);
    }

    private void printHeaders(final Writer writer, HttpHeaders headers) {
        printBegin(writer, HEADERS_TAG);
        headers.entrySet().forEach(he -> printSelfClosed(writer, HEADER_TAG, getHeaderAttributes(he)));
        printEnd(writer, HEADERS_TAG);
    }

    private void printJsonBody(final Writer writer, final String message) {
        if (!isEmpty(message)) {
            printBegin(writer, BODY_TAG);
            printCData(writer, message);
            printEnd(writer, BODY_TAG);
        }
    }

    private Map<String, String> getRequestMessageAttributes(RestMessageContext message) {
        Map<String, String> requestMessageAttributes = new HashMap<>();
        requestMessageAttributes.put("url", message.getUrl().toASCIIString());
        requestMessageAttributes.put("method", message.getMethod().toString());
        requestMessageAttributes.put("time", message.getRequestTimeStamp().atZone(ZoneId.systemDefault()).toString());
        return requestMessageAttributes;
    }

    private Map<String, String> getResponseMessageAttributes(RestMessageContext message) {
        Map<String, String> responseMessageAttributes = new HashMap<>();
        responseMessageAttributes.put("status", String.valueOf(message.getResponseStatus()));
        responseMessageAttributes.put("time", message.getResponseTimeStamp().atZone(ZoneId.systemDefault()).toString());
        return responseMessageAttributes;
    }

    private Map<String, String> getHeaderAttributes(Map.Entry<String, List<String>> header) {
        Map<String, String> headerAttributes = new HashMap<>();
        headerAttributes.put("key", header.getKey());
        headerAttributes.put("value", header.getValue().get(0));
        return headerAttributes;
    }

    private String handleMultipart(String body) throws IOException {
        Path destinationPath = fileNameResolver.resolveFilePath(FILE_NAME_PATTERN, restDirectory);
        Files.write(destinationPath, body.getBytes());
        return "multipart/form-data: " + destinationPath;
    }
}
