package com.gateway.gateway.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import reactor.core.publisher.Mono;

@Configuration
@RestController
public class Gateway {
	@Autowired
    private CircuitBreakerFactory circuitBreakerFactory;
	@GetMapping("/test")
	public String test() {
		// CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
    	// String url = "http://localhost:8084/notification/timeoutTest";
		// RestTemplate restTemplate = new RestTemplate();
    	// return circuitBreaker.run(() -> restTemplate.getForObject(url, String.class));
		return "test";
	}
	@RequestMapping("/fallback")
	public ResponseEntity<?> fallback() {
		return ResponseEntity.status(503).body("Circuit closed");
	}

	// @Bean
	// public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
	// 	return builder.routes()
	// 		// .route("order_management", r -> r.path("/order_management/**")
	// 		// 	.uri("http://localhost:8081/")
	// 		// )
	// 		.route("order_management", r -> r.path("/order_management/**")
	// 			// Convert /order_management/abc to /abc
	// 			.filters(f -> f.rewritePath("/order_management/(?<path>.*)", "/${path}")
	// 							.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter()))
	// 							.circuitBreaker(config -> config
	// 								.setName("myCircuitBreaker")
	// 								.setFallbackUri("forward:/fallback")
	// 							)
	// 			)
	// 			.uri("http://localhost:8081/")
	// 		)
	// 		.route("user_management", r -> r.path("/user_management/**")
	// 			.filters(f -> f.rewritePath("/user_management/(?<path>.*)", "/${path}")
	// 							.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter()))
	// 							.circuitBreaker(config -> config
	// 								.setName("myCircuitBreaker")
	// 								.setFallbackUri("forward:/fallback")
	// 							)
	// 			)
	// 			.uri("http://localhost:8082/")
	// 		)
	// 		.route("product_management", r -> r.path("/product_management/**")
	// 			.filters(f -> f.rewritePath("/product_management/(?<path>.*)", "/${path}")
    //                             .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter()))
	// 							.circuitBreaker(config -> config
	// 								.setName("myCircuitBreaker")
	// 								.setFallbackUri("forward:/fallback")
	// 							)
    //             )
	// 			.uri("http://localhost:8083/")
	// 		)
	// 		.route("notification", r -> r.path("/notification/**")
	// 			.filters(f -> f.rewritePath("/notification/(?<path>.*)", "/${path}")
	// 							.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter()))
	// 							.circuitBreaker(config -> config
	// 								.setName("myCircuitBreaker")
	// 								.setFallbackUri("forward:/fallback")
	// 							)
	// 					)
	// 			.uri("http://localhost:8084/")
	// 		)
	// 		.build();
	// }

	// Implement the functional interface KeyResolver to resolve the key to be used for rate limiting.
	@Bean
	KeyResolver redisKeyResolver() {
		return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
	}

	@Bean
	RedisRateLimiter redisRateLimiter() {
		return new RedisRateLimiter(1, 2);
	}

	// @Bean
	// public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
	// 	return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
	// 			.circuitBreakerConfig(CircuitBreakerConfig.custom()
	// 					.slidingWindowSize(8)
	// 					.permittedNumberOfCallsInHalfOpenState(5)
	// 					.failureRateThreshold(50)
	// 					.waitDurationInOpenState(Duration.ofSeconds(5))
	// 					.build())
	// 			// .timeLimiterConfig(TimeLimiterConfig.custom()
	// 			// 		.timeoutDuration(Duration.ofSeconds(1))
	// 			// 		.build())
	// 			.build()
	// 	);
	// }
}