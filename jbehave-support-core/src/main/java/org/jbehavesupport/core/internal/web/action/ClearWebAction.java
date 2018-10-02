package org.jbehavesupport.core.internal.web.action;

import org.jbehavesupport.core.web.WebActionContext;

import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

@Component
public class ClearWebAction extends AbstractWebAction {

    @Override
    public String name() {
        return "CLEAR";
    }

    @Override
    public void perform(WebActionContext ctx) {
        WebElement element = findElement(ctx);
        element.clear();
    }

}
