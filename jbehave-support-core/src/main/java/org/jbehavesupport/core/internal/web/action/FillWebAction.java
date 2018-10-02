package org.jbehavesupport.core.internal.web.action;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import org.jbehavesupport.core.web.WebActionContext;

import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

@Component
public class FillWebAction extends AbstractWebAction {

    @Override
    public String name() {
        return "FILL";
    }

    @Override
    public void perform(WebActionContext ctx) {
        saveAlias(ctx);

        WebElement element = findElement(ctx);

        if (equalsIgnoreCase(element.getTagName(), "input") && equalsIgnoreCase(element.getAttribute("type"), "text")) {
            element.clear();
        }

        element.sendKeys(ctx.getData());
    }

}
