package org.jbehavesupport.core.internal.expression.numeric;

import java.math.BigDecimal;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.expression.ExpressionCommand;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import static org.jbehavesupport.core.internal.expression.CommandHelper.checkNumericParams;
import static org.springframework.util.Assert.isTrue;

/**
 * Command run subtraction operation on parameters.
 * Parameters must be numbers or numeric string.
 */
@Component
@RequiredArgsConstructor
public class MinusCommand implements ExpressionCommand {

    private final ConversionService conversionService;

    @Override
    public Object execute(Object... params) {
        isTrue(params.length >= 2, "At least two parameters were expected");
        checkNumericParams(params);
        return Arrays
            .stream(Arrays.copyOfRange(params, 1, params.length))
            .map(i -> conversionService.convert(i, BigDecimal.class))
            .reduce(conversionService.convert(params[0], BigDecimal.class), BigDecimal::subtract);
    }
}
