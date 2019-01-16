package org.jbehavesupport.core.internal.web;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.time.Duration;
import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.web.WebElementLocator;
import org.jbehavesupport.core.web.WebElementRegistry;
import org.jbehavesupport.core.web.WebSetting;
import org.jbehavesupport.core.web.WebSteps;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebElementLocatorImpl implements WebElementLocator {

    @Value("${web.timeout:10}")
    private Long timeout;

    private final WebDriver driver;
    private final WebElementRegistry elementRegistry;

    @Override
    public WebElement findElement(String pageName, String elementName) {
        By locator = elementRegistry.getLocator(pageName, elementName);
        return waiting().until(presenceOfElementLocated(locator));
    }

    @Override
    public WebElement findClickableElement(String pageName, String elementName) {
        By locator = elementRegistry.getLocator(pageName, elementName);
        return waiting().until(elementToBeClickable(locator));
    }

    private FluentWait<WebDriver> waiting() {
        waitForDocumentIsReady();
        waitForCustomCondition();
        return new FluentWait<>(driver).withTimeout(Duration.ofSeconds(timeout));
    }

    private void waitForDocumentIsReady() {
        new WebDriverWait(driver, 10).until((ExpectedCondition<Boolean>) wd ->
            ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
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
