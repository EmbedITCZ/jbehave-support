package org.jbehavesupport.core.internal.web.action;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import org.jbehavesupport.core.web.WebActionContext;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class SelectWebAction extends AbstractWebAction {

    @Override
    public String name() {
        return "SELECT";
    }

    @Override
    public void perform(WebActionContext ctx) {
        saveAlias(ctx);

        WebElement element = findElement(ctx);

        Assert.isTrue(equalsIgnoreCase(element.getTagName(), "select"), "The SELECT action is not supported on this element type: " + element.getTagName());

        new Select(element).selectByVisibleText(ctx.getData());
    }

}
