package org.jbehavesupport.core.ws;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.Value;

/**
 * This class holds list of configured web service endpoints for some application,
 * it is used by {@link WebServiceHandler}.
 * <p>
 * Example:
 * <pre>
 * public class MyAppWebServiceHandler extends WebServiceHandler {
 *
 *     &#064;Override
 *     protected void initEndpoints(WebServiceEndpointRegistry registry) {
 *         registry.register(MyRequest.class, MyResponse.class)
 *                 .register(MyOtherRequest.class, "OtherRequest", MyOtherResponse.class, "OtherResponse");
 *     }
 *
 * }
 * </pre>
 */
public class WebServiceEndpointRegistry {

    private final List<Endpoint> endpoints = new ArrayList<>();

    /**
     * Register endpoint for request and response class.
     *
     * @param requestClass  the request class
     * @param responseClass the response class
     * @return the web service endpoint registry
     */
    public final WebServiceEndpointRegistry register(Class requestClass, Class responseClass) {
        return register(requestClass, null, responseClass, null);
    }

    /**
     * Register web service endpoint registry.
     * Request and response classes can have aliases. So in case of same class name from different packages we are
     * able to distinguish between them.
     *
     * @param requestClass  the request class
     * @param requestAlias  the request alias
     * @param responseClass the response class
     * @param responseAlias the response alias
     * @return the web service endpoint registry
     */
    public final WebServiceEndpointRegistry register(Class requestClass, String requestAlias, Class responseClass, String responseAlias) {
        endpoints.add(new Endpoint(requestClass, requestAlias, responseClass, responseAlias));
        return this;
    }

    public final void validateRequest(String request) {
        if (endpoints.stream().noneMatch(e -> e.getRequestName().equals(request))) {
            throw new IllegalArgumentException("Unknown request name " + request + ", only following request names are supported: " + endpoints.stream()
                .map(Endpoint::getRequestName)
                .collect(joining(", ")));
        }
    }

    public final void validateResponse(String response) {
        if (endpoints.stream().noneMatch(e -> e.getResponseName().equals(response))) {
            throw new IllegalArgumentException("Unknown response name " + response + ", only following response names are supported: " + endpoints.stream()
                .map(Endpoint::getResponseName)
                .collect(joining(", ")));
        }
    }

    public final void validateRequestOrResponse(String requestOrResponse) {
        if (resolveEndpoint(requestOrResponse) == null) {
            throw new IllegalArgumentException(
                "Unknown request or response name " + requestOrResponse + ", only following names are supported: " + endpoints.stream().flatMap(e -> Stream
                    .of(e.getRequestName(), e.getResponseName())).collect(joining(", ")));
        }
    }

    public final Endpoint resolveEndpoint(String requestOrResponse) {
        return endpoints.stream()
            .filter(e -> e.getRequestName().equals(requestOrResponse) || e.getResponseName().equals(requestOrResponse))
            .findFirst()
            .orElse(null);
    }

    public final Class[] endpointsClasses() {
        return endpoints.stream()
            .flatMap(e -> Stream.of(e.getRequestClass(), e.getResponseClass()))
            .toArray(Class[]::new);
    }

    @Value
    public static class Endpoint {

        @NonNull
        Class requestClass;
        String requestAlias;
        @NonNull
        Class responseClass;
        String responseAlias;

        public String getRequestName() {
            return nonNull(requestAlias) ? requestAlias : requestClass.getSimpleName();
        }

        public String getResponseName() {
            return nonNull(responseAlias) ? responseAlias : responseClass.getSimpleName();
        }

    }

}
