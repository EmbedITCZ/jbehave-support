package org.jbehavesupport.core.internal.web.waitcondition;

import org.jbehavesupport.core.web.WebWaitConditionContext;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractAttributeWebWaitCondition extends AbstractWebWaitCondition {

    private final Pattern pattern;

    AbstractAttributeWebWaitCondition(Pattern attributePattern) {
        pattern = attributePattern;
    }

    @Override
    public boolean match(WebWaitConditionContext ctx) {
        return pattern.matcher(ctx.getCondition()).matches();
    }

    @Override
    public void evaluate(WebWaitConditionContext ctx) {
        String attributeName = parseAttributeName(ctx);
        fluentWait().until(driver -> {
            WebElement element = findElement(ctx);
            String attributeValue = element.getAttribute(attributeName);
            return evaluateAttribute(ctx, element, attributeValue);
        });
    }

    protected WebElement evaluateAttribute(WebWaitConditionContext ctx, WebElement element, String attributeValue) {
        if (attributeValue == null) {
            return element;
        } else if (ctx.getValue() != null && !attributeValue.contains(ctx.getValue())) {
            return element;
        } else {
            return null;
        }
    }

    private String parseAttributeName(WebWaitConditionContext ctx) {
        Matcher matcher = pattern.matcher(ctx.getCondition());
        Assert.isTrue(matcher.matches(), "Given wait condition [" + ctx.getCondition() + "] does not match expected pattern");
        return matcher.group(1);
    }
}
