package org.jbehavesupport.core.rest;

import java.util.Map;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

/**
 * This class provides convenient api for customization of {@link RestTemplate},
 * it is used by {@link RestServiceHandler}.
 * <p>
 * Example:
 * <pre>
 * public class MyAppRestServiceHandler extends RestServiceHandler {
 *
 *     &#064;Override
 *     protected void initTemplate(RestTemplateConfigurer templateConfigurer) {
 *         templateConfigurer
 *             .basicAuthorization(myUsername, myPassword);
 *             .header(this::headers);
 *     }
 *
 * }
 * </pre>
 */
@RequiredArgsConstructor
public class RestTemplateConfigurer {

    private final RestTemplate template;

    public final RestTemplateConfigurer interceptor(ClientHttpRequestInterceptor interceptor) {
        this.template.getInterceptors().add(interceptor);
        return this;
    }

    public final RestTemplateConfigurer header(Supplier<Map<String, String>> headerProvider) {
        ClientHttpRequestInterceptor headerInterceptor = (request, body, execution) -> {
            headerProvider.get()
                .entrySet()
                .forEach(header -> request.getHeaders().add(header.getKey(), header.getValue()));
            return execution.execute(request, body);
        };
        return interceptor(headerInterceptor);
    }

    public final RestTemplateConfigurer basicAuthorization(String username, String password) {
        return interceptor(new BasicAuthenticationInterceptor(username, password));
    }

}
