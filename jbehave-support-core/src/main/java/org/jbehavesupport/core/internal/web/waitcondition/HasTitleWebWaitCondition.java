package org.jbehavesupport.core.internal.web.waitcondition;

import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.jbehavesupport.core.web.WebWaitConditionContext;

import org.springframework.stereotype.Component;

@Component
public class HasTitleWebWaitCondition extends AbstractWebWaitCondition {

    @Override
    public boolean match(WebWaitConditionContext ctx) {
        return "@title".equals(ctx.getElement()) && startsWith(ctx.getCondition(), "has text ");
    }

    @Override
    public void evaluate(WebWaitConditionContext ctx) {
        fluentWait().until(titleContains(ctx.getValue()));
    }

}
