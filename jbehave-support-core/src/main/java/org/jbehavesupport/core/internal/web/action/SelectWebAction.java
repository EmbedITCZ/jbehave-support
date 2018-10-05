package org.jbehavesupport.core.internal.web.action;

import org.apache.commons.lang3.StringUtils;
import org.jbehavesupport.core.web.WebActionContext;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

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

        if (equalsIgnoreCase(element.getTagName(), "input") && equalsIgnoreCase(element.getAttribute("type"), "checkbox")) {
            handleCheckbox(ctx, element);
        } else if (equalsIgnoreCase(element.getTagName(), "select")) {
            handleSelect(ctx, element);
        } else {
            throw new IllegalArgumentException("The SELECT action is not supported on this element type: " + element.getTagName());
        }
    }

    private void handleCheckbox(final WebActionContext ctx, final WebElement element) {
        boolean checked = StringUtils.isEmpty(ctx.getData()) || toBoolean(ctx.getData());

        if (toBoolean(element.getAttribute("checked")) ^ checked) {
            element.sendKeys(Keys.SPACE);
        }
    }

    private void handleSelect(final WebActionContext ctx, final WebElement element) {
        new Select(element).selectByVisibleText(ctx.getData());
    }
}
