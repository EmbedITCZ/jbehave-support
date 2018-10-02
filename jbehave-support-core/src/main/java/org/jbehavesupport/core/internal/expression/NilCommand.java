package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isTrue;

import org.jbehavesupport.core.expression.ExpressionCommand;

import org.springframework.stereotype.Component;

/**
 * Nil command can be used as value for JAXBElement, when nillable is set
 */
@Component
public class NilCommand implements ExpressionCommand {

    public static final String NIL = "<nil>";

    @Override
    public String execute(Object... params) {
        isTrue(params.length == 0, "No parameters are expected");
        return NIL;
    }

}
