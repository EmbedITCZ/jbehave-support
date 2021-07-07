package org.jbehavesupport.core.internal.expression.numeric;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.expression.ExpressionCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import static org.jbehavesupport.core.internal.expression.CommandHelper.checkNumericParams;
import static org.springframework.util.Assert.isTrue;

/**
 * Command run dividing operation on parameters.
 * parameters goes in order: dividend, divisors...
 */
@Component
@RequiredArgsConstructor
public class DivideCommand implements ExpressionCommand {

    private final ConversionService conversionService;

    @Value("${numeric.scale:10}")
    private int scale = 10;

    @Override
    public Object execute(Object... params) {
        isTrue(params.length >= 2, "At least two parameters were expected");
        checkNumericParams(params);

        BigDecimal[] divisors = Arrays.stream(params)
            .skip(1)
            .map(it -> conversionService.convert(it, BigDecimal.class))
            .toArray(BigDecimal[]::new);

        if (Arrays.asList(divisors).contains(BigDecimal.ZERO)){
            throw new IllegalArgumentException("Can not divide by zero");
        }

        return Arrays.stream(divisors)
            .reduce(conversionService.convert(params[0], BigDecimal.class), (dividend, divisor) -> dividend.divide(divisor, new MathContext(scale, RoundingMode.HALF_UP)));
    }
}
