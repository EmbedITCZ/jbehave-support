package org.jbehavesupport.core.healthcheck;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Row;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * This class contains a step for checking the health of tested components.
 * The health-check is performed by bean implementing interface {@link HealthCheck}
 * and with configured qualifier.
 * <p>
 * Example configuration:
 * <pre>
 * &#064;Configuration
 * public class MyTestConfiguration {
 *
 *     &#064;Bean
 *     &#064;Qualifier("MYAPP")
 *     public HealthCheck myAppHealthCheck() {
 *         return HealthChecks.http(url, username, password);
 *     }
 *
 * }
 * </pre>
 * <p>
 * Example usage:
 * <pre>
 * Given these components are healthy:
 * | component |
 * | MYAPP     |
 * </pre>
 */
@Component
@RequiredArgsConstructor
public final class HealthCheckSteps {

    private final ConfigurableListableBeanFactory beanFactory;

    @Given("these components are healthy:$componentList")
    public void checkComponentsAreHealthy(ExamplesTable componentList) {
        List<String> componentQualifiers = componentList.getRowsAsParameters().stream()
            .map(Row::values)
            .map(row -> row.get("component"))
            .toList();

        SoftAssertions softly = new SoftAssertions();
        for (String componentQualifier : componentQualifiers) {
            HealthCheck healthCheck = resolveHealthCheck(componentQualifier);
            try {
                healthCheck.check();
            } catch (Exception e) {
                softly.fail("Component " + componentQualifier + " is not healthy. " + e.getMessage(), e);
            }
        }

        softly.assertAll();
    }

    private HealthCheck resolveHealthCheck(String qualifier) {
        try {
            return BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, HealthCheck.class, qualifier);
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalArgumentException("HealthCheckSteps requires single HealthCheck bean with qualifier [" + qualifier + "]", e);
        }
    }

}
