package org.jbehavesupport.core.internal.web.waitcondition;

import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.startsWith;

import org.jbehavesupport.core.web.WebWaitConditionContext;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.springframework.stereotype.Component;

@Component
public class HasTextWebWaitCondition extends AbstractWebWaitCondition {

    @Override
    public boolean match(WebWaitConditionContext ctx) {
        return startsWith(ctx.getCondition(), "has text ");
    }

    @Override
    public void evaluate(WebWaitConditionContext ctx) {
        fluentWait().until((ExpectedCondition<WebElement>) driver -> {
            WebElement element = findElement(ctx);
            String text = element.getText();
            if (isNotEmpty(ctx.getValue())) {
                return contains(text, ctx.getValue()) ? element : null;
            } else {
                return isNotEmpty(text) ? element : null;
            }
        });
    }

}
