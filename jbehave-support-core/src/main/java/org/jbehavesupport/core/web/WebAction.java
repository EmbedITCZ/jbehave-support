package org.jbehavesupport.core.web;

/**
 * Interface represents user action on web page.
 */
public interface WebAction {

    /**
     * Returns symbolic name of the action, eg. CLICK.
     */
    String name();

    /**
     * Performs this action.
     */
    void perform(WebActionContext ctx);

}
