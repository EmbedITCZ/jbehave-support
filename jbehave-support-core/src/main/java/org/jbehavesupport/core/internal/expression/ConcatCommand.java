package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isTrue;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.jbehavesupport.core.expression.ExpressionCommand;

import org.springframework.stereotype.Component;

/**
 * Concat two or more strings.
 * We are calling explicit {@link String#valueOf} for getting string to concat.
 */
@Component
public class ConcatCommand implements ExpressionCommand {

    @Override
    public Object execute(Object... params) {
        isTrue(params.length >= 2, "At least two parameters were expected");

        return Arrays.stream(params)
            .map(String::valueOf)
            .collect(Collectors.joining());
    }
}
