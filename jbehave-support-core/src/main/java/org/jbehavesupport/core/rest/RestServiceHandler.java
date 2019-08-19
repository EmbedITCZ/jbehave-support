package org.jbehavesupport.core.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Row;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.internal.ExampleTableConstraints;
import org.jbehavesupport.core.internal.ExamplesTableUtil;
import org.jbehavesupport.core.internal.MetadataUtil;
import org.jbehavesupport.core.internal.SkipSslVerificationHttpRequestFactory;
import org.jbehavesupport.core.internal.verification.EqualsVerifier;
import org.jbehavesupport.core.report.extension.RestXmlReporterExtension;
import org.jbehavesupport.core.verification.Verifier;
import org.jbehavesupport.core.verification.VerifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.ALIAS;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.DATA;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.EXPECTED_VALUE;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.NAME;
import static org.jbehavesupport.core.internal.ExamplesTableUtil.getValue;
import static org.springframework.util.Assert.state;

/**
 * This class implements steps for testing REST API and provides customization
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
 *     public RestServiceHandler myAppRestServiceHandler() {
 *         return new RestServiceHandler("http://myapp");
 *     }
 *
 * }
 */
@Slf4j
public class RestServiceHandler {

    private static final String REST_RESPONSE_CODE = "rest_response_code";
    private static final String REST_RESPONSE_JSON = "rest_response_json";
    private static final String REST_RESPONSE_HEADERS = "rest_response_headers";
    private static final String HEADER_START = "@header.";
    private static final String RAW_BODY_KEY = "@body";
    private static final String STATUS_HEADER = HEADER_START + "Status";
    private static Pattern indexedKeyPattern = Pattern.compile("(.*)\\[(\\d+)\\]");
    private static Pattern indexedKeyPattern2 = Pattern.compile("^\\[(\\d+)\\]\\.(.*)");
    private static final String PERIOD_REGEX = "\\.(\\d+)(\\.)?";

    private String url;

    @Autowired
    private TestContext testContext;

    @Autowired(required = false)
    private RestXmlReporterExtension restXmlReporterExtension;

    @Autowired
    private VerifierResolver verifierResolver;

    @Autowired
    private EqualsVerifier equalsVerifier;

    protected final RestTemplate template = new RestTemplate();
    protected final RestTemplateConfigurer templateConfigurer = new RestTemplateConfigurer(template);

    public RestServiceHandler(String url) {
        this.url = url;
    }

    @PostConstruct
    private void init() {
        initTemplateInternal();
        initTemplate(templateConfigurer);
    }

    private void initTemplateInternal() {
        BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory = new BufferingClientHttpRequestFactory(
            new SkipSslVerificationHttpRequestFactory());
        template.setRequestFactory(bufferingClientHttpRequestFactory);
        if (restXmlReporterExtension != null) {
            template.getInterceptors().add(restXmlReporterExtension);
        }
    }

    @SuppressWarnings("squid:S1166")
    public void sendRequest(String urlPath, HttpMethod requestMethod, ExamplesTable data) throws IOException {
        URL apiUrl = new URL(this.url + urlPath);
        HttpEntity requestEntity = createRequestEntity(convertCollectionNotation(data));
        try {
            ResponseEntity<String> responseEntity = template.exchange(apiUrl.toString(), requestMethod, requestEntity, String.class);
            storeResponse(responseEntity);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            storeResponse(e);
        }
    }

    /**
     * Each response that is meant to be successful must match data
     *
     * @return ExamplesTable that will be compared against response in same format
     * as {@link RestServiceSteps#verifyResponse(java.lang.String, java.lang.String, org.jbehave.core.model.ExamplesTable)}
     */
    public ExamplesTable getSuccessResult() {
        return new ExamplesTable("");
    }

    private void storeResponse(final ResponseEntity<String> responseEntity) {
        testContext.put(REST_RESPONSE_CODE, responseEntity.getStatusCode().value());
        testContext.put(REST_RESPONSE_HEADERS, responseEntity.getHeaders());
        testContext.put(REST_RESPONSE_JSON, responseEntity.getBody());
    }

    private void storeResponse(final HttpStatusCodeException e) {
        testContext.put(REST_RESPONSE_CODE, e.getStatusCode().value());
        testContext.put(REST_RESPONSE_HEADERS, e.getResponseHeaders());
        testContext.put(REST_RESPONSE_JSON, e.getResponseBodyAsString());
    }

    private HttpEntity createRequestEntity(final ExamplesTable data) throws IOException {
        if (data == null) { //return dummy
            return new HttpEntity<>("", null);
        }
        List<Triple<String, Object, String>> requestDataList = ExamplesTableUtil.convertTriple(data, NAME, DATA, ALIAS);
        HttpHeaders headers = createHeaders(requestDataList);

        boolean isRaw = ExamplesTableUtil.tableContains(data, NAME, RAW_BODY_KEY::equals);
        if (MediaType.MULTIPART_FORM_DATA.equals(headers.getContentType())) {
            state(!isRaw, "multipart request can not use raw data");
            return createMultipartRequestEntity(requestDataList, headers);
        } else if (isRaw) {

            return createRawBodyRequestEntity(requestDataList, headers);
        }
        return createJsonRequestEntity(requestDataList, headers);
    }

    private HttpHeaders createHeaders(List<Triple<String, Object, String>> requestDataList) {
        HttpHeaders headers = new HttpHeaders();
        for (Iterator<Triple<String, Object, String>> iterator = requestDataList.iterator(); iterator.hasNext(); ) {
            Triple<String, Object, String> line = iterator.next();
            String key = line.getLeft();
            String value = String.valueOf(line.getMiddle());
            handleContextAlias(line.getRight(), value);

            if (key.startsWith(HEADER_START)) {
                headers.put(StringUtils.remove(key, HEADER_START), Collections.singletonList(value));
                iterator.remove();
            }
        }
        setHeadersContentTypeIfNull(headers, MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpEntity<String> createRawBodyRequestEntity(final List<Triple<String, Object, String>> requestDataList, HttpHeaders headers) {
        Triple<String, Object, String> line = requestDataList.remove(0);
        String rawBody = String.valueOf(line.getMiddle());
        handleContextAlias(line.getRight(), rawBody);
        if (!requestDataList.isEmpty()) {
            throw new IllegalStateException("If " + RAW_BODY_KEY + " is present, no other keys except headers are allowed.");
        }
        return new HttpEntity<>(rawBody, headers);
    }

    private HttpEntity<MultiValueMap<String, Object>> createMultipartRequestEntity(final List<Triple<String, Object, String>> requestDataList,
                                                                                   HttpHeaders headers) {
        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();

        for (Iterator<Triple<String, Object, String>> iterator = requestDataList.iterator(); iterator.hasNext(); ) {
            Triple<String, Object, String> line = iterator.next();
            String value = String.valueOf(line.getMiddle());
            handleContextAlias(line.getRight(), value);
            if (testContext.isReferenceKey(value)) {
                Object data = testContext.get(value);
                if (data instanceof Resource) {
                    multipartRequest.add(line.getLeft(), data);
                } else if (data instanceof byte[]) {
                    multipartRequest.add(line.getLeft(), new ByteArrayResource((byte[]) data));
                }
            } else {
                multipartRequest.add(line.getLeft(), value);
            }
            iterator.remove();
        }
        return new HttpEntity<>(multipartRequest, headers);
    }

    private HttpEntity<String> createJsonRequestEntity(final List<Triple<String, Object, String>> requestDataList, HttpHeaders headers) throws IOException {
        Map<String, Object> requestEntityMap = new HashMap<>();

        for (Triple<String, Object, String> line : requestDataList) {
            Object value = line.getMiddle();
            String stringValue = String.valueOf(value);
            if (testContext.isReferenceKey(stringValue)) {
                Object data = testContext.get(stringValue);
                if (data instanceof Resource) {
                    byte[] bytes = IOUtils.toByteArray(((Resource) data).getInputStream());
                    stringValue = new String(bytes);
                } else if (data instanceof byte[]) {
                    stringValue = new String((byte[]) data);
                }
            }
            handleContextAlias(line.getRight(), stringValue);
            requestEntityMap.put(line.getLeft(), value);
        }
        return new HttpEntity<>(createJsonRequest(requestEntityMap), headers);
    }

    private void setHeadersContentTypeIfNull(HttpHeaders headers, MediaType mediaType) {
        if (headers.getContentType() == null) {
            headers.setContentType(mediaType);
        }
    }

    private void handleContextAlias(final String rightColumn, final String value) {
        if (StringUtils.isNotEmpty(rightColumn)) {
            testContext.put(rightColumn, value, MetadataUtil.userDefined());
        }
    }

    private String createJsonRequest(Map<String, Object> data) throws IOException {
        List<Map<String, Object>> requestListOfMaps = new ArrayList<>();
        boolean isCollection = false;
        List<String> orderedKeys = data.keySet().stream().sorted(new IndexedKeyComparator()).collect(Collectors.toList());
        for (String key : orderedKeys) {
            Integer index = 0;
            Object value = data.get(key);
            Matcher matcher = indexedKeyPattern2.matcher(key);
            if (matcher.matches()) {
                isCollection = true;
                index = Integer.valueOf(matcher.group(1));
                key = matcher.group(2);
            }

            Map<String, Object> requestMap;
            if (requestListOfMaps.size() > index) {
                requestMap = requestListOfMaps.get(index);
            } else {
                requestMap = new HashMap<>();
                requestListOfMaps.add(index, requestMap);
            }

            addEntryToMap(requestMap, key, value);
        }

        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        if (isCollection) {
            json = mapper.writeValueAsString(requestListOfMaps);
        } else if (requestListOfMaps.size() == 1) {
            json = mapper.writeValueAsString(requestListOfMaps.get(0));
        }
        return json;
    }

    private class IndexedKeyComparator implements Comparator<String> {
        @Override
        public int compare(final String o1, final String o2) {
            Pattern p = Pattern.compile("([^\\[\\]]*)\\[(\\d+)\\](.*)");
            Matcher o1Matcher = p.matcher(o1);
            Matcher o2Matcher = p.matcher(o2);

            if (!o1Matcher.matches() || !o2Matcher.matches()) {
                return o1.compareTo(o2);
            }

            int grpNumber = 1;
            while (true) {
                int result = o1Matcher.group(grpNumber).compareTo(o2Matcher.group(grpNumber));
                if (result != 0) {
                    return result;
                }
                int o1Index = Integer.valueOf(o1Matcher.group(grpNumber + 1));
                int o2Index = Integer.valueOf(o2Matcher.group(grpNumber + 1));
                result = o1Index - o2Index;
                if (result != 0) {
                    return result;
                } else {
                    grpNumber = grpNumber + 2;
                }
            }
        }
    }

    private void addEntryToMap(Map<String, Object> requestMap, String key, Object value) {
        requestMap.keySet().forEach(k -> requestMap.put(k, requestMap.get(k)));
        if (key.contains(".")) { // nested element
            String k1 = key.substring(0, key.indexOf('.'));
            String k2 = key.substring(key.indexOf('.') + 1);
            int index = -1;
            Matcher matcher = indexedKeyPattern.matcher(k1);
            if (matcher.matches()) {
                k1 = matcher.group(1);
                index = Integer.valueOf(matcher.group(2));
            }

            Map<String, Object> map;
            if (!requestMap.containsKey(k1)) {
                map = new HashMap<>();
            } else {
                if (index > -1) {
                    if (((List<Map<String, Object>>) requestMap.get(k1)).size() > index) {
                        map = ((List<Map<String, Object>>) requestMap.get(k1)).get(index);
                    } else {
                        map = new HashMap<>();
                    }
                } else {
                    map = (Map<String, Object>) requestMap.get(k1);
                }
            }

            addEntryToMap(map, k2, value);
            if (index > -1) {
                List<Map<String, Object>> list;
                if (requestMap.containsKey(k1)) {
                    list = (List<Map<String, Object>>) requestMap.get(k1);
                } else {
                    list = new ArrayList<>();
                }
                if (list.size() > index) {
                    list.set(index, map);
                } else {
                    list.add(map);
                }
                requestMap.put(k1, list);
            } else {
                requestMap.put(k1, map);
            }
        } else if (key.matches(".*\\[\\d+\\].*")) { // not nested, collection element
            Matcher matcher = indexedKeyPattern.matcher(key);
            if (matcher.matches()) {
                String k1 = matcher.group(1);
                int index = Integer.valueOf(matcher.group(2));
                List list;
                if (requestMap.containsKey(k1)) {
                    list = (List) requestMap.get(k1);
                } else {
                    list = new ArrayList<>();
                }
                if (list.size() > index) {
                    list.set(index, value);
                } else {
                    list.add(value);
                }
                requestMap.put(k1, list);
            }
        } else {
            requestMap.put(key, value);
        }
    }

    public void saveResponse(ExamplesTable mapping) {
        String response = testContext.get(REST_RESPONSE_JSON).toString();
        HttpHeaders headers = testContext.get(REST_RESPONSE_HEADERS);
        DocumentContext jsonContext = JsonPath.parse(response);
        Consumer<Map<String, String>> rowConsumer = (row) -> {
            String propertyName = row.get(NAME);
            String alias = row.get(ALIAS);
            Object val;
            if (propertyName.startsWith(HEADER_START)) {
                val = headers.get(propertyName.substring(HEADER_START.length())).get(0);
            } else {
                val = jsonContext.read("$." + propertyName);
            }
            testContext.put(alias, val, MetadataUtil.userDefined());
        };

        mapping.getRowsAsParameters()
            .stream()
            .map(Row::values)
            .forEach(rowConsumer);
    }

    private void verifyResponseStatus(HttpStatus actualStatus, HttpStatus expectedStatus, String actualResponseMessage) {
        String assertionErrorMessage =
            String.format("Expected response code is %s but was %s", expectedStatus.name(), actualStatus.name()) + "\n" + actualResponseMessage;
        assertThat(actualStatus).as(assertionErrorMessage).isEqualTo(expectedStatus);
    }

    private void verifyResponseHeaders(HttpHeaders actualHeaders, ExamplesTable data, String actualResponseMessage) {
        String usedOperator = data.getHeaders().contains(ExampleTableConstraints.VERIFIER) ? ExampleTableConstraints.VERIFIER : ExampleTableConstraints.OPERATOR;
        List<Triple<String, Object, String>> expectedData = ExamplesTableUtil.convertTriple(data, NAME, EXPECTED_VALUE, usedOperator);
        for (Triple<String, Object, String> triple : expectedData) {
            String key = triple.getLeft();
            if (key.startsWith(HEADER_START) && !key.equals(STATUS_HEADER)) {
                String headerKey = key.substring(HEADER_START.length());
                String assertionErrorMessage = "Headers don't contain " + headerKey + "\n" + actualResponseMessage;
                assertThat(actualHeaders.containsKey(headerKey)).as(assertionErrorMessage).isTrue();

                final Verifier verifier = verifierResolver.getVerifierByName(triple.getRight(), equalsVerifier);
                verifier.verify(actualHeaders.get(headerKey).get(0), triple.getMiddle());
            }
        }
    }

    private void verifyResponseJson(String response, ExamplesTable expectedDataTable, String actualResponseMessage) {
        String usedOperator = expectedDataTable.getHeaders().contains(ExampleTableConstraints.VERIFIER) ? ExampleTableConstraints.VERIFIER : ExampleTableConstraints.OPERATOR;
        List<Triple<String, Object, String>> expectedData =
            ExamplesTableUtil.convertTriple(expectedDataTable, NAME, EXPECTED_VALUE, usedOperator)
                .stream()
                .filter(i -> !i.getLeft().startsWith(HEADER_START))
                .collect(Collectors.toList());

        verifyJson(response, expectedData, actualResponseMessage);
    }

    public void verifyResponse(String expectedStatus) {
        HttpStatus actualResponseStatus = HttpStatus.valueOf((Integer) testContext.get(REST_RESPONSE_CODE));
        HttpStatus expectedResponseStatus = parseHttpStatus(expectedStatus);
        HttpHeaders actualHeaders = testContext.get(REST_RESPONSE_HEADERS);
        String actualResponseMessage = createResponseAssertionErrorMessage(actualResponseStatus, actualHeaders, null);
        verifyResponseStatus(actualResponseStatus, expectedResponseStatus, actualResponseMessage);
    }

    public void verifyResponse(String expectedStatus, ExamplesTable data) {
        ExamplesTable convertedData = convertCollectionNotation(data);
        HttpStatus actualResponseStatus = HttpStatus.valueOf((Integer) testContext.get(REST_RESPONSE_CODE));
        HttpHeaders actualHeaders = testContext.get(REST_RESPONSE_HEADERS);
        String actualResponseBody = testContext.get(REST_RESPONSE_JSON).toString();
        String actualResponseMessage = createResponseAssertionErrorMessage(actualResponseStatus, actualHeaders, actualResponseBody);

        if (!StringUtils.isEmpty(expectedStatus)) {
            verifyResponseStatus(actualResponseStatus, parseHttpStatus(expectedStatus), actualResponseMessage);
        }
        if (ExamplesTableUtil.tableContains(convertedData, NAME, STATUS_HEADER::equals)) {
            verifyResponseStatus(actualResponseStatus, parseHttpStatus(getValue(convertedData, NAME, STATUS_HEADER, EXPECTED_VALUE)), actualResponseMessage);
        }

        verifyResponseHeaders(actualHeaders, convertedData, actualResponseMessage);
        verifyResponseJson(actualResponseBody, convertedData, actualResponseMessage);
    }

    private ExamplesTable convertCollectionNotation(ExamplesTable data) {
        if (data != null && !data.getRows().isEmpty()) {
            List<Map<String, String>> newMapList = data.getRows().stream()
                .map(this::convertIfNeeded)
                .collect(Collectors.toList());
            return data.withRows(newMapList);
        } else {
            return data;
        }
    }

    private Map<String, String> convertIfNeeded(Map<String, String> map) {
        String name = map.get(ExampleTableConstraints.NAME);
        String newName = name.replaceAll(PERIOD_REGEX, "\\[$1\\]$2");
        map.put(ExampleTableConstraints.NAME, newName);
        return map;
    }

    private HttpStatus parseHttpStatus(final String status) {
        HttpStatus expectedStatus;
        if (status.matches("\\d+")) {
            expectedStatus = HttpStatus.valueOf(Integer.valueOf(status));
        } else {
            expectedStatus = HttpStatus.valueOf(status);
        }
        return expectedStatus;
    }

    private String createResponseAssertionErrorMessage(HttpStatus actualResponseStatus, HttpHeaders actualHeaders, String actualResponseBody) {
        String message = "Actual response:\n";
        if (actualResponseStatus != null) {
            message = message + "status: " + actualResponseStatus + "\n";
        }
        if (actualHeaders != null) {
            message = message + "headers: " + actualHeaders + "\n";
        }
        if (actualResponseBody != null) {
            message = message + "body: " + actualResponseBody;
        }
        return message;
    }

    private void verifyJson(String json, List<Triple<String, Object, String>> expectedData, String actualResponseMessage) {
        assertThat(json).as(actualResponseMessage).isNotEmpty();
        DocumentContext jsonContext = JsonPath.parse(json);
        for (Triple<String, Object, String> data : expectedData) {
            String propertyName = data.getLeft();
            Object expectedValue = data.getMiddle();

            Object actualValue = jsonContext.read("$." + propertyName);
            verifierResolver.getVerifierByName(data.getRight(), equalsVerifier)
                .verify(actualValue, expectedValue);
        }
    }

    /**
     * Custom initialization of {@link RestTemplate}.
     * Usually we are passing to configurer authentication details, header builder.
     *
     * <pre>{@code
     *      templateConfigurer
     *          .basicAuthorization("username", "password")
     *          .header(this::headers);
     * }</pre>
     *
     * @param templateConfigurer
     */
    protected void initTemplate(RestTemplateConfigurer templateConfigurer) {
        // noop
    }
}
