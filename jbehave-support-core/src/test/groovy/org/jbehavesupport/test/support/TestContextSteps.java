package org.jbehavesupport.test.support;

import static org.jbehavesupport.core.AbstractSpringStories.JBEHAVE_SCENARIO;
import static org.assertj.core.api.Assertions.assertThat;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.springframework.stereotype.Component;

/**
 * Example steps working with {@link TestContext} to show how easy it is to provide new step implementations.
 */
@Component
@RequiredArgsConstructor
public final class TestContextSteps {

    private final TestContext testContext;

    @Then("context contains [$data] under [$key]")
    public void checkData(ExpressionEvaluatingParameter<String> data, String key) {
        assertThat(data.getValue())
            .as("Context must contains [%s] under key [%s]", data.getValue(), key)
            .isEqualTo(testContext.get(key));
    }

    @Given("context is cleared")
    public void clearData() {
        testContext.clear();
    }

    @Then("context is empty")
    public void contextIsEmpty() {
        assertThat(testContext.keySet())
            .as("only one item is expected in test context")
            .hasSize(1);
        assertThat(testContext.contains(JBEHAVE_SCENARIO))
            .as("only item in test context should be " + JBEHAVE_SCENARIO)
            .isTrue();
    }
}
