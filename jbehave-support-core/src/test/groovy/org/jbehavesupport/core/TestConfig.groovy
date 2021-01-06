package org.jbehavesupport.core

import com.splunk.SSLSecurityProtocol
import org.jbehave.core.configuration.MostUsefulConfiguration
import org.jbehavesupport.core.healthcheck.HealthCheck
import org.jbehavesupport.core.healthcheck.HealthCheckSteps
import org.jbehavesupport.core.internal.FileNameResolver
import org.jbehavesupport.core.internal.expression.BytesCommand
import org.jbehavesupport.core.internal.parameterconverters.ExamplesEvaluationTableConverter
import org.jbehavesupport.core.internal.verification.RegexVerifier
import org.jbehavesupport.core.internal.web.WebScreenshotCreator
import org.jbehavesupport.core.internal.web.by.XpathByFactory
import org.jbehavesupport.core.rest.RestServiceHandler
import org.jbehavesupport.core.internal.web.webdriver.WebDriverDelegatingInterceptor
import org.jbehavesupport.core.splunk.SplunkConfig
import org.jbehavesupport.core.ssh.RollingLogResolver
import org.jbehavesupport.core.ssh.SimpleRollingLogResolver
import org.jbehavesupport.core.ssh.SshLog
import org.jbehavesupport.core.ssh.SshSetting
import org.jbehavesupport.core.ssh.SshTemplate
import org.jbehavesupport.core.support.YamlPropertySourceFactory
import org.jbehavesupport.core.test.app.oxm.NameRequest
import org.jbehavesupport.core.test.app.oxm.NameResponse
import org.jbehavesupport.core.verification.Verifier
import org.jbehavesupport.core.web.ByFactory
import org.jbehavesupport.core.web.WebDriverFactoryResolver
import org.jbehavesupport.core.ws.WebServiceEndpointRegistry
import org.jbehavesupport.core.ws.WebServiceHandler
import org.jbehavesupport.test.support.TestWebServiceHandler
import org.openqa.selenium.WebDriver
import org.springframework.aop.framework.ProxyFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment

import static java.util.Objects.nonNull
import static org.mockito.Mockito.when

import java.time.ZonedDateTime
import static org.mockito.Mockito.mock
import static org.mockito.ArgumentMatchers.any

import javax.annotation.PostConstruct
import java.util.concurrent.RejectedExecutionException

@Configuration
@ComponentScan
@PropertySource(value = "test.yml", factory = YamlPropertySourceFactory.class)
class TestConfig {

    @Autowired
    private Environment env

    @Autowired
    private ApplicationContext applicationContext

    @PostConstruct
    void configuration() {
        applicationContext.getBean(ExamplesEvaluationTableConverter.class).setConfiguration(new MostUsefulConfiguration())
    }

    @Bean
    HealthCheckSteps healthCheckSteps(ConfigurableListableBeanFactory beanFactory) {
        return new HealthCheckSteps(beanFactory)
    }

    @Bean
    BytesCommand bytesCommand(TestContext testContext) {
        return new BytesCommand(testContext)
    }

    @Bean
    WebDriver driver(WebDriverFactoryResolver webDriverFactoryResolver) {
        ProxyFactory proxyFactory = new ProxyFactory(WebDriver.class, new WebDriverDelegatingInterceptor(webDriverFactoryResolver))
        proxyFactory.setProxyTargetClass(true)
        proxyFactory.setTargetClass(webDriverFactoryResolver.resolveWebDriverFactory().getProxyClass())
        return (WebDriver) proxyFactory.getProxy()
    }

    @Bean
    WebScreenshotCreator webScreenshotCreator(WebDriver driver, TestContext testContext, FileNameResolver fileNameResolver) {
        return new WebScreenshotCreator(driver, testContext, fileNameResolver)
    }

    @Bean
    @Qualifier("HEALTHY")
    HealthCheck fitHealthCheck() {
        return new HealthCheck() {
            @Override
            void check() {
            }
        }
    }

    @Bean
    @Qualifier("SICK")
    HealthCheck sickHealthCheck() {
        return new HealthCheck() {
            @Override
            void check() {
                throw new RejectedExecutionException("I am very sick")
            }
        }
    }

    @Bean
    @Qualifier("TEST")
    Verifier anotherRegexVerifier() {
        return new RegexVerifier()
    }

    @Bean
    @Qualifier("TEST")
    ByFactory anotherXpathByFactory() {
        return new XpathByFactory()
    }

    @Bean
    @Qualifier("TEST")
    SshTemplate sshTemplate() throws IOException {
        SshSetting passwordSetting = SshSetting.builder()
            .hostname(env.getProperty("ssh.hostname"))
            .user(env.getProperty("ssh.credentials.user"))
            .password(env.getProperty("ssh.credentials.password"))
            .port(Integer.parseInt(env.getProperty("ssh.port")))
            .logPath(env.getProperty("ssh.logPath"))
            .build()

        RollingLogResolver rollingLogResolver = new SimpleRollingLogResolver()
        return new SshTemplate(passwordSetting, env.getProperty("ssh.timestampFormat"), rollingLogResolver)
    }

    @Bean
    @Qualifier("MOCK_TEMPLATE")
    SshTemplate mockSshTemplate() throws IOException {
        def sshLogOne = new SshLog("Log is from cache", null)
        def sshLogTwo = new SshLog("New log without cache", null)
        SshTemplate sshTemplate = mock(SshTemplate.class)
        when(sshTemplate.copyLog(any(ZonedDateTime.class), any(ZonedDateTime.class))).thenReturn(sshLogOne, sshLogTwo)
        return sshTemplate
    }

    @Bean
    @Qualifier("TEST")
    WebServiceHandler requestFactoryTestHandler() {
        return new TestWebServiceHandler(env) {
            @Override
            protected void initEndpoints(WebServiceEndpointRegistry endpointRegistry) {
                endpointRegistry.register(RequestFactoryTest.Foo.class, RequestFactoryTest.Foo.class)
                endpointRegistry.register(RequestFactoryTest.Foo.class, "MyClass", RequestFactoryTest.Foo.class, "MyClass")
                endpointRegistry.register(NameRequest.class, NameResponse.class)
                endpointRegistry.register(RequestFactoryTest.Ugly.class, RequestFactoryTest.Ugly.class)
                endpointRegistry.register(UnsupportedClass.class, UnsupportedClass.class)
            }
        }
    }

    @Bean
    RestServiceHandler testRestServiceHandler() {
        return new RestServiceHandler(env.getProperty("rest.url"))
    }

    @Bean
    SplunkConfig splunkConfig() throws IOException {
        return SplunkConfig.builder()
            .host(env.getProperty("splunk.host"))
            .port(Integer.parseInt(env.getProperty("splunk.port")))
            .scheme(env.getProperty("splunk.scheme"))
            .sslSecurityProtocol(nonNull(env.getProperty("splunk.sslSecurityProtocol")) ? SSLSecurityProtocol.valueOf(env.getProperty("splunk.sslSecurityProtocol")) : null)
            .username(env.getProperty("splunk.credentials.username"))
            .password(env.getProperty("splunk.credentials.password"))
            .token(env.getProperty("splunk.credentials.token"))
            .build();
    }
}
