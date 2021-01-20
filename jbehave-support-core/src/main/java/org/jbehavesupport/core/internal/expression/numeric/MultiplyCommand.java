package org.jbehavesupport.core.internal.expression.numeric;

import java.math.BigDecimal;
import java.util.Arrays;
import org.jbehavesupport.core.expression.ExpressionCommand;
import org.springframework.stereotype.Component;
import static org.jbehavesupport.core.internal.expression.CommandHelper.checkNumericParams;
import static org.springframework.util.Assert.isTrue;

/**
 * Command run multiplying operation on parameters.
 * Parameters must be numbers or numeric string.
 */
@Component
public class MultiplyCommand implements ExpressionCommand {

    @Override
    public Object execute(Object... params) {
        isTrue(params.length >= 2, "At least two parameters were expected");
        checkNumericParams(params);
        return Arrays.stream(params)
            .map(i -> new BigDecimal(i.toString()))
            .reduce(BigDecimal.ONE, BigDecimal::multiply);
    }
}
