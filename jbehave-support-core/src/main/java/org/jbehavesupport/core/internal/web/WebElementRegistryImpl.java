package org.jbehavesupport.core.internal.web;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;

import org.jbehavesupport.core.web.WebElementRegistry;

import org.openqa.selenium.By;

public class WebElementRegistryImpl implements WebElementRegistry {

    private static final String ID_PREFIX = "#";

    private final Map<String, Map<String, By>> pageRegistry = new HashMap<>();

    @Override
    public By getLocator(String pageName, String elementName) {
        requireNonNull(pageName, "Parameter pageName is required.");
        requireNonNull(elementName, "Parameter elementName is required.");
        Map<String, By> elementRegistry = pageRegistry.get(pageName);

        if (isNull(elementRegistry) && elementName.startsWith(ID_PREFIX)) {
            return By.cssSelector(elementName);
        }
        requireNonNull(elementRegistry, "Unable to find any element locators for page [" + pageName + "].");

        By by = elementRegistry.get(elementName);
        if (isNull(by) && elementName.startsWith(ID_PREFIX)) {
            by = By.cssSelector(elementName);
        }
        requireNonNull(by, "Unable to find element locator for page [" + pageName + "] and element [" + elementName + "].");
        return by;
    }

    @Override
    public void registerLocator(String pageName, String elementName, By locator) {
        requireNonNull(pageName, "Parameter pageName is required.");
        requireNonNull(elementName, "Parameter elementName is required.");
        requireNonNull(locator, "Parameter locator is required.");
        Map<String, By> elementRegistry = pageRegistry.computeIfAbsent(pageName, p -> new HashMap<>());
        if (elementName.equals("@url") || elementName.equals("@title")) {
            throw new IllegalStateException("Element [" + elementName + "] is predefined and can't be defined manually.");
        }
        if(elementRegistry.put(elementName, locator) != null){
            throw new IllegalStateException("Web elements mapping contains duplicate key [" + elementName + "] for page [" + pageName + "].");
        }
    }

}
