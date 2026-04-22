package org.jbehavesupport.core.test.app;

import java.sql.SQLException;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatabaseConfig {

    private final Environment env;

    @Bean
    public Server initServer() {
        Server h2Server;
        try {
            h2Server = Server.createTcpServer("-ifNotExists", "-tcpAllowOthers", "-tcpPort", env.getProperty("h2.tcp.port")).start();
            if (h2Server.isRunning(true)) {
                log.info("H2 server was started and is running.");
            } else {
                throw new RuntimeException("Could not start H2 server.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to start H2 server: ", e);
        }
        return h2Server;
    }

    @Bean("TEST")
    @DependsOn("initServer")
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        ds.setUrl(env.getProperty("spring.datasource.url"));
        ds.setUsername(env.getProperty("spring.datasource.username"));
        ds.setPassword(env.getProperty("spring.datasource.password"));
        log.info("DataSource initialized with URL: {}", env.getProperty("spring.datasource.url"));
        return ds;
    }
}
