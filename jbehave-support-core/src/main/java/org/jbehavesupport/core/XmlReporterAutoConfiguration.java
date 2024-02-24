package org.jbehavesupport.core;

import org.jbehavesupport.core.internal.FileNameResolver;
import org.jbehavesupport.core.report.XmlReporterFactory;
import org.jbehavesupport.core.report.extension.EnvironmentInfoXmlReporterExtension;
import org.jbehavesupport.core.report.extension.FailScreenshotsReporterExtension;
import org.jbehavesupport.core.report.extension.RestXmlReporterExtension;
import org.jbehavesupport.core.report.extension.ScreenshotReporterExtension;
import org.jbehavesupport.core.report.extension.ServerLogXmlReporterExtension;
import org.jbehavesupport.core.report.extension.SqlXmlReporterExtension;
import org.jbehavesupport.core.report.extension.TestContextXmlReporterExtension;
import org.jbehavesupport.core.report.extension.WsXmlReporterExtension;
import org.jbehavesupport.core.rest.RestServiceHandler;
import org.jbehavesupport.core.ssh.SshHandler;
import org.jbehavesupport.core.ssh.SshTemplate;
import org.jbehavesupport.core.ws.WebServiceHandler;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Map;

@AutoConfiguration(after = JBehaveAutoConfiguration.class)
public class XmlReporterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "reporting.enabled", havingValue = "true", matchIfMissing = true)
    public XmlReporterFactory xmlReporterFactory() {
        return new XmlReporterFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(XmlReporterFactory.class)
    @ConditionalOnProperty(name = "reporting.context.enabled", havingValue = "true", matchIfMissing = true)
    public TestContextXmlReporterExtension testContextXmlReporterExtension(TestContext testContext) {
        return new TestContextXmlReporterExtension(testContext);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(XmlReporterFactory.class)
    @ConditionalOnProperty(name = "reporting.web.screenshot.failed.enabled", havingValue = "true", matchIfMissing = true)
    public FailScreenshotsReporterExtension failScreenshotsReporterExtension(TestContext testContext) {
        return new FailScreenshotsReporterExtension(testContext);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(XmlReporterFactory.class)
    @Conditional(ScreenshotReporterCondition.class)
    public ScreenshotReporterExtension screenshotReporterExtension(TestContext testContext) {
        return new ScreenshotReporterExtension(testContext);
    }

    @Bean
    @ConditionalOnBean(XmlReporterFactory.class)
    @ConditionalOnMissingBean
    @ConditionalOnPropertyPrefix(prefix = "environmentInfo")
    @ConditionalOnProperty(name = "reporting.environment.enabled", havingValue = "true", matchIfMissing = true)
    public EnvironmentInfoXmlReporterExtension environmentInfoXmlReporterExtension(Environment environment) {
        return new EnvironmentInfoXmlReporterExtension(environment);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RestServiceHandler.class, XmlReporterFactory.class})
    @ConditionalOnProperty(name = "reporting.rest.enabled", havingValue = "true", matchIfMissing = true)
    public RestXmlReporterExtension restXmlReporterExtension(TestContext testContext, FileNameResolver fileNameResolver) {
        return new RestXmlReporterExtension(testContext, fileNameResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({SshTemplate.class, XmlReporterFactory.class})
    @ConditionalOnProperty(name = "reporting.ssh.enabled", havingValue = "true", matchIfMissing = true)
    public ServerLogXmlReporterExtension serverLogXmlReporterExtension(ConfigurableListableBeanFactory beanFactory, SshHandler sshHandler, TestContext testContext, FileNameResolver fileNameResolver) {
        return new ServerLogXmlReporterExtension(testContext, fileNameResolver, sshHandler, beanFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({DataSource.class, XmlReporterFactory.class})
    @ConditionalOnProperty(name = "reporting.sql.enabled", havingValue = "true", matchIfMissing = true)
    public SqlXmlReporterExtension sqlXmlReporterExtension() {
        return new SqlXmlReporterExtension();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({WebServiceHandler.class, XmlReporterFactory.class})
    @ConditionalOnProperty(name = "reporting.ws.enabled", havingValue = "true", matchIfMissing = true)
    public WsXmlReporterExtension wsXmlReporterExtension() {
        return new WsXmlReporterExtension();
    }

}

class ScreenshotReporterCondition extends NoneNestedConditions {
    public ScreenshotReporterCondition() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(name = "web.screenshot.reporting.mode", havingValue = "FAILED", matchIfMissing = true)
    static class ModeFailedCondition {

    }

    @ConditionalOnProperty(name = "reporting.web.screenshot.enabled", havingValue = "true", matchIfMissing = true)
    static class ReportingEnabledCondition {

    }
}

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnPropertyPrefixCondition.class)
@interface ConditionalOnPropertyPrefix {
    String prefix() default "";

}

class OnPropertyPrefixCondition extends SpringBootCondition {

    private String getPrefix(Map<String, Object> annotationAttributes) {
        String prefix = (String) annotationAttributes.get("prefix");
        if (StringUtils.hasText(prefix) && !prefix.endsWith(".")) {
            prefix = prefix + ".";
        }

        return prefix;
    }

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ConditionalOnPropertyPrefix.class.getName());
        Environment environment = context.getEnvironment();
        String prefix = getPrefix(annotationAttributes);

        boolean match = ((ConfigurableEnvironment) environment).getPropertySources().stream()
            .filter(EnumerablePropertySource.class::isInstance)
            .map(EnumerablePropertySource.class::cast)
            .map(EnumerablePropertySource::getPropertyNames)
            .flatMap(Arrays::stream)
            .anyMatch(propertyName -> propertyName.startsWith(prefix));

        return match ? ConditionOutcome.match() : ConditionOutcome.noMatch("no property with prefix " + prefix + "found");
    }
}
