package org.jbehavesupport.core.ws;

import lombok.Data;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Parameters;
import org.jbehave.core.steps.Row;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.internal.ExampleTableConstraints;
import org.jbehavesupport.core.internal.MetadataUtil;
import org.jbehavesupport.core.internal.ReflectionUtils;
import org.jbehavesupport.core.internal.verification.EqualsVerifier;
import org.jbehavesupport.core.support.RequestFactory;
import org.jbehavesupport.core.support.TestContextUtil;
import org.jbehavesupport.core.verification.Verifier;
import org.jbehavesupport.core.verification.VerifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.ws.FaultAwareWebServiceMessage;
import org.springframework.ws.client.core.FaultMessageResolver;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.Assert.assertNotNull;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.state;

/**
 * This class implements steps for testing web services and provides customization
 * points for concrete applications. The test configuration has to provide this
 * bean with application qualifier.
 * <p>
 * Example:
 * <pre>
 * &#064;Configuration
 * public class MyTestConfiguration {
 *
 *     &#064;Bean
 *     &#064;Qualifier("MYAPP")
 *     public MyAppWebServiceHandler myAppWebServiceHandler() {
 *         return new MyAppWebServiceHandler();
 *     }
 *
 * }
 *
 * public class MyAppWebServiceHandler extends WebServiceHandler {
 *
 *     // application specific configuration/customization
 *
 * }
 * </pre>
 */
public abstract class WebServiceHandler {

    private static final String CONTEXT_SEPARATOR = ".";
    private static final String REQUEST_POSTFIX = "Request";

    @Autowired
    protected TestContext testContext;

    @Autowired(required = false)
    private ClientInterceptor[] interceptors;

    @Autowired(required = false)
    private ConversionService conversionService;

    @Autowired
    private VerifierResolver verifierResolver;

    @Autowired
    private EqualsVerifier equalsVerifier;

    protected final WebServiceTemplate template = new WebServiceTemplate();
    protected final WebServiceTemplateConfigurer templateConfigurer = new WebServiceTemplateConfigurer(template);
    protected final WebServiceEndpointRegistry endpointRegistry = new WebServiceEndpointRegistry();

    @PostConstruct
    public final void init() {
        initEndpoints(endpointRegistry);
        initTemplateInternal();
        initTemplate(templateConfigurer);
    }

    private void initTemplateInternal() {
        template.setInterceptors(interceptors);
        templateConfigurer.classesToBeBound(endpointRegistry.endpointsClasses());
    }

    public final void setRequestData(String request, ExamplesTable data) {
        testContext.clear(key -> key.startsWith(request));
        overrideRequestData(request, data);
    }

    public final void overrideRequestData(String request, ExamplesTable data) {
        endpointRegistry.validateRequest(request);
        TestContextUtil.putDataIntoContext(testContext, data, request);
    }

    public final void requestIsSent(String request) {
        endpointRegistry.validateRequest(request);
        WebServiceEndpointRegistry.Endpoint endpoint = endpointRegistry.resolveEndpoint(request);
        Object requestObj = createRequest(endpoint.getRequestClass(), endpoint.getRequestAlias());
        Object responseObj = sendRequest(requestObj);
        testContext.put(endpoint.getResponseName(), responseObj);
    }

    public final void requestIsSentWithSuccess(String request) {
        WebServiceEndpointRegistry.Endpoint endpoint = endpointRegistry.resolveEndpoint(request);
        requestIsSent(request);
        responseResultIsSuccess(endpoint.getResponseName());
    }

    public final void requestIsSentWithFault(String request, ExamplesTable expectedFault) {
        endpointRegistry.validateRequest(request);
        WebServiceEndpointRegistry.Endpoint endpoint = endpointRegistry.resolveEndpoint(request);
        Object requestObj = createRequest(endpoint.getRequestClass(), endpoint.getRequestAlias());

        FaultData faultData = new FaultData();
        FaultMessageResolver originalFaultMessageResolver = template.getFaultMessageResolver();
        try {
            template.setFaultMessageResolver(msg -> {
                FaultAwareWebServiceMessage faultMsg = (FaultAwareWebServiceMessage) msg;
                faultData.setFaultCode(faultMsg.getFaultCode().getLocalPart());
                faultData.setFaultReason(faultMsg.getFaultReason());
            });
            sendRequest(requestObj);
        } finally {
            template.setFaultMessageResolver(originalFaultMessageResolver);
        }

        state(faultData.getFaultCode() != null, "Fault response was expected, but the web service call finished with success.");
        verifyProperties(faultData, expectedFault);
    }

    public final void responseResultIsSuccess(String response) {
        endpointRegistry.validateResponse(response);
        String successResult = getSuccessResult();
        responseResultIs(response, successResult == null ? null : new ExamplesTable(successResult)); //todo: potential issue when commands that is being handled with parameterConverter is used inside ExTa.
    }

    public final void responseResultIs(String response, ExamplesTable expectedResults) {
        endpointRegistry.validateResponse(response);
        Object responseObj = testContext.get(response);
        verifyResults(responseObj, expectedResults);
    }

    public final void responseValuesMatch(String response, ExamplesTable expectedValues) {
        endpointRegistry.validateResponse(response);
        verifyProperties(testContext.get(response), expectedValues);
    }

    public final void storeDataInContext(String requestOrResponse, ExamplesTable mapping) {
        endpointRegistry.validateRequestOrResponse(requestOrResponse);

        Consumer<Map<String, String>> rowConsumer;
        if (requestOrResponse.endsWith(REQUEST_POSTFIX)) {
            rowConsumer = row -> {
                String propertyName = row.get(ExampleTableConstraints.NAME);
                String alias = row.get(ExampleTableConstraints.ALIAS);
                Object val = testContext.get(String.join(CONTEXT_SEPARATOR, requestOrResponse, propertyName));
                testContext.put(alias, val, MetadataUtil.userDefined());
            };
        } else {
            Object response = testContext.get(requestOrResponse);
            rowConsumer = row -> {
                String propertyName = row.get(ExampleTableConstraints.NAME);
                String alias = row.get(ExampleTableConstraints.ALIAS);
                Object val = ReflectionUtils.getPropertyValue(response, propertyName);
                testContext.put(alias, val, MetadataUtil.userDefined());
            };
        }

        mapping.getRowsAsParameters()
            .stream()
            .map(Row::values)
            .forEach(rowConsumer);
    }

    /**
     * Custom initialization of {@link WebServiceEndpointRegistry}.
     * Is dedicated to register pair request - response.
     *
     * <pre>{@code
     *      endpointRegistry.registry(Request.class, Response.class)
     *          .registry...
     * }</pre>
     *
     * @param endpointRegistry
     */
    protected abstract void initEndpoints(WebServiceEndpointRegistry endpointRegistry);

    /**
     * Custom initialization of {@link WebServiceTemplate}.
     * Usually we are passing to configurer url, authentication details, header builder.
     *
     * <pre>{@code
     *      templateConfigurer
     *          .defaultUri("http://localhost:8080/services")
     *          .authenticatingMessageSender("username", "password")
     *          .header(this::getHeader);
     * }</pre>
     *
     * @param templateConfigurer
     */
    protected void initTemplate(WebServiceTemplateConfigurer templateConfigurer) {
        // noop
    }

    /**
     * Custom initialization of {@link RequestFactory}.
     * <p>
     * e.g change default bean access strategy to field access strategy;
     * <pre>{@code
     *      requestFactory.withFieldAccessStrategy();
     * }</pre>
     * <p>
     * or you can register custom handler for request modification
     * <pre>{@code
     *      requestFactory
     *          .handler...
     * }</pre>
     *
     * @param requestFactory
     */
    protected void initRequestFactory(RequestFactory requestFactory) {
        // noop
    }

    /**
     * By overriding this method you can define return string with success example table.
     * e.g.
     * <pre>{@code
     *      return "|code|description|\n" +
     *             "|0   |Successful |";
     * }</pre>
     *
     * @return
     */
    protected String getSuccessResult() {
        return null;
    }

    /**
     * Verify response object and as expected result example table is used.
     * Method is internally called by API method {@link WebServiceHandler#responseResultIs}.
     *
     * @param response
     * @param expectedResults
     */
    protected void verifyResults(Object response, ExamplesTable expectedResults) {
        assertNotNull("Response can't be null", response);
    }

    /**
     * Once you will override this method you can create request object manually.
     *
     * @param requestClass
     * @return
     */
    protected Object createRequest(Class requestClass, String alias) {
        RequestFactory requestFactory = new RequestFactory(requestClass, testContext, conversionService).prefix(alias);
        initRequestFactory(requestFactory);
        return requestFactory.createRequest();
    }

    /**
     * By overriding this method you can modify default send request and receive response behaviour.
     *
     * @param request
     * @return
     */
    protected Object sendRequest(Object request) {
        return template.marshalSendAndReceive(request);
    }

    @Data
    public static class FaultData {
        String faultCode;
        String faultReason;
    }

    private void verifyProperties(Object bean, ExamplesTable elementsMapping) {
        isTrue(elementsMapping.getHeaders().contains(ExampleTableConstraints.NAME), "Example table must contain column: " + ExampleTableConstraints.NAME);
        isTrue(elementsMapping.getHeaders().contains(
            ExampleTableConstraints.EXPECTED_VALUE), "Example table must contain column: " + ExampleTableConstraints.EXPECTED_VALUE);

        elementsMapping.getRowsAsParameters()
            .forEach(p -> {
                String propertyName = p.valueAs(ExampleTableConstraints.NAME, String.class);
                String expectedValue = p.valueAs(ExampleTableConstraints.EXPECTED_VALUE, String.class);
                Object actualValue = ReflectionUtils.getPropertyValue(bean, propertyName);
                getVerifier(p).verify(actualValue, expectedValue);
            });
    }

    private Verifier getVerifier(Parameters parameters) {
        String verifierName = parameters.values().entrySet().stream()
            .filter(e -> e.getKey().equals(ExampleTableConstraints.OPERATOR) || e.getKey().equals(ExampleTableConstraints.VERIFIER))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(null);

        return verifierResolver.getVerifierByName(verifierName, equalsVerifier);
    }

}
