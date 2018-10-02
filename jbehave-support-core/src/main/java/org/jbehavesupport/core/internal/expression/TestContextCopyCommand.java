package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;

import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.expression.ExpressionCommand;
import org.jbehavesupport.core.support.TestContextUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Get value from test context.
 * Command consumes two parameters:
 * params[0] - key in test context
 * params[1] - optional - prefix which will be added to result
 */
@Component
@RequiredArgsConstructor
public class TestContextCopyCommand implements ExpressionCommand {

    private final TestContext testContext;

    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 1 || params.length == 2, "Only one or two parameters were expected");
        isInstanceOf(String.class, params[0], "First param must be string");

        String key = (String)params[0];
        String prefix = "";
        if (params.length == 2) {
            isInstanceOf(String.class, params[1], "Second param must be string");
            prefix = (String )params[1];
        }

        Object value = testContext.get(key);
        return value != null ? prefix + TestContextUtil.escape(value) : NullCommand.NULL_VALUE;
    }

}
