package org.jbehavesupport.core.internal.web;

import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Wraps RemoteWebElement to handle {@link StaleElementReferenceException}.
 */
@AllArgsConstructor
public class RefreshableWebElementInterceptor implements MethodInterceptor {

    private static final int ITERATIONS = 4;

    private RemoteWebElement webElement;
    private final WebElementLocatorImpl webElementLocator;
    private final String webElementName;
    private final String webElementPage;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method proxyMethod = ReflectionUtils.findMethod(this.webElement.getClass(), invocation.getMethod().getName());
        if (proxyMethod != null) {
            if (shouldHandleStaleElement(proxyMethod)) {
                for (int i = 0; i < ITERATIONS; i++) {
                    try {
                        return proxyMethod.invoke(this.webElement, invocation.getArguments());
                    } catch (InvocationTargetException e) {
                        if (e.getTargetException().getClass() != StaleElementReferenceException.class || i == ITERATIONS - 1) {
                            throw e.getTargetException();
                        }
                        Thread.sleep(1000);
                        renewElement();
                    }
                }
            } else {
                return proxyMethod.invoke(this.webElement, invocation.getArguments());
            }
        }
        return invocation.proceed();
    }

    private boolean shouldHandleStaleElement(Method method) {
        return !(isSpecificMethod(method, "toString", String.class) || isSpecificMethod(method, "equals", boolean.class)
            || isSpecificMethod(method, "hashCode", int.class) || isSpecificMethod(method, "getWrappedElement", WebElement.class));
    }

    private void renewElement(){
        webElement = webElementLocator.findPureElement(webElementPage, webElementName);
    }

    private boolean isSpecificMethod(Method method, String methodName, Class<?> returnType) {
        return method.getName().equals(methodName) && method.getReturnType().equals(returnType);
    }

}
