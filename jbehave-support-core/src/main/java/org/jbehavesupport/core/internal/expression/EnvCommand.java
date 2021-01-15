package org.jbehavesupport.core.internal.expression;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.expression.ExpressionCommand;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;

/**
 * Env command can be used for loading environment variable value.
 */
@Component
@RequiredArgsConstructor
public class EnvCommand implements ExpressionCommand {

    private final Environment environment;

    @Override
    public String execute(Object... params) {
        isTrue(params.length == 1, "Only one parameter was expected");
        isInstanceOf(String.class, params[0], "First parameter must be string");

        String name = (String) params[0];
        return environment.getProperty(name);
    }

}
