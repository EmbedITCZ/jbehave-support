package org.jbehavesupport.test;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.jbehave.core.model.ExamplesTable;
import org.jbehavesupport.core.healthcheck.HealthCheck;
import org.jbehavesupport.core.healthcheck.HealthChecks;
import org.jbehavesupport.core.rest.RestServiceHandler;
import org.jbehavesupport.core.rest.RestTemplateConfigurer;
import org.jbehavesupport.core.ssh.RollingLogResolver;
import org.jbehavesupport.core.ssh.SimpleRollingLogResolver;
import org.jbehavesupport.core.ssh.SshLog;
import org.jbehavesupport.core.ssh.SshSetting;
import org.jbehavesupport.core.ssh.SshTemplate;
import org.jbehavesupport.core.support.YamlPropertySourceFactory;
import org.jbehavesupport.core.web.WebDriverFactory;
import org.jbehavesupport.core.web.WebSetting;
import org.jbehavesupport.core.ws.WebServiceHandler;
import org.jbehavesupport.test.support.TestWebServiceHandler;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;

import static java.lang.Integer.parseInt;
import static java.util.Collections.singletonMap;
import static java.util.Objects.nonNull;
import static org.jbehavesupport.core.ssh.SshSetting.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
@RequiredArgsConstructor
@PropertySource(value = "test.yml", factory = YamlPropertySourceFactory.class)
public class TestConfiguration {

    public static final String FIREFOX_BROWSERSTACK = "firefox-browserstack";
    public static final String SAFARI_BROWSERSTACK = "safari-browserstack";
    public static final String CHROME_BROWSERSTACK = "chrome-browserstack";

    private final Environment env;

    final ResourceLoader resourceLoader;

    @Bean
    @Qualifier("TEST")
    public WebServiceHandler testWebServiceHandler() {
        return new TestWebServiceHandler(env);
    }

    @Bean
    @Qualifier("TEST")
    public DataSource testDatasource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(env.getProperty("db.driver"));
        dataSourceBuilder.url(env.getProperty("db.url"));
        dataSourceBuilder.username(env.getProperty("db.username"));
        dataSourceBuilder.password(env.getProperty("db.password"));
        return dataSourceBuilder.build();
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
        SshTemplate passwordTemplate = new SshTemplate(passwordSetting, timestampFormat, rollingLogResolver, false);
        SshTemplate keyTemplate = new SshTemplate(keySetting, timestampFormat, rollingLogResolver, false);

        return new SshTemplate[]{passwordTemplate, keyTemplate};
    }

    @Bean
    @Qualifier("LONG_REPORTABLE")
    SshTemplate longReportableSshTemplate() throws IOException {
        SshSetting sshSetting = builder()
            .hostname("fake hostname")
            .user("fake user")
            .password("asdf5684Daa")
            .port(0)
            .logPath("fake/log/api.log")
            .build();
        SshLog sshLog = new SshLog("Random really long string: " + RandomStringUtils.random(10001, true, true), sshSetting);
        SshTemplate sshTemplate = mock(SshTemplate.class);
        when(sshTemplate.copyLog(any(ZonedDateTime.class), any(ZonedDateTime.class))).thenReturn(sshLog);
        when(sshTemplate.isReportable()).thenReturn(true);
        when(sshTemplate.getSshSetting()).thenReturn(sshSetting);
        return sshTemplate;
    }

    @Bean
    @Qualifier("TEST")
    HealthCheck realHealthCheck() {
        return HealthChecks.http(env.getProperty("rest.url") + "user/", "", "");
    }

    @Bean
    public WebDriverFactory safariBrowserStackDriverFactory() {
        return new WebDriverFactory() {
            @Override
            public RemoteWebDriver createWebDriver() {
                DesiredCapabilities caps = new DesiredCapabilities();
                caps.setCapability("os", "OS X");
                caps.setCapability("os_version", "Catalina");
                caps.setCapability("browser", "Safari");
                caps.setCapability("browserstack.safari.driver", "2.48");
                caps.setCapability("browserstack.safari.enablePopups", "true");
                caps.setCapability("acceptSslCerts", true);

                return getBrowserStackWebDriver(caps);
            }

            @Override
            public String getName() {
                return SAFARI_BROWSERSTACK;
            }
        };
    }

    @Bean
    public WebDriverFactory firefoxBrowserStackDriverFactory() {
        return new WebDriverFactory() {
            @Override
            public RemoteWebDriver createWebDriver() {
                DesiredCapabilities caps = new DesiredCapabilities();
                caps.setCapability("os", "Windows");
                caps.setCapability("os_version", "10");
                caps.setCapability("browser", "Firefox");

                return getBrowserStackWebDriver(caps);
            }

            @Override
            public String getName() {
                return FIREFOX_BROWSERSTACK;
            }
        };
    }

    @Bean
    public WebDriverFactory chromeBrowserStackDriverFactory() {
        return new WebDriverFactory() {
            @Override
            public RemoteWebDriver createWebDriver() {
                DesiredCapabilities caps = new DesiredCapabilities();
                caps.setCapability("os", "Windows");
                caps.setCapability("os_version", "10");
                caps.setCapability("browser", "Chrome");

                // issue with w3c mode set on default - causes fails with window handles
                // see https://github.com/webdriverio/webdriverio/issues/4187
                ChromeOptions options = new ChromeOptions();
                options.setExperimentalOption("w3c", false);
                caps.setCapability(ChromeOptions.CAPABILITY, options);

                return getBrowserStackWebDriver(caps);
            }

            @Override
            public String getName() {
                return CHROME_BROWSERSTACK;
            }
        };
    }

    @SneakyThrows(MalformedURLException.class)
    private RemoteWebDriver getBrowserStackWebDriver(DesiredCapabilities capabilities) {
        capabilities.setCapability("resolution", "1920x1080");
        capabilities.setCapability("browserstack.local", "true");
        capabilities.setCapability("browser_version", "latest");
        capabilities.setCapability("browserstack.selenium_version", "3.141.59");
        capabilities.setCapability("name", env.getProperty("browser-stack.name"));
        capabilities.setCapability("build", env.getProperty("browser-stack.build"));
        if (nonNull(env.getProperty("browserstack.local.identifier"))) {
            capabilities.setCapability("browserstack.localIdentifier", env.getProperty("browserstack.local.identifier"));
        }

        RemoteWebDriver driver = new RemoteWebDriver(new URL(env.getProperty("browser-stack.url")), capabilities);
        driver.manage().window().maximize();
        return driver;
    }
}
