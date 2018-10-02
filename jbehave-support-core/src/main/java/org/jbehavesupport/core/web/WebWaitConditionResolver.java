package org.jbehavesupport.core.web;

/**
 * Strategy for resolving {@link WebWaitCondition}.
 */
public interface WebWaitConditionResolver {

    /**
     * Returns {@link WebWaitCondition} which is able to evaluate given condition or throws exception.
     */
    WebWaitCondition resolveWaitCondition(WebWaitConditionContext ctx);

}
