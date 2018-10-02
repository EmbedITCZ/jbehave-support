package org.jbehavesupport.core.internal.web.action;

import org.jbehavesupport.core.web.WebActionContext;

import org.openqa.selenium.Alert;
import org.springframework.stereotype.Component;

@Component
public class AcceptAlertWebAction extends AbstractAlertWebAction {

    @Override
    public String name() {
        return "ACCEPT";
    }

    @Override
    protected void perform(WebActionContext ctx, Alert alert) {
        alert.accept();
    }

}
