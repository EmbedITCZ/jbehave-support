package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.jbehavesupport.core.expression.ExpressionCommand;

import org.springframework.stereotype.Component;

/**
 * Format date to expected format.
 * Command consumes two arguments: date in string format and output format.
 * E.g.:
 * param1: "2031-05-20"
 * param2: "MM/dd/yyyy"
 */
@Component
public class FormatDateCommand implements ExpressionCommand {

    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 2, "Two parameters were expected");
        isInstanceOf(String.class, params[0], "First parameter must be string");
        isInstanceOf(String.class, params[1], "Second parameter must be string");

        LocalDate input = LocalDate.parse((String)params[0]);
        String dateFormat  = (String)params[1];
        DateTimeFormatter dtf  = DateTimeFormatter.ofPattern(dateFormat);
        return dtf.format(input);
    }
}
