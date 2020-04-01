package org.jbehavesupport.core.internal.web.waitcondition;

import java.util.regex.Pattern;

import org.jbehavesupport.core.web.WebWaitConditionContext;

import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

@Component
public class MissingAttributeWebWaitCondition extends AbstractAttributeWebWaitCondition {

    public MissingAttributeWebWaitCondition(){
        super(Pattern.compile("missing (?!text)([\\w\\-]+)(( .*)|$)"));
    }

    @Override
    protected WebElement evaluateAttribute(WebWaitConditionContext ctx, WebElement element, String attributeValue) {
        if (attributeValue == null) {
            return element;
        } else if (ctx.getValue() != null && !attributeValue.contains(ctx.getValue())) {
            return element;
        } else {
            return null;
        }
    }


}
