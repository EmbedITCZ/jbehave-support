package org.jbehavesupport.core.internal.web;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * DummyWebElements is used as WebElement representation of url and title of page
 * It allows web steps to work with @url and @title default elements
 */
public class DummyWebElement implements WebElement {

    DummyWebElement(String text){
        this.text = text;
    }

    private String text;

    @Override
    public void click() {
        //noop
    }

    @Override
    public void submit() {
        //noop
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        //noop
    }

    @Override
    public void clear() {
        //noop
    }

    @Override
    public String getTagName() {
        return null;
    }

    @Override
    public String getAttribute(String name) {
        if(name.equalsIgnoreCase("value") || name.equalsIgnoreCase("text")){
            return text;
        }
        return null;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public List<WebElement> findElements(By by) {
        return new ArrayList<>();
    }

    @Override
    public WebElement findElement(By by) {
        return null;
    }

    @Override
    public boolean isDisplayed() {
        return true;
    }

    @Override
    public Point getLocation() {
        return null;
    }

    @Override
    public Dimension getSize() {
        return null;
    }

    @Override
    public Rectangle getRect() {
        return null;
    }

    @Override
    public String getCssValue(String propertyName) {
        return null;
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) {
        return null;
    }
}
