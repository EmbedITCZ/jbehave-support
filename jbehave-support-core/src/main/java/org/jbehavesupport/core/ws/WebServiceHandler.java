package org.jbehavesupport.core.ws;

import lombok.Data;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.ConvertedParameters;
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

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.jbehavesupport.core.internal.ExampleTableConstraints.NAME;
import static org.jbehavesupport.core.internal.ExamplesTableUtil.assertDuplicatesInColumns;
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
    public static final String BRACKET_REGEX = "(.*)\\[(\\d+)\\](.*)";
    private static final String SLASH_PREFIX = "/";
    public static final String NAMESPACE_AWARE = "NAMESPACE_AWARE";

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
        ExamplesTable convertedData = convertCollectionNotation(data);
        assertDuplicatesInColumns(data, NAME);
        overrideRequestData(request, convertedData);
    }

    public final void overrideRequestData(String request, ExamplesTable data) {
        endpointRegistry.validateRequest(request);
        TestContextUtil.putDataIntoContext(testContext, data, request);
    }

    private ExamplesTable convertCollectionNotation(ExamplesTable data) {
        List<Map<String, String>> newMapList = data.getRows().stream()
            .map(map -> {
                String name = map.get(ExampleTableConstraints.NAME);
                if (name.matches(BRACKET_REGEX) && !name.startsWith(SLASH_PREFIX)) {
                    String newName = name.replace("[", ".").replace("]", "");
                    map.put(ExampleTableConstraints.NAME, newName);
                }
                return map;
            })
            .collect(Collectors.toList());
        return data.withRows(newMapList);
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
        ExamplesTable convertedValues = convertCollectionNotation(expectedValues);
        verifyProperties(testContext.get(response), convertedValues);
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
            final Document[] xmlResponse = { null };
            final Document[] xmlResponseWithoutNamesPaces = { null };

            Object response = testContext.get(requestOrResponse);
            rowConsumer = row -> {
                String propertyName = row.get(ExampleTableConstraints.NAME);
                String alias = row.get(ExampleTableConstraints.ALIAS);
                Object value;
                if (propertyName.startsWith(SLASH_PREFIX)){
                    if (NAMESPACE_AWARE.equals(row.get(ExampleTableConstraints.MODE))){
                        value = evaluateRowWithNamespaces(xmlResponse, propertyName, response);
                    } else {
                        value = evaluateRowWithoutNamespaces(xmlResponseWithoutNamesPaces, propertyName, response);
                    }
                } else {
                    value = ReflectionUtils.getPropertyValue(response, propertyName);
                }
                testContext.put(alias, value, MetadataUtil.userDefined());
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

        final Document[] xmlResponseWithoutNamesPaces = { null };
        final Document[] xmlResponse = { null };

        elementsMapping.getRowsAsParameters()
            .forEach(p -> {
                String propertyName = p.valueAs(ExampleTableConstraints.NAME, String.class);
                String expectedValue = p.valueAs(ExampleTableConstraints.EXPECTED_VALUE, String.class);
                Object actualValue;
                if (propertyName.startsWith(SLASH_PREFIX)){
                    if (NAMESPACE_AWARE.equals(getModeParameter(p))){
                        actualValue = evaluateRowWithNamespaces(xmlResponse, propertyName, bean);
                    } else {
                        actualValue = evaluateRowWithoutNamespaces(xmlResponseWithoutNamesPaces, propertyName, bean);
                    }
                } else {
                    actualValue = ReflectionUtils.getPropertyValue(bean, propertyName);
                }
                getVerifier(p).verify(actualValue, expectedValue);
            });
    }

    private String getModeParameter(Parameters row){
        try{
            return row.valueAs(ExampleTableConstraints.MODE, String.class);
        } catch (ConvertedParameters.ValueNotFound e) {
            return null;
        }
    }

    private Object evaluateRowWithNamespaces(Document[] xmlResponse, String propertyName, Object bean){
        if(xmlResponse[0] == null){
            xmlResponse[0] = createXmlResponse(bean);
        }
        return evaluateXpath(xmlResponse[0], propertyName);
    }

    private Object evaluateRowWithoutNamespaces(Document[] xmlResponse, String propertyName, Object bean){
        if(xmlResponse[0] == null){
            xmlResponse[0] = cleanNameSpace(createXmlResponse(bean));
        }
        return evaluateXpath(xmlResponse[0], propertyName);
    }

    private Object evaluateXpath(Document document, String expression){
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            return xpath.compile(expression).evaluate(document, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException("Unable to parse xpath: " + expression);
        }
    }

    private Document createXmlResponse(Object bean){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            JAXBContext context = JAXBContext.newInstance(bean.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(bean, document);
            return document;
        } catch (ParserConfigurationException | JAXBException e){
            throw new IllegalArgumentException("Unable to build DOM document for xpath");
        }
    }

    public Document cleanNameSpace(Document document) {
        NodeList list = document.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            removeNameSpace(list.item(i), "");
        }
        return document;
    }

    private void removeNameSpace(Node node, String nameSpaceURI) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Document ownerDoc = node.getOwnerDocument();
            NamedNodeMap map = node.getAttributes();
            Node n;
            while ((0!=map.getLength())) {
                n = map.item(0);
                map.removeNamedItemNS(n.getNamespaceURI(), n.getLocalName());
            }
            ownerDoc.renameNode(node, nameSpaceURI, node.getLocalName());
        }
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            removeNameSpace(list.item(i), nameSpaceURI);
        }
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
