package org.jbehavesupport.core.internal.web.webdriver;

import javax.annotation.PreDestroy;

import org.jbehavesupport.core.web.WebDriverFactoryResolver;

import lombok.experimental.Delegate;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebDriverDelegate implements WebDriver, JavascriptExecutor,
    FindsById, FindsByClassName, FindsByLinkText, FindsByName,
    FindsByCssSelector, FindsByTagName, FindsByXPath,
    HasInputDevices, HasCapabilities, TakesScreenshot {

    @Autowired
    private WebDriverFactoryResolver webDriverFactoryResolver;

    private RemoteWebDriver driver = null;

    @Delegate(excludes = {Quit.class, TakesScreenshot.class})
    private RemoteWebDriver getDelegate() {
        if (driver == null) {
            driver = webDriverFactoryResolver.resolveWebDriverFactory().createWebDriver();
        }
        return driver;
    }

    @PreDestroy
    public void quit() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    @Override
    public <T> T getScreenshotAs(final OutputType<T> outputType) {
        if (driver != null) {
            return driver.getScreenshotAs(outputType);
        }

        return null;
    }

    public boolean isInitialized() {
        return driver != null;
    }

    private interface Quit {
        void quit();
    }

}
