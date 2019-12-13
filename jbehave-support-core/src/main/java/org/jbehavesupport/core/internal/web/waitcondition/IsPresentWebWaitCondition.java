package org.jbehavesupport.core.internal.web.waitcondition;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import org.jbehavesupport.core.web.WebWaitConditionContext;

import org.springframework.stereotype.Component;

@Component
public class IsPresentWebWaitCondition extends AbstractWebWaitCondition {

    @Override
    public boolean match(WebWaitConditionContext ctx) {
        return "is present".equals(ctx.getCondition());
    }

    @Override
    public void evaluate(WebWaitConditionContext ctx) {
        fluentWait().until(presenceOfElementLocated(getLocator(ctx)));
    }

}
