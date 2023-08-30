package com.user_management.user_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
// @EnableWebFluxSecurity
public class SecurityConfig {
    // @Bean
	// public MapReactiveUserDetailsService userDetailsService() {
	// 	UserDetails user = User.withDefaultPasswordEncoder()
	// 		.username("manager")
	// 		.password("123")
	// 		.roles("manager")
	// 		.build();
	// 	return new MapReactiveUserDetailsService(user);
	// }

    // For webflux
    // @Bean
    // public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    //     http.csrf().disable()
    //     .authorizeExchange()
    //     .anyExchange().permitAll();
 
    //     return http.build();
    // }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(request -> request
                    .anyRequest().permitAll()
                ).build();
    }
}