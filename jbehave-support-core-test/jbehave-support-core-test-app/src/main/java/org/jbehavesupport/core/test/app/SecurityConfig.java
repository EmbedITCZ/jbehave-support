package org.jbehavesupport.core.test.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain basicAuthFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers("/rest/secure/**").fullyAuthenticated()
            .requestMatchers("/**").permitAll()
            .and()
            .httpBasic()
            .and().headers().frameOptions().sameOrigin()
            .and().build();
    }

}
