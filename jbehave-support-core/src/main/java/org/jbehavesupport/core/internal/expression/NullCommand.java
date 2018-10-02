package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isTrue;

import org.jbehavesupport.core.expression.ExpressionCommand;
import org.jbehavesupport.core.internal.parameterconverters.NullStringConverter;

import org.springframework.stereotype.Component;

/**
 * The type Null command.
 * Null command returns string "&lt;nullValue&gt;"
 * Usually this string should be converted to null via {@link NullStringConverter}.
 */
@Component
public class NullCommand implements ExpressionCommand {

    public static final String NULL_VALUE = "<nullValue>";

    @Override
    public String execute(Object... params) {
        isTrue(params.length == 0, "No parameters are expected");
        return NULL_VALUE;
    }

}
