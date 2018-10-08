package org.jbehavesupport.core.expression;

/**
 * The interface represents single command in expression.
 */
public interface ExpressionCommand {

    /**
     * Execute command with params.
     */
    Object execute(Object... params);

}
