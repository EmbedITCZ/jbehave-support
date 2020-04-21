package org.jbehavesupport.core.web

import org.jbehave.core.model.ExamplesTable
import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.TestContext
import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter
import org.jbehavesupport.core.internal.web.RefreshableWebElement
import org.jbehavesupport.core.internal.web.WebElementLocatorImpl
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.remote.RemoteWebElement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@ContextConfiguration(classes = [TestConfig.class, WebElementMockConfig.class])
class StaleElementExceptionTest extends Specification {
    @Autowired
    WebSteps webSteps

    @Autowired
    TestContext testContext

    void savePropertyValueTest() {
        def page = "page"
        def table = new ExamplesTable(
            "| element   | property | contextAlias |\n" +
                "| myElement | TEXT     | MY_TEXT      | ")
        def exception = null

        when:
        try {
            webSteps.storePropertiesInContext(page, table)
        } catch (Exception e) {
            exception = e
        }

        then:
        testContext.get("MY_TEXT") == ("mocked text")
        exception == null
    }

    void thrownException() {
        def page = "page"
        def table = new ExamplesTable(
            "| element   | property | contextAlias |\n" +
                "| myElement | VALUE    | MY_TEXT      | ")
        def exception = null

        when:
        try {
            webSteps.storePropertiesInContext(page, table)
        } catch (Exception e) {
            exception = e
        }

        then:
        exception instanceof StaleElementReferenceException
    }

    void waitConditionTest() {
        def exception = null

        when:
        try {
            webSteps.waitUntilCondition("home", "#hasText", new ExpressionEvaluatingParameter<String>("has text mocked text"))
        } catch (Exception e) {
            exception = e
        }

        then:
        exception == null
    }
}

class WebElementMockConfig {
    @Bean
    WebElementLocator webElementLocator() {
        RemoteWebElement staleWebElement = mock(RemoteWebElement.class)
        when(staleWebElement.getAttribute("value")).thenThrow(StaleElementReferenceException.class)
        when(staleWebElement.getText()).thenThrow(StaleElementReferenceException.class)
        RemoteWebElement notStaleWebElement = mock(RemoteWebElement.class)
        when(notStaleWebElement.getAttribute("value")).thenThrow(StaleElementReferenceException.class)
        when(notStaleWebElement.getText()).thenReturn("mocked text")
        WebElementLocatorImpl webElementLocator = mock(WebElementLocatorImpl.class)
        when(webElementLocator.findElement(any(String.class), any(String.class)))
            .thenReturn(new RefreshableWebElement(webElementLocator, staleWebElement, "elementName", "pageName"))
            .thenReturn(new RefreshableWebElement(webElementLocator, staleWebElement, "elementName", "pageName"))
            .thenReturn(new RefreshableWebElement(webElementLocator, staleWebElement, "elementName", "pageName"))
            .thenReturn(new RefreshableWebElement(webElementLocator, staleWebElement, "elementName", "pageName"))
            .thenReturn(new RefreshableWebElement(webElementLocator, notStaleWebElement, "elementName", "pageName"))
        when(webElementLocator.findPureElement(any(String.class), any(String.class)))
            .thenReturn(staleWebElement)
            .thenReturn(staleWebElement)
            .thenReturn(notStaleWebElement)
        return webElementLocator
    }
}