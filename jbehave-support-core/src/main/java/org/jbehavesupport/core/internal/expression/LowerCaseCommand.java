package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;

import org.jbehavesupport.core.expression.ExpressionCommand;

import org.springframework.stereotype.Component;

/**
 * Make string lower case.
 */
@Component
public class LowerCaseCommand implements ExpressionCommand {
    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 1, "Only one parameter was expected");
        isInstanceOf(String.class, params[0], "First parameter must be string");

        String input = (String) params[0];
        return input.toLowerCase();
    }
}
