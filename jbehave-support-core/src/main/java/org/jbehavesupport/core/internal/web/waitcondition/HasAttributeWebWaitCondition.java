package org.jbehavesupport.core.internal.web.waitcondition;

import static org.apache.commons.lang3.StringUtils.contains;

import java.util.regex.Pattern;

import org.jbehavesupport.core.web.WebWaitConditionContext;

import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

@Component
public class HasAttributeWebWaitCondition extends AbstractAttributeWebWaitCondition {

    public HasAttributeWebWaitCondition(){
        super(Pattern.compile("has (?!text)([\\w\\-]+)(( .*)|$)"));
    }

    @Override
    protected WebElement evaluateAttribute(WebWaitConditionContext ctx, WebElement element, String attributeValue) {
        if (attributeValue != null && ctx.getValue() == null) {
            return element;
        } else {
            return contains(attributeValue, ctx.getValue()) ? element : null;
        }
    }

}
