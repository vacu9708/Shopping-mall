package com.product_management.product_management;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String allowedIps = "hasIpAddress('127.0.0.1')";
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(request -> request
                    .requestMatchers("/manager/**").access(new WebExpressionAuthorizationManager(allowedIps))
                    .anyRequest().permitAll()
                    // .anyRequest().authenticated()
                ).build();
    }
}