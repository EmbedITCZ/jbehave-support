package org.jbehavesupport.core.internal.web;

import static org.jbehavesupport.core.internal.web.DummyWebElement.URL;
import static org.jbehavesupport.core.internal.web.DummyWebElement.TITLE;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import java.time.Duration;
import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.web.WebElementLocator;
import org.jbehavesupport.core.web.WebElementRegistry;
import org.jbehavesupport.core.web.WebSetting;
import org.jbehavesupport.core.web.WebSteps;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
public class WebElementLocatorImpl implements WebElementLocator {

    @Value("${web.timeout:10}")
    private Long timeout;

    private final WebDriver driver;
    private final WebElementRegistry elementRegistry;

    @Override
    public WebElement findElement(String pageName, String elementName) {
        if (elementName.equals(URL)) {
            return new DummyWebElement(driver.getCurrentUrl());
        } else if (elementName.equals(TITLE)) {
            return new DummyWebElement(driver.getTitle());
        }
        By locator = elementRegistry.getLocator(pageName, elementName);
        return waiting().until(presenceOfElementLocated(locator));
    }

    @Override
    public WebElement findClickableElement(String pageName, String elementName) {
        By locator = elementRegistry.getLocator(pageName, elementName);
        waiting().until(visibilityOfElementLocated(locator));
        return waiting().until(elementToBeClickable(locator));
    }

    private FluentWait<WebDriver> waiting() {
        waitForCustomCondition();
        return new FluentWait<>(driver).withTimeout(Duration.ofSeconds(timeout));
    }

    private void waitForCustomCondition() {
        WebSetting currentSetting = WebSteps.getCurrentSetting();
        if (currentSetting != null) {
            Consumer<WebDriver> waitForLoad = currentSetting.getWaitForLoad();
            if (waitForLoad != null) {
                waitForLoad.accept(driver);
            }
        }
    }

}
