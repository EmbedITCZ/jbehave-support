package org.jbehavesupport.core.web;

/**
 * Interface represents some property or value currently present on the web page.
 */
public interface WebProperty<T> {

    /**
     * Returns symbolic name of the property, eg. TEXT or VALUE.
     */
    String name();

    /**
     * Returns actual value.
     */
    T value(WebPropertyContext ctx);

}
