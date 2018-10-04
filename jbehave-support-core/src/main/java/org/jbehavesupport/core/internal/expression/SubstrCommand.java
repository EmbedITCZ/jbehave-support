package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;

import org.jbehavesupport.core.expression.ExpressionCommand;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Create substring.
 * Command consumes three parameters.
 * params[0] - string for substring
 * params[1] - start index
 * params[2] - optional - end index
 */
@Component
public class SubstrCommand implements ExpressionCommand {

    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 2 || params.length == 3, "Two or three parameters were expected");
        isInstanceOf(String.class, params[0], "First param must be string");

        String input = (String) params[0];
        Integer startIndex = parseInteger(params[1]);
        if (params.length == 2) {
            return input.substring(startIndex);
        } else {
            Integer endIndex = parseInteger(params[2]);
            return input.substring(startIndex, endIndex);
        }
    }

    private int parseInteger(Object number) {
        isTrue(number instanceof String || number instanceof Number, "Parameter must be String or Number");
        if (number instanceof String) {
            isTrue(StringUtils.isNumeric((String) number), "String must be numeric");
            return Integer.parseInt((String) number);
        } else {
            return ((Number) number).intValue();
        }
    }
}
