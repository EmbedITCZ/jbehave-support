package org.jbehavesupport.core.expression;

/**
 * The interface represents evaluation service for expressions,
 * which can occur in test stories.
 */
public interface ExpressionEvaluator {

    /**
     * Evaluate given expression.
     */
    Object evaluate(String expression);

    /**
     * Register alias for given command, which can be used in expression.
     */
    void registerAlias(String alias, String command);

    void registerShorthand(String shortHand, String command);
}
