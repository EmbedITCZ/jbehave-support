package org.jbehavesupport.core.rest;

import java.io.IOException;

import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter;

import lombok.RequiredArgsConstructor;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * This class contains basic set of steps for testing REST API:
 * <p>
 * Steps:
 * <ul>
 * <li>
 * <code>Given [METHOD] request to [APPLICATION]/[URL] is sent</code>
 * </li>
 * <li>
 * <code>Given [METHOD] request to [APPLICATION]/[URL] is sent with data: DATA</code>
 * </li>
 * <li>
 * <code>Then response from [APPLICATION] REST API has status [STATUS]</code>
 * </li>
 * <li>
 * <code>Then response from [APPLICATION] REST API has status [STATUS] and values match: DATA</code>
 * </li>
 * <li>
 * <code>Then response values from [APPLICATION] REST API are saved: MAPPING</code>
 * </li>
 * <p>
 * Parameters:
 * <ul>
 * <li>
 * <code>METHOD</code> - HTTP request methods, eg. <code>GET</code>, <code>POST</code>, ...
 * </li>
 * <li>
 * <code>APPLICATION</code> - application qualifier used to resolve {@link RestServiceHandler},
 * which implements individual steps and where is possible to provide application specific customization
 * </li>
 * <li>
 * <code>URL</code> - the path in URL, eg. <code>user/?order=1</code>
 * </li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public final class RestServiceSteps {

    private final ConfigurableListableBeanFactory beanFactory;

    @Given("[$method] request to [$application]/[$url] is sent")
    @When("[$method] request to [$application]/[$url] is sent")
    public void sendRequest(String method, String application, ExpressionEvaluatingParameter<String> url) throws IOException {
        resolveHandler(application).sendRequest(url.getValue(), HttpMethod.valueOf(method), null);
    }

    @Given("[$method] request to [$application]/[$url] is sent with data: $data")
    @When("[$method] request to [$application]/[$url] is sent with data: $data")
    public void sendRequest(String method, String application, ExpressionEvaluatingParameter<String> url, ExamplesTable data) throws IOException {
        resolveHandler(application).sendRequest(url.getValue(), HttpMethod.valueOf(method), data);
    }

    @When("response from [$application] REST API has status [$status]")
    @Then("response from [$application] REST API has status [$status]")
    public void verifyResponse(String application, String status) {
        resolveHandler(application).verifyResponse(status);
    }

    @When("response from [$application] REST API has status [$status] and values match: $data")
    @Then("response from [$application] REST API has status [$status] and values match: $data")
    public void verifyResponse(String application, String status, ExamplesTable data) {
        resolveHandler(application).verifyResponse(status, data);
    }

    @When("response values from [$application] REST API are saved: $mapping")
    @Then("response values from [$application] REST API are saved: $mapping")
    public void saveResponse(String application, ExamplesTable mapping) {
        resolveHandler(application).saveResponse(mapping);
    }

    private RestServiceHandler resolveHandler(String application) {
        try {
            return BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, RestServiceHandler.class, application);
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalArgumentException("RestServiceSteps requires single RestServiceHandler bean with qualifier [" + application + "]", e);
        }
    }

}
