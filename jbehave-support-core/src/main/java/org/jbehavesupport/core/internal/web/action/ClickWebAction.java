package org.jbehavesupport.core.internal.web.action;

import org.jbehavesupport.core.web.WebActionContext;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Component;

@Component
public class ClickWebAction extends AbstractWebAction {

    @Override
    public String name() {
        return "CLICK";
    }

    @Override
    public void perform(WebActionContext ctx) {
        WebElement element = findElement(ctx);

        scrollIntoView(element);

        new Actions(driver)
            .click(element)
            .perform();
    }

}
