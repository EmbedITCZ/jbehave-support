package org.jbehavesupport.core.internal.expression.numeric;

import java.math.BigDecimal;
import java.util.Arrays;
import org.jbehavesupport.core.expression.ExpressionCommand;
import org.springframework.stereotype.Component;
import static org.jbehavesupport.core.internal.expression.CommandHelper.checkNumericParams;
import static org.springframework.util.Assert.isTrue;

/**
 * Command run subtraction operation on parameters.
 * Parameters must be numbers or numeric string.
 */
@Component
public class MinusCommand implements ExpressionCommand {

    @Override
    public Object execute(Object... params) {
        isTrue(params.length >= 2, "At least two parameters were expected");
        checkNumericParams(params);
        return Arrays
            .stream(Arrays.copyOfRange(params, 1, params.length))
            .map(i -> new BigDecimal(i.toString()))
            .reduce(new BigDecimal(params[0].toString()), BigDecimal::subtract);
    }
}
