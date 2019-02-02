package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isTrue;

import java.math.BigDecimal;
import java.util.Arrays;

import org.jbehavesupport.core.expression.ExpressionCommand;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

/**
 * Command run addition operation on parameters.
 * Parameters must be numbers or numeric string.
 */
@Component
public class PlusCommand implements ExpressionCommand {

    @Override
    public Object execute(Object... params) {
        isTrue(params.length >= 2, "At least two parameters were expected");
        Arrays.stream(params).forEach(e -> {
                isTrue(e instanceof String || e instanceof Number, "Parameter must be String or Number: " + e);
                if (e instanceof String) {
                    isTrue(NumberUtils.isCreatable((String) e), "String parameter must be numeric: " + e);
                }
            }
        );
        return Arrays.stream(params).map(i -> new BigDecimal(i.toString())).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
