package org.jbehavesupport.core.jms;

import static org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils.qualifiedBeanOfType;

import lombok.RequiredArgsConstructor;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * Steps for publishing JMS messages to JMS destinations
 * <p>
 * Steps:
 * <ul>
 * <li>
 * <code>Given [$typeAlias] data for JMS broker [$broker]: $data</code>
 * </li>
 * <li>
 * <code>When [$typeAlias] is sent to destination [$destinationName] on JMS broker [$broker]</code>
 * </li>
 * </ul>
 * <p>
 * Parameters:
 * <ul>
 * <li>
 * <code>$typeAlias</code> - simple class name
 * </li>
 * <li>
 * <code>$destinationName</code> - JMS queue or topic
 * </li>
 * <li>
 * <code>$broker</code> - qualifier used to resolve {@link JmsHandler}, which implements individual steps
 * and provides application specific customization
 * </li>
 * </ul>
 * <p>
 * <p>
 * <p>
 * <p>
 */
@Component
@RequiredArgsConstructor
public final class JmsSteps {

    private final ConfigurableListableBeanFactory beanFactory;

    @Given("[$typeAlias] data for JMS broker [$broker]: $data")
    public void messageData(String typeAlias, String broker, ExamplesTable data) {
        resolveHandler(broker).setMessageData(typeAlias, data);
    }

    @When("[$typeAlias] is sent to destination [$destinationName] on JMS broker [$broker]")
    public void sendMessage(String typeAlias, String destinationName, String broker) {
        this.resolveHandler(broker).sendMessage(destinationName, typeAlias);
    }

    private JmsHandler resolveHandler(String broker) {
        try {
            return qualifiedBeanOfType(beanFactory, JmsHandler.class, broker);
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalArgumentException(
                "JmsSteps requires single JmsHandler bean with qualifier [" + broker + "]", e);
        }
    }

}
