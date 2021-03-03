package org.jbehavesupport.core.internal.expression.numeric;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import org.jbehavesupport.core.expression.ExpressionCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import static org.jbehavesupport.core.internal.expression.CommandHelper.checkNumericParams;
import static org.springframework.util.Assert.isTrue;

/**
 * Command run dividing operation on parameters.
 * parameters goes in order: dividend, divisors...
 */
@Component
public class DivideCommand implements ExpressionCommand {

    @Value("${numeric.scale:10}")
    private int scale = 10;

    @Override
    public Object execute(Object... params) {
        isTrue(params.length >= 2, "At least two parameters were expected");
        checkNumericParams(params);

        BigDecimal[] divisors = Arrays.stream(params).skip(1).map( it -> new BigDecimal(it.toString())).toArray(BigDecimal[]::new);

        if (Arrays.asList(divisors).contains(BigDecimal.ZERO)){
            throw new IllegalArgumentException("Can not divide by zero");
        }

        return Arrays.stream(divisors)
            .reduce(new BigDecimal(params[0].toString()), (dividend, divisor) -> dividend.divide(divisor, new MathContext(scale, RoundingMode.HALF_UP)));
    }
}
