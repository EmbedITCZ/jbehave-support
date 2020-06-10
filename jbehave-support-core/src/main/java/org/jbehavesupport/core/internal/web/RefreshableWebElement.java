package org.jbehavesupport.core.internal.web;

import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;
import org.openqa.selenium.internal.HasIdentity;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.interactions.Locatable;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps RemoteWebElement to handle {@link StaleElementReferenceException}.
 */
public class RefreshableWebElement implements WebElement, FindsByLinkText, FindsById, FindsByName, FindsByTagName,
    FindsByClassName, FindsByCssSelector, FindsByXPath, WrapsDriver, HasIdentity, TakesScreenshot, Locatable,
    WrapsElement {

    private static final int ITERATIONS = 4;

    @Delegate(excludes = WebElement.class)
    private RemoteWebElement webElement;

    private final WebElementLocatorImpl webElementLocator;
    private final String webElementName;
    private final String webElementPage;

    public RefreshableWebElement(WebElementLocatorImpl elementLocator, RemoteWebElement element, String name, String page){
        webElementLocator = elementLocator;
        webElement = element;
        webElementName = name;
        webElementPage = page;
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public void click() {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                webElement.click();
                break;
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public void submit() {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                webElement.submit();
                break;
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public void sendKeys(CharSequence... keysToSend) {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                webElement.sendKeys(keysToSend);
                break;
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public void clear() {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                webElement.clear();
                break;
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public String getTagName() {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                return webElement.getTagName();
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
        return null;
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public String getAttribute(String name) {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                return webElement.getAttribute(name);
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
        return null;
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public boolean isSelected() {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                return webElement.isSelected();
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
        return false;
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public boolean isEnabled() {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                return webElement.isEnabled();
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
        return false;
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public String getText() {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                return webElement.getText();
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
        return null;
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public List<WebElement> findElements(By by) {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                return webElement.findElements(by);
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
        return new ArrayList<>();
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public WebElement findElement(By by) {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                return webElement.findElement(by);
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
        return null;
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public boolean isDisplayed() {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                return webElement.isDisplayed();
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
        return false;
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public Point getLocation() {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                return webElement.getLocation();
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
        return null;
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public Dimension getSize() {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                return webElement.getSize();
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
        return null;
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public Rectangle getRect() {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                return webElement.getRect();
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
        return null;
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public String getCssValue(String propertyName) {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                return webElement.getCssValue(propertyName);
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
        return null;
    }

    @SneakyThrows(InterruptedException.class)
    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        for (int i = 0; i < ITERATIONS; i++){
            try {
                return webElement.getScreenshotAs(target);
            } catch (StaleElementReferenceException e) {
                if(i == ITERATIONS - 1){
                    throw e;
                }
                Thread.sleep(1000);
                renewElement();
            }
        }
        return null;
    }

    private void renewElement(){
        webElement = webElementLocator.findPureElement(webElementPage, webElementName);
    }

    @Override
    public String toString() {
        return webElement.toString();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof WebElement) {
            return webElement.equals(o);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode(){
        return webElement.hashCode();
    }

    @Override
    public WebElement getWrappedElement() {
        return webElement;
    }
}
