package org.jbehavesupport.core.internal.expression.temporal;

import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.jbehavesupport.core.expression.ExpressionCommand;
import org.springframework.stereotype.Component;

/**
 * Format date time to expected format.
 * Command consumes two arguments: date time in string format and output format.
 * E.g.:
 * param1: "2031-05-20T10:15:30"
 * param2: "MM/dd/yyyy HH:mm:ss"
 */
@Component
public class FormatDateTimeCommand implements ExpressionCommand {

    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 2, "Two parameters were expected");
        isInstanceOf(String.class, params[0], "First parameter must be string");
        isInstanceOf(String.class, params[1], "Second parameter must be string");

        LocalDateTime input = LocalDateTime.parse((String) params[0]);
        String dateFormat = (String) params[1];
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
        return dtf.format(input);
    }
}
