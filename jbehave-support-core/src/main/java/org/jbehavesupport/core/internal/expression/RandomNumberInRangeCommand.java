package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isTrue;

import org.jbehavesupport.core.expression.ExpressionCommand;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Generate random number in range.
 */
@Component
public class RandomNumberInRangeCommand implements ExpressionCommand {

    @Override
    public Object execute(Object... params) {
        Assert.isTrue(params.length == 2, "Two parameters were expected");
        Long startInclusive = parseLong(params[0]);
        Long endExclusive = parseLong(params[1]);
        return RandomUtils.nextLong(startInclusive, endExclusive);
    }

    private long parseLong(Object number) {
        isTrue(number instanceof String || number instanceof Number, "Parameter must be String or Number");
        if (number instanceof String) {
            isTrue(StringUtils.isNumeric((String)number), "String must be numeric");
            return Long.parseLong((String) number);
        } else {
            return ((Number) number).longValue();
        }
    }

}
