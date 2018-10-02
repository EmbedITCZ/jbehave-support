package org.jbehavesupport.core.web;

/**
 * Interface represents some condition that has to be met on the web page
 * to be able to continue with tests, eg. some element is present.
 */
public interface WebWaitCondition {

    /**
     * Returns true if the implementation is able to evaluate given condition otherwise false.
     */
    boolean match(WebWaitConditionContext ctx);

    /**
     * Waits until given condition is met.
     */
    void evaluate(WebWaitConditionContext ctx);

}
