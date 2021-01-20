package org.jbehavesupport.core.internal.expression.numeric;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.jbehavesupport.core.expression.ExpressionCommand;
import org.springframework.stereotype.Component;
import static org.jbehavesupport.core.internal.expression.CommandHelper.checkNumericParams;
import static org.springframework.util.Assert.isTrue;

/**
 * Command round given number
 * parameters goes in order: number, decimal places
 */
@Component
public class RoundCommand implements ExpressionCommand {

    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 2, "Two parameters were expected");
        checkNumericParams(params);
        int scale;
        try{
            scale = Integer.parseInt(params[1].toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Scale must be integer");
        }

        return new BigDecimal(params[0].toString()).round(new MathContext(scale + 1, RoundingMode.HALF_UP));
    }
}
