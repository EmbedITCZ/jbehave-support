package org.jbehavesupport.core.internal.expression.temporal;

import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.jbehavesupport.core.expression.ExpressionCommand;
import org.springframework.stereotype.Component;

/**
 * Command for parsing date.
 * Command consumes two arguments date in string format and format.
 * E.g.:
 * param1: "2031-05-20T10:15:30"
 * param2: "MM/dd/yyyy HH:mm:ss"
 */
@Component
public class DateTimeParseCommand implements ExpressionCommand {

    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 2, "Two parameters were expected");
        isInstanceOf(String.class, params[0], "First param must be string");
        isInstanceOf(String.class, params[1], "Second param must be string");

        String dateAsString = (String) params[0];
        String dateFormat = (String) params[1];
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
        return LocalDateTime.parse(dateAsString, dtf);
    }
}
