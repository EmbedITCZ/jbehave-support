package org.jbehavesupport.core;


import org.jbehavesupport.core.internal.ConditionalOnBean;
import org.jbehavesupport.core.internal.ConditionalOnProperty;
import org.jbehavesupport.core.internal.FileNameResolver;
import org.jbehavesupport.core.jms.JmsHandler;
import org.jbehavesupport.core.report.XmlReporterFactory;
import org.jbehavesupport.core.report.extension.EnvironmentInfoXmlReporterExtension;
import org.jbehavesupport.core.report.extension.FailScreenshotsReporterExtension;
import org.jbehavesupport.core.report.extension.JmsXmlReporterExtension;
import org.jbehavesupport.core.report.extension.RestXmlReporterExtension;
import org.jbehavesupport.core.report.extension.ScreenshotReporterExtension;
import org.jbehavesupport.core.report.extension.ServerLogXmlReporterExtension;
import org.jbehavesupport.core.report.extension.SplunkXmlReporterExtension;
import org.jbehavesupport.core.report.extension.SqlXmlReporterExtension;
import org.jbehavesupport.core.report.extension.TestContextXmlReporterExtension;
import org.jbehavesupport.core.report.extension.WsXmlReporterExtension;
import org.jbehavesupport.core.rest.RestServiceHandler;
import org.jbehavesupport.core.splunk.SplunkClient;
import org.jbehavesupport.core.ssh.SshHandler;
import org.jbehavesupport.core.ws.WebServiceHandler;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@ConditionalOnBean(XmlReporterFactory.class)
public class XmlReporterConfig {

    @Bean
    public TestContextXmlReporterExtension testContextXmlReporterExtension(TestContext testContext) {
        return new TestContextXmlReporterExtension(testContext);
    }

    @Bean
    @ConditionalOnProperty(prefix = "environmentInfo")
    public EnvironmentInfoXmlReporterExtension environmentInfoXmlReporterExtension(Environment environment) {
        return new EnvironmentInfoXmlReporterExtension(environment);
    }

    @Bean
    public FailScreenshotsReporterExtension failScreenshotsReporterExtension(TestContext testContext) {
        return new FailScreenshotsReporterExtension(testContext);
    }

    @Bean
    public ScreenshotReporterExtension screenshotReporterExtension(TestContext testContext) {
        return new ScreenshotReporterExtension(testContext);
    }

    @Bean
    @ConditionalOnBean(JmsHandler.class)
    public JmsXmlReporterExtension jmsXmlReporterExtension() {
        return new JmsXmlReporterExtension();
    }

    @Bean
    @ConditionalOnBean(RestServiceHandler.class)
    public RestXmlReporterExtension restXmlReporterExtension(TestContext testContext, FileNameResolver fileNameResolver) {
        return new RestXmlReporterExtension(testContext, fileNameResolver);
    }

    @Bean
    @ConditionalOnBean(SshHandler.class)
    public ServerLogXmlReporterExtension serverLogXmlReporterExtension(ConfigurableListableBeanFactory beanFactory, SshHandler sshHandler, TestContext testContext, FileNameResolver fileNameResolver) {
        return new ServerLogXmlReporterExtension(testContext, fileNameResolver, sshHandler, beanFactory);
    }

    @Bean
    @ConditionalOnBean(SplunkClient.class)
    public SplunkXmlReporterExtension splunkXmlReporterExtension(){
        return new SplunkXmlReporterExtension();
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    public SqlXmlReporterExtension sqlXmlReporterExtension() {
        return new SqlXmlReporterExtension();
    }

    @Bean
    @ConditionalOnBean(WebServiceHandler.class)
    public WsXmlReporterExtension wsXmlReporterExtension() {
        return new WsXmlReporterExtension();
    }

}
