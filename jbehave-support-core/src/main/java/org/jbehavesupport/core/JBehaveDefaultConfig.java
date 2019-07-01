package org.jbehavesupport.core;

import org.jbehavesupport.core.expression.ExpressionCommand;
import org.jbehavesupport.core.expression.ExpressionEvaluator;
import org.jbehavesupport.core.internal.ConditionalOnMissingBean;
import org.jbehavesupport.core.internal.TestContextImpl;
import org.jbehavesupport.core.internal.expression.ExpressionEvaluatorImpl;
import org.jbehavesupport.core.internal.parameterconverters.ExamplesEvaluationTableConverter;
import org.jbehavesupport.core.internal.verification.VerifierResolverImpl;
import org.jbehavesupport.core.internal.web.WebElementLocatorImpl;
import org.jbehavesupport.core.internal.web.WebElementRegistryImpl;
import org.jbehavesupport.core.internal.web.action.WebActionBuilderImpl;
import org.jbehavesupport.core.internal.web.action.WebActionResolverImpl;
import org.jbehavesupport.core.internal.web.by.ByFactoryResolverImpl;
import org.jbehavesupport.core.internal.web.property.WebPropertyResolverImpl;
import org.jbehavesupport.core.internal.web.waitcondition.WebWaitConditionResolverImpl;
import org.jbehavesupport.core.internal.web.webdriver.WebDriverDelegatingInterceptor;
import org.jbehavesupport.core.internal.web.webdriver.WebDriverFactoryResolverImpl;
import org.jbehavesupport.core.support.TimeFacade;

import org.jbehavesupport.core.verification.Verifier;
import org.jbehavesupport.core.verification.VerifierResolver;
import org.jbehavesupport.core.web.ByFactory;
import org.jbehavesupport.core.web.ByFactoryResolver;
import org.jbehavesupport.core.web.WebAction;
import org.jbehavesupport.core.web.WebActionBuilder;
import org.jbehavesupport.core.web.WebActionResolver;
import org.jbehavesupport.core.web.WebDriverFactory;
import org.jbehavesupport.core.web.WebDriverFactoryResolver;
import org.jbehavesupport.core.web.WebElementLocator;
import org.jbehavesupport.core.web.WebElementRegistry;
import org.jbehavesupport.core.web.WebProperty;
import org.jbehavesupport.core.web.WebPropertyResolver;
import org.jbehavesupport.core.web.WebWaitCondition;
import org.jbehavesupport.core.web.WebWaitConditionResolver;
import org.openqa.selenium.WebDriver;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.List;
import java.util.Map;

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
    @ConditionalOnMissingBean(TestContext.class)
    public TestContext testContext(ConversionService conversionService) {
        return new TestContextImpl(conversionService);
    }

    @Bean
    @ConditionalOnMissingBean(WebActionBuilder.class)
    public WebActionBuilder webActionBuilder(ExamplesEvaluationTableConverter examplesTableConverter) {
        return new WebActionBuilderImpl(examplesTableConverter);
    }

    @Bean
    @ConditionalOnMissingBean(WebActionResolver.class)
    public WebActionResolver webActionResolver(List<WebAction> actions) {
        return new WebActionResolverImpl(actions);
    }

    @Bean
    @ConditionalOnMissingBean(ByFactoryResolver.class)
    public ByFactoryResolver byFactoryResolver(List<ByFactory> byFactories) {
        return new ByFactoryResolverImpl(byFactories);
    }

    @Bean
    @ConditionalOnMissingBean(WebPropertyResolver.class)
    public WebPropertyResolver webPropertyResolver(List<WebProperty> properties) {
        return new WebPropertyResolverImpl(properties);
    }

    @Bean
    @ConditionalOnMissingBean(WebWaitConditionResolver.class)
    public WebWaitConditionResolver webWaitConditionResolver(List<WebWaitCondition> waitConditions) {
        return new WebWaitConditionResolverImpl(waitConditions);
    }

    @Bean
    @ConditionalOnMissingBean(WebDriverFactoryResolver.class)
    public WebDriverFactoryResolver webDriverFactoryResolver(List<WebDriverFactory> webDriverFactories) {
        return new WebDriverFactoryResolverImpl(webDriverFactories);
    }

    @Bean
    @ConditionalOnMissingBean(WebElementLocator.class)
    public WebElementLocator webElementLocator(WebDriver driver, WebElementRegistry elementRegistry) {
        return new WebElementLocatorImpl(driver, elementRegistry);
    }

    @Bean
    @ConditionalOnMissingBean(ExpressionEvaluator.class)
    public ExpressionEvaluator expressionEvaluator(Map<String, ExpressionCommand> commands) {
        return new ExpressionEvaluatorImpl(commands);
    }

    @Bean
    @ConditionalOnMissingBean(WebElementRegistry.class)
    public WebElementRegistry webElementRegistry() {
        return new WebElementRegistryImpl();
    }

    @Bean
    @ConditionalOnMissingBean(VerifierResolver.class)
    public VerifierResolver verifierResolver(List<Verifier> verifiers) {
        return new VerifierResolverImpl(verifiers);
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
