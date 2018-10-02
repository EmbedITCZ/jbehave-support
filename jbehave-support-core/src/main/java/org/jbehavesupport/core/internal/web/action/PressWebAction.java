package org.jbehavesupport.core.internal.web.action;

import org.jbehavesupport.core.web.WebActionContext;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

@Component
public class PressWebAction extends AbstractWebAction {

    @Override
    public String name() {
        return "PRESS";
    }

    @Override
    public void perform(WebActionContext ctx) {
        WebElement element = findElement(ctx);
        element.sendKeys(Keys.valueOf(ctx.getData()));
    }

}
