package org.jbehavesupport.core.ws;

import lombok.RequiredArgsConstructor;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * This class contains basic set of steps for testing web services:
 * <p>
 * Steps:
 * <ul>
 * <li>
 * <code>Given [REQUEST] data for [APPLICATION]: DATA</code>
 * </li>
 * <li>
 * <code>Given [REQUEST] data for [APPLICATION] with overrides: DATA</code>
 * </li>
 * <li>
 * <code>When [REQUEST] is sent to [APPLICATION]</code>
 * </li>
 * <li>
 * <code>When [REQUEST] is sent to [APPLICATION] with success</code>
 * </li>
 * <li>
 * <code>When [REQUEST] is sent to [APPLICATION] with fault</code>
 * </li>
 * <li>
 * <code>Then [RESPONSE] result is success for [APPLICATION]</code>
 * </li>
 * <li>
 * <code>Then [RESPONSE] result from [APPLICATION] is: EXPECTED_RESULT</code>
 * </li>
 * <li>
 * <code>Then [RESPONSE] values from [APPLICATION] match: EXPECTED_VALUES</code>
 * </li>
 * <li>
 * <code>Given/When/Then [REQUEST/RESPONSE] values from [APPLICATION] are saved: MAPPING</code>
 * </li>
 * </ul>
 * <p>
 * Parameters:
 * <ul>
 * <li>
 * <code>REQUEST</code> - simple class name or alias of request object
 * </li>
 * <li>
 * <code>RESPONSE</code> - simple class name or alias of response object
 * </li>
 * <li>
 * <code>APPLICATION</code> - application qualifier used to resolve {@link WebServiceHandler},
 * which implements individual steps and where is possible to provide application specific customization
 * </li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public final class WebServiceSteps {

    private final ConfigurableListableBeanFactory beanFactory;

    @Given("[$request] data for [$application]: $data")
    public void requestData(String request, String application, ExamplesTable data) {
        resolveHandler(application).setRequestData(request, data);
    }

    @Given("previous [$request] data for [$application] with overrides: $data")
    public void overrideRequestData(String request, String application, ExamplesTable data) {
        resolveHandler(application).overrideRequestData(request, data);
    }

    @When("[$request] is sent to [$application]")
    public void requestIsSent(String request, String application) {
        resolveHandler(application).requestIsSent(request);
    }

    @When(value = "[$request] is sent to [$application] with success", priority = 100)
    public void requestIsSentWithSuccess(String request, String application) {
        resolveHandler(application).requestIsSentWithSuccess(request);
    }

    @When(value = "[$request] is sent to [$application] with fault: $expectedFault", priority = 100)
    public void requestIsSentWithFault(String request, String application, ExamplesTable expectedFault) {
        resolveHandler(application).requestIsSentWithFault(request, expectedFault);
    }

    @Then("[$response] result is success for [$application]")
    public void responseResultIsSuccess(String response, String application) {
        resolveHandler(application).responseResultIsSuccess(response);
    }

    @Then("[$response] result from [$application] is: $expectedResults")
    public void responseResultIs(String response, String application, ExamplesTable expectedResults) {
        resolveHandler(application).responseResultIs(response, expectedResults);
    }

    @Then("[$response] values from [$application] match: $expectedValues")
    public void responseValuesMatch(String response, String application, ExamplesTable expectedValues) {
        resolveHandler(application).responseValuesMatch(response, expectedValues);
    }

    @Given("[$requestOrResponse] values from [$application] are saved: $mapping")
    @When("[$requestOrResponse] values from [$application] are saved: $mapping")
    @Then("[$requestOrResponse] values from [$application] are saved: $mapping")
    public void storeDataInContext(String requestOrResponse, String application, ExamplesTable mapping) {
        resolveHandler(application).storeDataInContext(requestOrResponse, mapping);
    }

    private WebServiceHandler resolveHandler(String application) {
        try {
            return BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, WebServiceHandler.class, application);
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalArgumentException("WebServiceSteps requires single WebServiceHandler bean with qualifier [" + application + "]", e);
        }
    }

}
