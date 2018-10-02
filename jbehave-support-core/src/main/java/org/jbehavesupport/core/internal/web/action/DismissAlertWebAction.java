package org.jbehavesupport.core.internal.web.action;

import org.jbehavesupport.core.web.WebActionContext;

import org.openqa.selenium.Alert;
import org.springframework.stereotype.Component;

@Component
public class DismissAlertWebAction extends AbstractAlertWebAction {

    @Override
    public String name() {
        return "DISMISS";
    }

    @Override
    protected void perform(WebActionContext ctx, Alert alert) {
        alert.dismiss();
    }

}
