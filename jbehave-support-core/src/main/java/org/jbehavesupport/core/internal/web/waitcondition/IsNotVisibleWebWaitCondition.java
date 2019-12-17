package org.jbehavesupport.core.internal.web.waitcondition;

import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;

import org.jbehavesupport.core.web.WebWaitConditionContext;

import org.springframework.stereotype.Component;

@Component
public class IsNotVisibleWebWaitCondition extends AbstractWebWaitCondition {

    @Override
    public boolean match(WebWaitConditionContext ctx) {
        return "is not visible".equals(ctx.getCondition());
    }

    @Override
    public void evaluate(WebWaitConditionContext ctx) {
        fluentWait().until(invisibilityOfElementLocated(getLocator(ctx)));
    }

}
