package org.jbehavesupport.core.internal.web.waitcondition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jbehavesupport.core.web.WebWaitConditionContext;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class MissingAttributeWebWaitCondition extends AbstractWebWaitCondition {

    private static final Pattern PATTERN = Pattern.compile("missing (?!text)([\\w\\-]+)(( .*)|$)");

    @Override
    public boolean match(WebWaitConditionContext ctx) {
        return PATTERN.matcher(ctx.getCondition()).matches();
    }

    @Override
    public void evaluate(WebWaitConditionContext ctx) {
        String attributeName = parseAttributeName(ctx);
        fluentWait().until((ExpectedCondition<WebElement>) driver -> {
            WebElement element = findElement(ctx);
            String attributeValue = element.getAttribute(attributeName);

            if (attributeValue == null) {
                return element;
            } else if (ctx.getValue() != null && !attributeValue.contains(ctx.getValue())) {
                return element;
            } else {
                return null;
            }
        });
    }

    private String parseAttributeName(WebWaitConditionContext ctx) {
        Matcher matcher = PATTERN.matcher(ctx.getCondition());
        Assert.isTrue(matcher.matches(), "Given wait condition [" + ctx.getCondition() + "] does not match expected pattern");
        return matcher.group(1);
    }

}
