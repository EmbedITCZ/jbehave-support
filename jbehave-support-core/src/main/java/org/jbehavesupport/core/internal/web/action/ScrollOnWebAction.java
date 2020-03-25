package org.jbehavesupport.core.internal.web.action;

import org.jbehavesupport.core.web.WebActionContext;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

@Component
public class ScrollOnWebAction extends AbstractWebAction {

    @Override
    public String name() {
        return "SCROLL_ON";
    }

    @Override
    public void perform(WebActionContext ctx) {
        WebElement element = findElement(ctx);
        scrollIntoView(element);
    }
}
