package org.jbehavesupport.core.internal.web;

import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * DummyWebElements is used as WebElement representation of url and title of page
 * It allows web steps to work with @url and @title default elements
 * Only text and value properties are allowed.
 */
@AllArgsConstructor
public class DummyWebElement implements WebElement {

    public static final String URL = "@url";
    public static final String TITLE = "@title";
    private static final String UNSUPPORTED_ACTION = "This action is not supported on default elements.";
    private static final String UNSUPPORTED_PROPERTY = "This property is not supported on default elements.";

    private String text;

    @Override
    public void click() {
        throw new UnsupportedOperationException(UNSUPPORTED_ACTION);
    }

    @Override
    public void submit() {
        throw new UnsupportedOperationException(UNSUPPORTED_ACTION);
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        throw new UnsupportedOperationException(UNSUPPORTED_ACTION);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(UNSUPPORTED_ACTION);
    }

    @Override
    public String getTagName() {
        throw new UnsupportedOperationException(UNSUPPORTED_PROPERTY);
    }

    @Override
    public String getAttribute(String name) {
        if(name.equalsIgnoreCase("value") || name.equalsIgnoreCase("text")) {
            return text;
        }
        throw new UnsupportedOperationException(UNSUPPORTED_PROPERTY);
    }

    @Override
    public boolean isSelected() {
        throw new UnsupportedOperationException(UNSUPPORTED_PROPERTY);
    }

    @Override
    public boolean isEnabled() {
        throw new UnsupportedOperationException(UNSUPPORTED_PROPERTY);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public List<WebElement> findElements(By by) {
        throw new UnsupportedOperationException(UNSUPPORTED_ACTION);
    }

    @Override
    public WebElement findElement(By by) {
        throw new UnsupportedOperationException(UNSUPPORTED_ACTION);
    }

    @Override
    public boolean isDisplayed() {
        throw new UnsupportedOperationException(UNSUPPORTED_PROPERTY);
    }

    @Override
    public Point getLocation() {
        throw new UnsupportedOperationException(UNSUPPORTED_PROPERTY);
    }

    @Override
    public Dimension getSize() {
        throw new UnsupportedOperationException(UNSUPPORTED_PROPERTY);
    }

    @Override
    public Rectangle getRect() {
        throw new UnsupportedOperationException(UNSUPPORTED_PROPERTY);
    }

    @Override
    public String getCssValue(String propertyName) {
        throw new UnsupportedOperationException(UNSUPPORTED_PROPERTY);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) {
        throw new UnsupportedOperationException(UNSUPPORTED_ACTION);
    }
}
