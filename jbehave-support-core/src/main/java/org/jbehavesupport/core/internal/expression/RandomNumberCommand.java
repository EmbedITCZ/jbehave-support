package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isTrue;

import org.jbehavesupport.core.expression.ExpressionCommand;
import org.jbehavesupport.core.internal.RandomGeneratorHelper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Generate random numeric string with specific length.
 * Command consumes one parameter - length of random number.
 */
@Component
public class RandomNumberCommand implements ExpressionCommand {

    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 1, "Only one parameter is expected");
        isTrue(params[0] instanceof String || params[0] instanceof Number, "Parameter must be String or Number");

        int length;
        if (params[0] instanceof String) {
            isTrue(StringUtils.isNumeric((String) params[0]), "String must be numeric");
            length = Integer.parseInt((String) params[0]);
        } else {
            length = ((Number) params[0]).intValue();
        }

        return RandomGeneratorHelper.randomNumericAsString(length);
    }

}
