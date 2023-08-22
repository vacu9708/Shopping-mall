package com.gateway.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("**");
                        // .allowedOrigins("**") // Replace this with specific origin URLs if needed
                        // .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE");
                        // .allowedHeaders("*")
                        // .allowCredentials(false)
                        // .maxAge(3600);
            }
        };
    }
}
