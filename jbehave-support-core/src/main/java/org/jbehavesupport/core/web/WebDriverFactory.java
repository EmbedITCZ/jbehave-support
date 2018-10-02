package org.jbehavesupport.core.web;

import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Interface that allows to supply custom WebDriver to tests.
 */
public interface WebDriverFactory {

    RemoteWebDriver createWebDriver();

    String getName();

}
