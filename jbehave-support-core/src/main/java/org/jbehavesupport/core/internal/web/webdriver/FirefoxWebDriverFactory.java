package org.jbehavesupport.core.internal.web.webdriver;

import java.util.concurrent.TimeUnit;

import io.github.bonigarcia.wdm.DriverManagerType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jbehavesupport.core.web.WebDriverFactory;

import io.github.bonigarcia.wdm.FirefoxDriverManager;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @deprecated kept for backwards compatibility only, should not be used otherwise
 */
@Component
@Deprecated
public class FirefoxWebDriverFactory implements WebDriverFactory {

    private static final String NAME = "firefox47";

    @Value("${web.timeout:10}")
    private int timeout;

    @Value("${web.browser.driver.location:#{null}}")
    private String browserDriverLocation;

    @Value("${web.browser.driver.startup.arguments:#{null}}")
    private String browserStartupArguments;

    private boolean driverSetup = false;

    private RemoteWebDriver driver = null;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public RemoteWebDriver createWebDriver() {
        createFirefox47Driver();
        driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
        return driver;
    }

    private void createFirefox47Driver() {
        if (!driverSetup) {
            if (browserDriverLocation != null) {
                System.setProperty("webdriver.gecko.driver", browserDriverLocation);
            } else {
                WebDriverManager.firefoxdriver().version("v0.8.0").setup();
            }
            driverSetup = true;
        }

        FirefoxOptions options = new FirefoxOptions().setLegacy(true);
        if (browserStartupArguments != null) {
            options.addArguments(browserStartupArguments);
        }
        driver = new FirefoxDriver(options);
    }

}
