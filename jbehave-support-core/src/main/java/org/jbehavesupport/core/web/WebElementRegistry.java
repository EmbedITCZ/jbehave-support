package org.jbehavesupport.core.web;

import org.openqa.selenium.By;

/**
 * Interface holds element locators (css, xpath, ...) by their logical name and page.
 */
public interface WebElementRegistry {

    By getLocator(String pageName, String elementName);

    void registerLocator(String pageName, String elementName, By locator);

}
