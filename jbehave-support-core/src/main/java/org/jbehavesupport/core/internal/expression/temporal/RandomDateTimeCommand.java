package org.jbehavesupport.core.internal.expression.temporal;


import static org.springframework.util.Assert.isTrue;

import org.jbehavesupport.core.expression.ExpressionCommand;
import org.jbehavesupport.core.internal.RandomGeneratorHelper;
import org.springframework.stereotype.Component;

/**
 * Generate random {@link java.time.LocalDateTime}
 */
@Component
public class RandomDateTimeCommand implements ExpressionCommand {

    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 0, "No arguments are expected");
        return RandomGeneratorHelper.randomDateTime();
    }
}
