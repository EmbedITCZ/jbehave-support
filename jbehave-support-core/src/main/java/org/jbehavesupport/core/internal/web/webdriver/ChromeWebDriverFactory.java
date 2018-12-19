package org.jbehavesupport.core.internal.web.webdriver;

import java.util.concurrent.TimeUnit;

import org.jbehavesupport.core.web.WebDriverFactory;

import io.github.bonigarcia.wdm.BrowserManager;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ChromeWebDriverFactory implements WebDriverFactory {

    private static final String NAME = "chrome";

    @Value("${web.timeout:10}")
    private int timeout;

    @Value("${web.browser.driver.location:#{null}}")
    private String browserDriverLocation;

    @Value("${web.browser.driver.port:#{null}}")
    private Integer browserPort;

    @Value("${web.browser.driver.version:#{null}}")
    private String browserDriverVersion;

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
        createChromeDriver();
        driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
        return driver;
    }

    private void createChromeDriver() {
        if (!driverSetup) {
            if (browserDriverLocation != null) {
                System.setProperty("webdriver.chrome.driver", browserDriverLocation);
            } else {
                BrowserManager browserManager = ChromeDriverManager.getInstance();
                if (browserDriverVersion != null) {
                    browserManager.version(browserDriverVersion);
                }
                browserManager.setup();
            }
            driverSetup = true;
        }

        ChromeOptions options = new ChromeOptions();
        if (browserStartupArguments != null) {
            options.addArguments(browserStartupArguments.split("\\s+"));
        } else {
            options.addArguments("--start-maximized");
        }

        ChromeDriverService.Builder driverServiceBuilder = new ChromeDriverService.Builder();
        if (browserPort != null) {
            driverServiceBuilder.usingPort(browserPort);
        } else {
            driverServiceBuilder.usingAnyFreePort();
        }

        driver = new ChromeDriver(driverServiceBuilder.build(), options);
    }

}
