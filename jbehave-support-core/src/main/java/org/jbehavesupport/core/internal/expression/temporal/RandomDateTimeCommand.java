package org.jbehavesupport.core.internal.expression.temporal;


import static org.springframework.util.Assert.isTrue;

import java.time.LocalDate;

import org.jbehavesupport.core.expression.ExpressionCommand;
import org.jbehavesupport.core.internal.util.RandomDateTime;
import org.springframework.stereotype.Component;

/**
 * Generate random {@link java.time.LocalDateTime}
 * @see RandomDateCommand
 */
@Component
public class RandomDateTimeCommand extends RandomDateCommand implements ExpressionCommand {

    @Override
    public Object execute(Object... params) {
        isTrue(params.length <= 2, "At most 2 parameters can be provided");
        LocalDate since = params.length >= 1 ? parseTime((String) params[0]) : null;
        LocalDate to = params.length == 2 ? parseTime((String) params[1]) : null;

        return RandomDateTime.builder()
            .since(since)
            .to(to)
            .build()
            .get();
    }
}
