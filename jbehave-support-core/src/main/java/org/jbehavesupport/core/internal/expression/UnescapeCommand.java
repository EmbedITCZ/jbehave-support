package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;

import org.jbehavesupport.core.expression.ExpressionCommand;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

/**
 * Unescape command can be used for sending special literals such as whitespaces (e.g. \n, \t)
 */
@Component
public class UnescapeCommand implements ExpressionCommand {

    @Override
    public String execute(Object... params) {
        isTrue(params.length == 1, "Only one parameter was expected");
        isInstanceOf(String.class, params[0], "First parameter must be string");

        String input = (String) params[0];
        return StringEscapeUtils.unescapeJava(input);
    }

}
