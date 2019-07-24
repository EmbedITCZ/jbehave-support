package org.jbehavesupport.core.internal.web.webdriver;

import javax.annotation.PreDestroy;

import org.aopalliance.intercept.MethodInvocation;
import org.jbehavesupport.core.web.WebDriverFactory;
import org.jbehavesupport.core.web.WebDriverFactoryResolver;
import org.openqa.selenium.WebDriver;
import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.IntroductionInfoSupport;
import org.springframework.util.ClassUtils;

/**
 * Based on {@link org.springframework.aop.support.DelegatingIntroductionInterceptor}
 */
public class WebDriverDelegatingInterceptor extends IntroductionInfoSupport
    implements IntroductionInterceptor {


    private transient WebDriverFactoryResolver webDriverFactoryResolver;

    private transient WebDriver driver = null;

    public WebDriverDelegatingInterceptor(WebDriverFactoryResolver webDriverFactoryResolver) {
        this.webDriverFactoryResolver = webDriverFactoryResolver;
        init();
    }

    private void init() {
        initInterfaces(webDriverFactoryResolver.resolveWebDriverFactory());
    }

    private void initInterfaces(WebDriverFactory webDriverFactory) {
        publishedInterfaces.clear();
        publishedInterfaces.addAll(webDriverFactory.getProxyInterfaces());
        publishedInterfaces.addAll(ClassUtils.getAllInterfacesForClassAsSet(webDriverFactory.getProxyClass()));

        // We don't want to expose the control interface
        suppressInterface(IntroductionInterceptor.class);
        suppressInterface(DynamicIntroductionAdvice.class);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        if (isTakeScreenShotMethod(mi) || isQuitMethod(mi) || isCloseMethod(mi)) {
            if (driver == null) {
                return null;
            }
        } else if (isEqualsMethod(mi)) {
            Object argument = mi.getArguments()[0];
            return driver != null ? driver.equals(argument) : equals(argument);
        } else if (isToStringMethod(mi)) {
            return driver != null ? driver.toString() : toString();
        } else if (isHashCodeMethod(mi)) {
            return hashCode();
        }

        return doProceed(mi);
    }

    private Object doProceed(final MethodInvocation mi) throws Throwable {
        if (driver == null) {
            WebDriverFactory webDriverFactory = webDriverFactoryResolver.resolveWebDriverFactory();
            initInterfaces(webDriverFactory);
            driver = webDriverFactory.createWebDriver();
        }

        // Using the following method rather than direct reflection, we
        // get correct handling of InvocationTargetException
        // if the introduced method throws an exception.
        Object retVal = AopUtils.invokeJoinpointUsingReflection(driver, mi.getMethod(), mi.getArguments());

        // Massage return value if possible: if the delegate returned itself,
        // we really want to return the proxy.
        if (retVal == driver && mi instanceof ProxyMethodInvocation) {
            Object proxy = ((ProxyMethodInvocation) mi).getProxy();
            if (mi.getMethod().getReturnType().isInstance(proxy)) {
                retVal = proxy;
            }
        }

        if (isQuitMethod(mi)) {
            driver = null;
        }

        return retVal;
    }

    @PreDestroy
    public void quit() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    private boolean isQuitMethod(MethodInvocation mi) {
        return isSpecificMethod(mi, "quit", void.class);
    }

    private boolean isCloseMethod(MethodInvocation mi) {
        return isSpecificMethod(mi, "close", void.class);
    }

    private boolean isTakeScreenShotMethod(MethodInvocation mi) {
        return mi.getMethod().getName().equals("getScreenshotAs");
    }

    private boolean isToStringMethod(MethodInvocation mi) {
        return isSpecificMethod(mi, "toString", String.class);
    }

    private boolean isEqualsMethod(MethodInvocation mi) {
        return isSpecificMethod(mi, "equals", boolean.class);
    }

    private boolean isHashCodeMethod(MethodInvocation mi) {
        return isSpecificMethod(mi, "hashCode", int.class);
    }

    private boolean isSpecificMethod(MethodInvocation mi, String methodName, Class returnType) {
        return mi.getMethod().getName().equals(methodName) && mi.getMethod().getReturnType().equals(returnType);
    }

}
