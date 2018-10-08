package org.jbehavesupport.core.test.app;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ShellProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class SshConfig {

    @Autowired
    private Environment env;

    @Bean
    public ShellProperties.CrshShellAuthenticationProperties crashProperties() {
        return new CustomCrashProperties();
    }

    private class CustomCrashProperties extends ShellProperties.CrshShellAuthenticationProperties {
        protected void applyToCrshShellConfig(Properties config) {
            config.put("crash.ssh.port", env.getProperty("ssh.port"));
            config.put("crash.ssh.auth_timeout", env.getProperty("ssh.timeouts.auth"));
            config.put("crash.ssh.idle_timeout", env.getProperty("ssh.timeouts.idle"));
            config.put("crash.auth", "simple,dummy-key");
            config.put("crash.auth.simple.username", env.getProperty("ssh.credentials.user"));
            config.put("crash.auth.simple.password", env.getProperty("ssh.credentials.password"));
            config.put("crash.auth.key.path", env.getProperty("ssh.credentials.key"));
        }
    }
}
