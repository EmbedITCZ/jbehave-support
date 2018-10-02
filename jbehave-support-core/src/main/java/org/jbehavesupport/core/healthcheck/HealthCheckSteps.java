package org.jbehavesupport.core.healthcheck;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Row;
import org.junit.runners.model.MultipleFailureException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * This class contains step for checking healthy of tested components.
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
public final class HealthCheckSteps {

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    @Given("these components are healthy:$componentList")
    public void checkComponentsAreHealthy(ExamplesTable componentList) throws MultipleFailureException{
        List<String> componentQualifiers = componentList.getRowsAsParameters().stream()
            .map(Row::values)
            .map(row -> row.get("component"))
            .collect(toList());

        List<Throwable> exceptions = new ArrayList<>();
        for (String componentQualifier : componentQualifiers) {
            HealthCheck healthCheck = resolveHealthCheck(componentQualifier);
            try {
                healthCheck.check();
            } catch (Exception e) {
                IllegalStateException ie = new IllegalStateException("Component " + componentQualifier + " is not healthy. " + e.getMessage(), e);
                exceptions.add(ie);
            }
        }

        if (!exceptions.isEmpty()) {
            throw new MultipleFailureException(exceptions);
        }
    }

    private HealthCheck resolveHealthCheck(String qualifier) {
        try {
            return BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, HealthCheck.class, qualifier);
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalArgumentException("HealthCheckSteps requires single HealthCheck bean with qualifier [" + qualifier + "]", e);
        }
    }

}
