package org.jbehavesupport.core.web;

import java.util.Collection;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.util.ClassUtils;

/**
 * Interface that allows to supply custom WebDriver to tests.
 */
public interface WebDriverFactory {

    RemoteWebDriver createWebDriver();

    String getName();

    default Class<? extends WebDriver> getProxyClass() {
        return RemoteWebDriver.class;
    }

    default Collection<Class<?>> getProxyInterfaces() {
        return ClassUtils.getAllInterfacesForClassAsSet(RemoteWebDriver.class);
    }

}
