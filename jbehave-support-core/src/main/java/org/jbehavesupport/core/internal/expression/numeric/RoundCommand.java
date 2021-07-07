package org.jbehavesupport.core.internal.expression.numeric;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.expression.ExpressionCommand;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import static org.jbehavesupport.core.internal.expression.CommandHelper.checkNumericParams;
import static org.springframework.util.Assert.isTrue;

/**
 * Command round given number
 * parameters goes in order: number, decimal places
 */
@Component
@RequiredArgsConstructor
public class RoundCommand implements ExpressionCommand {

    private final ConversionService conversionService;

    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 2, "Two parameters were expected");
        checkNumericParams(params);
        int decimalPoints;
        try{
            decimalPoints = Integer.parseInt(params[1].toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Scale must be integer");
        }

        BigDecimal number = conversionService.convert(params[0], BigDecimal.class);
        int scaleBeforeDecimalPoint = number.precision() - number.scale();
        return number.round(new MathContext(scaleBeforeDecimalPoint + decimalPoints, RoundingMode.HALF_UP));
    }
}
