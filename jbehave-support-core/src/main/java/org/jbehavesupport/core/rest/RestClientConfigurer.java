package org.jbehavesupport.core.rest;

import java.util.Map;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

/**
 * This class provides convenient api for customization of {@link RestClient},
 * it is used by {@link RestServiceHandler}.
 * <p>
 * Example:
 * <pre>
 * public class MyAppRestServiceHandler extends RestServiceHandler {
 *
 *     &#064;Override
 *     protected void initRestClient(RestClientConfigurer restClientConfigurer) {
 *         restClientConfigurer
 *             .basicAuthorization(myUsername, myPassword)
 *             .header(this::headers);
 *     }
 *
 * }
 * </pre>
 * <p>
 * OAuth2 client credentials example:
 * <pre>
 * public class MyAppRestServiceHandler extends RestServiceHandler {
 *
 *     &#064;Override
 *     protected void initRestClient(RestClientConfigurer restClientConfigurer) {
 *         clientConfigurer.oauth2ClientCredentials("my-api");
 *     }
 *
 * }
 * </pre>
 * OAuth2 client must be configured in application properties:
 * <pre>
 * spring.security.oauth2.client.registration.my-api.client-id=xxx
 * spring.security.oauth2.client.registration.my-api.client-secret=yyy
 * spring.security.oauth2.client.registration.my-api.authorization-grant-type=client_credentials
 * spring.security.oauth2.client.provider.my-api.token-uri=https://auth.example.com/token
 * </pre>
 */
public class RestClientConfigurer {

    private final RestClient.Builder builder;
    @Nullable
    private final OAuth2AuthorizedClientManager authorizedClientManager;

    RestClientConfigurer(RestClient.Builder builder, @Nullable OAuth2AuthorizedClientManager authorizedClientManager) {
        this.builder = builder;
        this.authorizedClientManager = authorizedClientManager;
    }

    public final RestClientConfigurer interceptor(ClientHttpRequestInterceptor interceptor) {
        builder.requestInterceptors(list -> list.add(interceptor));
        return this;
    }

    public final RestClientConfigurer header(Supplier<Map<String, String>> headerProvider) {
        return interceptor((req, body, execution) -> {
            headerProvider.get().forEach((k, v) -> req.getHeaders().add(k, v));
            return execution.execute(req, body);
        });
    }

    public final RestClientConfigurer basicAuthorization(String username, String password) {
        return interceptor(new BasicAuthenticationInterceptor(username, password));
    }

    public final RestClientConfigurer bearerToken(String token) {
        return interceptor((req, body, execution) -> {
            req.getHeaders().setBearerAuth(token);
            return execution.execute(req, body);
        });
    }

    public final RestClientConfigurer oauth2ClientCredentials(String registrationId) {
        if (authorizedClientManager == null) {
            throw new IllegalStateException("OAuth2AuthorizedClientManager is not available. Please configure Spring OAuth2 client registration in application properties.");
        }
        var interceptor = new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        interceptor.setClientRegistrationIdResolver(request -> registrationId);
        return interceptor(interceptor);
    }
}
