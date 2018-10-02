package org.jbehavesupport.core.internal.web.action;

import static org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent;

import org.jbehavesupport.core.web.WebActionContext;

import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.util.Assert;

public abstract class AbstractAlertWebAction extends AbstractWebAction {

    @Override
    public final void perform(WebActionContext ctx) {
        Assert.isTrue("@alert".equals(ctx.getElement()), "Alert action has to be used only with @alert element.");
        new FluentWait<>(driver).until(alertIsPresent());
        Alert alert = driver.switchTo().alert();
        perform(ctx, alert);
    }

    protected abstract void perform(WebActionContext ctx, Alert alert);

}
