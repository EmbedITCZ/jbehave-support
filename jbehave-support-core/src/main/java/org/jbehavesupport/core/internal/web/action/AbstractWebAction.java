package org.jbehavesupport.core.internal.web.action;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.web.WebAction;
import org.jbehavesupport.core.web.WebActionContext;
import org.jbehavesupport.core.web.WebElementLocator;

import org.jbehavesupport.core.internal.MetadataUtil;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractWebAction implements WebAction {

    @Autowired
    protected WebDriver driver;
    @Autowired
    protected WebElementLocator elementLocator;
    @Autowired
    protected TestContext testContext;

    protected final WebElement findElement(WebActionContext ctx) {
        return elementLocator.findClickableElement(ctx.getPage(), ctx.getElement());
    }

    protected final void scrollIntoView(WebElement element) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("arguments[0].scrollIntoView(false);", element);
    }

    protected final void saveAlias(WebActionContext ctx) {
        if (isNotEmpty(ctx.getAlias())) {
            testContext.put(ctx.getAlias(), ctx.getData(), MetadataUtil.userDefined());
        }
    }

}
