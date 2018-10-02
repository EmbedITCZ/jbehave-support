package org.jbehavesupport.core.internal.web.waitcondition;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

import org.jbehavesupport.core.web.WebWaitConditionContext;

import org.springframework.stereotype.Component;

@Component
public class IsClickableWebWaitCondition extends AbstractWebWaitCondition {

    @Override
    public boolean match(WebWaitConditionContext ctx) {
        return "is clickable".equals(ctx.getCondition());
    }

    @Override
    public void evaluate(WebWaitConditionContext ctx) {
        wait(ctx).until(elementToBeClickable(getLocator(ctx)));
    }

}
