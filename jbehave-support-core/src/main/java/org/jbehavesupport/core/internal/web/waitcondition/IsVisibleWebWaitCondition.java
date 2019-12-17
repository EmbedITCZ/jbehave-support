package org.jbehavesupport.core.internal.web.waitcondition;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import org.jbehavesupport.core.web.WebWaitConditionContext;

import org.springframework.stereotype.Component;

@Component
public class IsVisibleWebWaitCondition extends AbstractWebWaitCondition {

    @Override
    public boolean match(WebWaitConditionContext ctx) {
        return "is visible".equals(ctx.getCondition());
    }

    @Override
    public void evaluate(WebWaitConditionContext ctx) {
        fluentWait().until(visibilityOfElementLocated(getLocator(ctx)));
    }

}
