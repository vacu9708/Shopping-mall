package com.gateway.gateway.config;
// package com.gateway.gateway;

// import java.util.ArrayList;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authorization.AuthorizationDecision;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
// import org.springframework.security.config.web.server.ServerHttpSecurity;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.web.SecurityFilterChain;
// // import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
// import org.springframework.security.web.server.SecurityWebFilterChain;
// import org.springframework.security.web.server.authorization.AuthorizationContext;

// import reactor.core.publisher.Mono;

// @Configuration
// // @EnableWebSecurity
// @EnableWebFluxSecurity
// public class SecurityConfig {
//     @Bean
// 	public MapReactiveUserDetailsService userDetailsService() {
// 		UserDetails user = User.withDefaultPasswordEncoder()
// 			.username("manager")
// 			.password("123")
// 			.roles("manager")
// 			.build();
// 		return new MapReactiveUserDetailsService(user);
// 	}

//     @Bean
//     public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//         http.csrf().disable()
//         .authorizeExchange()
//         // .anyExchange().authenticated();
//         .pathMatchers("**").hasRole("manager");
//         // .pathMatchers("/manager/**").hasRole("manager")
//         // .anyExchange().permitAll();
 
//         return http.build();
//     }
//     // cloud gateway is built on webflux
//     // @Bean
//     // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//     //     String allowedIps = "hasIpAddress('127.0.0.1')";
//     //     return http.csrf(csrf -> csrf.disable())
//     //             .authorizeRequests(request -> request
//     //                 // .antMatchers("/manager/**").access(allowedIps)
//     //                 .anyRequest().permitAll()
//     //             ).build();
//     // }
// }