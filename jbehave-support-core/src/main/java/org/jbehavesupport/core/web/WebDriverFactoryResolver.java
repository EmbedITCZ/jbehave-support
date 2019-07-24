package org.jbehavesupport.core.web;

/**
 * Strategy for resolving {@link WebDriverFactory}.
 */
public interface WebDriverFactoryResolver {

    /**
     * Returns {@link WebDriverFactory} which is able to supply new WebDriver.
     */
    WebDriverFactory resolveWebDriverFactory();

    void setBrowserName (String browserName);

}
