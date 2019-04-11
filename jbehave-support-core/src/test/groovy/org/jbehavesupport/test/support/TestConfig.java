package org.jbehavesupport.test.support;

import static java.lang.Integer.parseInt;
import static java.util.Collections.singletonMap;
import static org.jbehavesupport.core.ssh.SshSetting.builder;

import java.io.IOException;

import javax.jms.ConnectionFactory;
import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jbehave.core.model.ExamplesTable;
import org.jbehavesupport.core.healthcheck.HealthCheck;
import org.jbehavesupport.core.healthcheck.HealthChecks;
import org.jbehavesupport.core.jms.JmsJaxbHandler;
import org.jbehavesupport.core.report.XmlReporterFactory;
import org.jbehavesupport.core.report.extension.EnvironmentInfoXmlReporterExtension;
import org.jbehavesupport.core.report.extension.RestXmlReporterExtension;
import org.jbehavesupport.core.report.extension.ServerLogXmlReporterExtension;
import org.jbehavesupport.core.report.extension.SqlXmlReporterExtension;
import org.jbehavesupport.core.report.extension.TestContextXmlReporterExtension;
import org.jbehavesupport.core.report.extension.WsXmlReporterExtension;
import org.jbehavesupport.core.rest.RestServiceHandler;
import org.jbehavesupport.core.rest.RestTemplateConfigurer;
import org.jbehavesupport.core.ssh.RollingLogResolver;
import org.jbehavesupport.core.ssh.SimpleRollingLogResolver;
import org.jbehavesupport.core.ssh.SshSetting;
import org.jbehavesupport.core.ssh.SshTemplate;
import org.jbehavesupport.core.support.YamlPropertiesConfigurer;
import org.jbehavesupport.core.test.app.oxm.NameRequest;
import org.jbehavesupport.core.web.WebSetting;
import org.jbehavesupport.core.ws.WebServiceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@ComponentScan
public class TestConfig {

    @Autowired
    private Environment env;

    @Autowired
    ResourceLoader resourceLoader;

    @Bean
    public static YamlPropertiesConfigurer yamlPropertiesConfigurer() {
        return new YamlPropertiesConfigurer("test.yml");
    }

    @Bean
    @Qualifier("TEST")
    public WebServiceHandler testWebServiceHandler() {
        return new TestWebServiceHandler();
    }

    @Bean
    public ConversionService conversionService() {
        return new DefaultConversionService();
    }

    @Bean
    @Qualifier("TEST")
    public DataSource testDatasource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("db.driver"));
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));
        return dataSource;
    }

    @Bean
    public XmlReporterFactory xmlReporterFactory() {
        return new XmlReporterFactory();
    }

    @Bean
    public WsXmlReporterExtension wsXmlReporterExtension() {
        return new WsXmlReporterExtension();
    }

    @Bean
    public RestXmlReporterExtension restXmlReporterExtension() {
        return new RestXmlReporterExtension();
    }

    @Bean
    public EnvironmentInfoXmlReporterExtension environmentInfoXmlReporterExtension() {
        return new EnvironmentInfoXmlReporterExtension();
    }

    @Bean
    public TestContextXmlReporterExtension testContextXmlReporterExtension() {
        return new TestContextXmlReporterExtension();
    }

    @Bean
    public ServerLogXmlReporterExtension serverLogXmlReporterExtension() {
        return new ServerLogXmlReporterExtension();
    }

    @Bean
    public SqlXmlReporterExtension sqlXmlReporterExtension() {
        return new SqlXmlReporterExtension();
    }

    @Bean
    @Qualifier("TEST")
    public RestServiceHandler testRestServiceHandler() {
        return new RestServiceHandler(env.getProperty("rest.url")) {

            @Override
            public ExamplesTable getSuccessResult() {
                return new ExamplesTable("" +
                    "| name           | expectedValue |\n" +
                    "| @header.Status | CREATED       |\n" +
                    "| payload[0]     | happy         |");
            }
        };
    }

    @Bean
    @Qualifier("TEST-SECURE")
    public RestServiceHandler secureTestRestServiceHandler() {
        return new RestServiceHandler(env.getProperty("rest.url")) {
            @Override
            protected void initTemplate(RestTemplateConfigurer templateConfigurer) {
                templateConfigurer
                    .basicAuthorization(env.getProperty("rest.username"), env.getProperty("rest.password"))
                    .header(() -> singletonMap("customHeader", "customValue"));
            }
        };
    }

    @Bean
    @Qualifier("TEST")
    public WebSetting testWebSettings() {
        return WebSetting.builder()
            .homePageUrl(env.getProperty("web.url"))
            .elementLocatorsSource("ui-mapping/*.yaml")
            .build();
    }

    @Bean
    @Qualifier("TEST")
    public SshTemplate[] sshTemplate() throws IOException {
        String hostname = env.getProperty("ssh.hostname");
        String user = env.getProperty("ssh.credentials.user");
        int port = parseInt(env.getProperty("ssh.port"));
        String logPath = env.getProperty("ssh.logPath");

        SshSetting passwordSetting = builder()
            .hostname(hostname)
            .user(user)
            .password(env.getProperty("ssh.credentials.password"))
            .port(port)
            .logPath(logPath)
            .build();

        String keyPath = resourceLoader.getResource(env.getProperty("ssh.credentials.keyPath"))
            .getURL()
            .getFile();
        SshSetting keySetting = builder()
            .hostname(hostname)
            .user(user)
            .keyPath(keyPath)
            .port(port)
            .logPath(logPath)
            .build();

        RollingLogResolver rollingLogResolver = new SimpleRollingLogResolver();
        String timestampFormat = env.getProperty("ssh.timestampFormat");
        SshTemplate passwordTemplate = new SshTemplate(passwordSetting, timestampFormat, rollingLogResolver);
        SshTemplate keyTemplate = new SshTemplate(keySetting, timestampFormat, rollingLogResolver);

        return new SshTemplate[]{ passwordTemplate, keyTemplate };
    }

    @Bean
    @Qualifier("TEST")
    HealthCheck realHealthCheck() {
        return HealthChecks.http(env.getProperty("rest.url") + "user/", "", "");
    }

    @Bean
    ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(env.getProperty("jms.brokerUrl"));
    }

    @Bean
    @Qualifier("TEST")
    public JmsJaxbHandler jmsJaxbHandler() {
        Class[] classesToBeBound = { NameRequest.class };
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        return new JmsJaxbHandler(jmsTemplate, classesToBeBound);
    }
}
