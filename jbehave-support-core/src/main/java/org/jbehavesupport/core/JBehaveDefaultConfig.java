package org.jbehavesupport.core;

import org.jbehavesupport.core.internal.ConditionalOnMissingBean;
import org.jbehavesupport.core.internal.web.webdriver.WebDriverDelegatingInterceptor;
import org.jbehavesupport.core.support.TimeFacade;

import org.jbehavesupport.core.web.WebDriverFactory;
import org.jbehavesupport.core.web.WebDriverFactoryResolver;
import org.openqa.selenium.WebDriver;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
@ComponentScan(basePackages = "org.jbehavesupport")
public class JBehaveDefaultConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public TimeFacade timeFacade() {
        return TimeFacade.getDefault();
    }

    @Bean
    @ConditionalOnMissingBean(ConfigurableConversionService.class)
    public ConfigurableConversionService conversionService() {
        return new DefaultConversionService();
    }

    @Bean
    public WebDriver webDriver(WebDriverFactoryResolver webDriverFactoryResolver) {
        WebDriverFactory webDriverFactory = webDriverFactoryResolver.resolveWebDriverFactory();
        ProxyFactory proxyFactory = new ProxyFactory(WebDriver.class, new WebDriverDelegatingInterceptor(webDriverFactory));
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.setTargetClass(webDriverFactory.getProxyClass());
        return (WebDriver) proxyFactory.getProxy();
    }

}
