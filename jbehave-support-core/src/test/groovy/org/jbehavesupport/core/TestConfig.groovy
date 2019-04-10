package org.jbehavesupport.core

import org.jbehave.core.configuration.MostUsefulConfiguration
import org.jbehavesupport.core.healthcheck.HealthCheck
import org.jbehavesupport.core.healthcheck.HealthCheckSteps
import org.jbehavesupport.core.internal.parameterconverters.ExamplesEvaluationTableConverter
import org.jbehavesupport.core.internal.verification.RegexVerifier
import org.jbehavesupport.core.internal.web.by.XpathByFactory
import org.jbehavesupport.core.ssh.RollingLogResolver
import org.jbehavesupport.core.ssh.SimpleRollingLogResolver
import org.jbehavesupport.core.ssh.SshSetting
import org.jbehavesupport.core.ssh.SshTemplate
import org.jbehavesupport.core.support.YamlPropertiesConfigurer
import org.jbehavesupport.core.test.app.oxm.NameRequest
import org.jbehavesupport.core.test.app.oxm.NameResponse
import org.jbehavesupport.core.verification.Verifier
import org.jbehavesupport.core.web.ByFactory
import org.jbehavesupport.core.ws.WebServiceEndpointRegistry
import org.jbehavesupport.core.ws.WebServiceHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

import javax.annotation.PostConstruct
import java.util.concurrent.RejectedExecutionException

@Configuration
@ComponentScan
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
    static YamlPropertiesConfigurer yamlPropertiesConfigurer() {
        return new YamlPropertiesConfigurer("test.yml")
    }

    @Bean
    HealthCheckSteps healthCheckSteps() {
        return new HealthCheckSteps()
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
        return new RegexVerifier();
    }

    @Bean
    @Qualifier("TEST")
    ByFactory anotherXpathByFactory() {
        return new XpathByFactory();
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
            .build();

        RollingLogResolver rollingLogResolver = new SimpleRollingLogResolver();
        return new SshTemplate(passwordSetting, env.getProperty("ssh.timestampFormat"), rollingLogResolver);
    }

    @Bean
    @Qualifier("TEST")
    WebServiceHandler requestFactoryTestHandler() {
        return new WebServiceHandler() {
            @Override
            protected void initEndpoints(WebServiceEndpointRegistry endpointRegistry) {
                endpointRegistry.register(RequestFactoryTest.Foo.class, RequestFactoryTest.Foo.class);
                endpointRegistry.register(RequestFactoryTest.Foo.class, "MyClass", RequestFactoryTest.Foo.class, "MyClass");
                endpointRegistry.register(NameRequest.class, NameResponse.class);
                endpointRegistry.register(RequestFactoryTest.Ugly.class, RequestFactoryTest.Ugly.class);
                endpointRegistry.register(UnsupportedClass.class, UnsupportedClass.class);
            }
        }
    }
}
