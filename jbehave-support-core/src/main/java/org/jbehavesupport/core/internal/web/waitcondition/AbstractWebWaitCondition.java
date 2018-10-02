package org.jbehavesupport.core.internal.web.waitcondition;

import static java.util.concurrent.TimeUnit.SECONDS;

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

    @Value("${web.timeout:10}")
    private Long timeout;

    protected final FluentWait<WebDriver> wait(WebWaitConditionContext ctx) {
        return new FluentWait<>(driver)
            .pollingEvery(1, SECONDS)
            .withTimeout(timeout, SECONDS)
            .ignoring(NoSuchElementException.class);
    }

    protected final By getLocator(WebWaitConditionContext ctx) {
        return elementRegistry.getLocator(ctx.getPage(), ctx.getElement());
    }

    protected final WebElement findElement(WebWaitConditionContext ctx) {
        return driver.findElement(getLocator(ctx));
    }

}
