package org.jbehavesupport.core.context;

import static org.jbehavesupport.core.internal.MetadataUtil.userDefined;
import static org.jbehavesupport.core.support.TestContextUtil.putDataIntoContext;

import java.util.Properties;

import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter;

import lombok.RequiredArgsConstructor;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.model.ExamplesTable;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Context steps.
 */
@Component
@RequiredArgsConstructor
public final class ContextSteps {

    private final TestContext testContext;

    @BeforeScenario
    public void resetTestContext() {
        testContext.clear();
    }

    @Given("the value [$value] is saved as [$contextAlias]")
    public void saveValue(ExpressionEvaluatingParameter<String> value, String contextAlias) {
        testContext.put(contextAlias, value.getValue(), userDefined());
    }

    @Given("data from resource [$fileName] is saved in context")
    public void loadDataFromResource(String fileName) {
        // only support yaml at the moment
        if (fileName.endsWith("yml")) {
            YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
            yamlPropertiesFactoryBean.setResources(new ClassPathResource(fileName));
            Properties properties = yamlPropertiesFactoryBean.getObject();
            properties.forEach((key, value) -> testContext.put(key.toString(), value));
        } else {
            throw new IllegalArgumentException("Only yml extension is supported");
        }
    }

    @Given("the following values are saved:$values")
    public void prepareApplicationData(ExamplesTable values) {
        putDataIntoContext(testContext, values, null);
    }
}
