package org.jbehavesupport.core.internal.web.waitcondition;

import java.time.Duration;

import org.jbehavesupport.core.internal.web.WebElementLocatorImpl;
import org.jbehavesupport.core.web.WebElementRegistry;
import org.jbehavesupport.core.web.WebWaitCondition;
import org.jbehavesupport.core.web.WebWaitConditionContext;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractWebWaitCondition implements WebWaitCondition {

    @Autowired
    private WebDriver driver;
    @Autowired
    private WebElementRegistry elementRegistry;
    @Autowired
    private WebElementLocatorImpl webElementLocatorImpl;

    @Value("${web.timeout:10}")
    private Long timeout;

    protected final FluentWait<WebDriver> fluentWait() {
        return new FluentWait<>(driver)
            .pollingEvery(Duration.ofSeconds(1))
            .withTimeout(Duration.ofSeconds(timeout))
            .ignoring(NoSuchElementException.class);
    }

    protected final By getLocator(WebWaitConditionContext ctx) {
        return elementRegistry.getLocator(ctx.getPage(), ctx.getElement());
    }

    protected final WebElement findElement(WebWaitConditionContext ctx) {
        if (ctx.getElement().equals("@url") | ctx.getElement().equals("@title")) {
            return webElementLocatorImpl.findElement(null, ctx.getElement());
        }
        return driver.findElement(getLocator(ctx));
    }

}
