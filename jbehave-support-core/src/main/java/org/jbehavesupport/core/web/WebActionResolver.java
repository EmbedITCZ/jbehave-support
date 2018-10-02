package org.jbehavesupport.core.web;

/**
 * Strategy for resolving {@link WebAction} by given name.
 */
public interface WebActionResolver {

    /**
     * Returns {@link WebAction} by given name or throws exception.
     */
    WebAction resolveAction(String actionName);

}
