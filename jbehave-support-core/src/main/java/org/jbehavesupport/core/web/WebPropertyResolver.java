package org.jbehavesupport.core.web;

/**
 * Strategy for resolving {@link WebProperty} by given name.
 */
public interface WebPropertyResolver {

    /**
     * Returns {@link WebProperty} by given name or throws exception.
     */
    <T> WebProperty<T> resolveProperty(String propertyName);

}
