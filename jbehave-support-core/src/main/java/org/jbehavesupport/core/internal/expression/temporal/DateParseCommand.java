package org.jbehavesupport.core.internal.expression.temporal;

import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.jbehavesupport.core.expression.ExpressionCommand;

import org.springframework.stereotype.Component;

/**
 * Command for parsing date.
 * Command consumes two arguments date in string format and format.
 * E.g.:
 * param1: "05/20/2031"
 * param2: "MM/dd/yyyy"
 */
@Component
public class DateParseCommand implements ExpressionCommand {

    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 2, "Two parameters were expected");
        isInstanceOf(String.class, params[0], "First param must be string");
        isInstanceOf(String.class, params[1], "Second param must be string");

        String dateAsString = (String) params[0];
        String dateFormat = (String) params[1];
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
        return LocalDate.parse(dateAsString, dtf);
    }
}
