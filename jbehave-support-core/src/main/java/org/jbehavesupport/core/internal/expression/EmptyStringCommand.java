package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isTrue;

import org.jbehavesupport.core.expression.ExpressionCommand;

import org.springframework.stereotype.Component;

/**
 * Create empty string.
 */
@Component
public class EmptyStringCommand implements ExpressionCommand {

    public static final String EMPTY_STRING = "";

    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 0, "No arguments are expected");
        return EMPTY_STRING;
    }
}
