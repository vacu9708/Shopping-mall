package com.gateway.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Configuration
@RestController
public class Gateway {
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
	KeyResolver customKeyResolver() {
		return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
	}

	// @Bean
	// RedisRateLimiter redisRateLimiter() {
	// 	return new RedisRateLimiter(1, 2);
	// }

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