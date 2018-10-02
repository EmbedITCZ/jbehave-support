package org.jbehavesupport.core.report.extension;

import java.net.URI;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class RestMessageContext {

    private LocalDateTime requestTimeStamp;
    private LocalDateTime responseTimeStamp;

    private HttpMethod method;
    private URI url;
    private HttpHeaders requestHeaders;
    private String requestJsonBody;

    private HttpStatus responseStatus;
    private HttpHeaders responseHeaders;
    private String responseJsonBody;
}
