package org.jbehavesupport.core.internal.web.webdriver;

import org.jbehavesupport.core.web.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ChromeWebDriverFactory implements WebDriverFactory {

    private static final String NAME = "chrome";

    @Value("${web.timeout:10}")
    private int timeout;

    @Value("${web.browser.driver.location:#{null}}")
    private String browserDriverLocation;

    @Value("${web.browser.version:#{null}}")
    private String browserVersion;

    @Value("${web.browser.driver.port:#{null}}")
    private Integer browserPort;

    @Value("${web.browser.driver.startup.arguments:#{null}}")
    private String browserStartupArguments;

    @Value("${web.browser.binary.location:#{null}}")
    private String binaryLocation;

    private RemoteWebDriver driver = null;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public RemoteWebDriver createWebDriver() {
        createChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(timeout));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(timeout));
        return driver;
    }

    private void createChromeDriver() {
        if (browserDriverLocation != null) {
            System.setProperty("webdriver.chrome.driver", browserDriverLocation);
        }

        ChromeOptions options = new ChromeOptions();

        if (browserVersion != null){
            options.setBrowserVersion(browserVersion);
        }

        if (browserStartupArguments != null) {
            options.addArguments(browserStartupArguments.split("\\s+"));
        } else {
            options.addArguments("--start-maximized");
        }

        if (binaryLocation != null) {
            options.setBinary(binaryLocation);
        }

        ChromeDriverService.Builder driverServiceBuilder = new ChromeDriverService.Builder();
        if (browserPort != null) {
            driverServiceBuilder.usingPort(browserPort);
        } else {
            driverServiceBuilder.usingAnyFreePort();
        }

        driver = new ChromeDriver(driverServiceBuilder.build(), options);
    }

    @Override
    public Class<? extends WebDriver> getProxyClass() {
        return ChromeDriver.class;
    }

}
