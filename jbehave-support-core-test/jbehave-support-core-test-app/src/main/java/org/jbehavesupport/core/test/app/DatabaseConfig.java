package org.jbehavesupport.core.test.app;

import java.sql.SQLException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatabaseConfig {

    private final Environment env;

    @Bean
    public Server initServer() {
        Server h2Server;
        try {
            h2Server = Server.createTcpServer("-ifNotExists", "-tcpAllowOthers", "-tcpPort", env.getProperty("spring.datasource.port")).start();
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
}
