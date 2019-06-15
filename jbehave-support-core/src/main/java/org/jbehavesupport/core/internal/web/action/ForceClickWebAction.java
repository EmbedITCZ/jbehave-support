package org.jbehavesupport.core.internal.web.action;

import org.jbehavesupport.core.web.WebActionContext;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

@Component
public class ForceClickWebAction extends AbstractWebAction {

    @Override
    public String name() {
        return "FORCE_CLICK";
    }

    @Override
    public void perform(WebActionContext ctx) {
        if (!(driver instanceof JavascriptExecutor)) {
            throw new AssertionError("WebDriver must implement javascript to use FORCE_CLICK");
        }
        WebElement element = elementLocator.findElement(ctx.getPage(), ctx.getElement());
        JavascriptExecutor jse2 = (JavascriptExecutor) driver;
        jse2.executeScript("arguments[0].click();", element);
    }

}
