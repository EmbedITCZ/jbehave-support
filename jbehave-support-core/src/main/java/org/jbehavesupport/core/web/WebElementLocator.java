package org.jbehavesupport.core.web;

import org.openqa.selenium.WebElement;

/**
 * Strategy for finding {@link WebElement} by their logical name and page.
 */
public interface WebElementLocator {

    /**
     * Finds {@link WebElement} by its logical name and page and waits, if necessary, until it is present.
     */
    WebElement findElement(String pageName, String elementName);

    /**
     * Finds {@link WebElement} by its logical name and page and waits, if necessary, until it is clickable (aka visible and enable).
     */
    WebElement findClickableElement(String pageName, String elementName);

}
