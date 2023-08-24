package com.gateway.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
	@GetMapping("/fallback")
	public String fallback() {
		return "fallback";
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				// .route(p -> p
				// .host("*.circuitbreaker.com")
				// .filters(f -> f.circuitBreaker(config -> config
				// 	.setName("mycmd")
				// 	.setFallbackUri("forward:/fallback")))
				// .uri("http://httpbin.org:80"))
			// .route("order_management", r -> r.path("/order_management/**")
			// 	.uri("http://localhost:8081/")
			// )
			// .route("order_management", r -> r.path("/order_management/**")
			// 	// Convert /order_management/abc to /abc
			// 	.filters(f -> f.rewritePath("/order_management/(?<path>.*)", "/${path}")
			// 					.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter()))
			// 	)
			// 	.uri("http://localhost:8081/")
			// )
			// .route("user_management", r -> r.path("/user_management/**")
			// 	.filters(f -> f.rewritePath("/user_management/(?<path>.*)", "/${path}")
			// 					.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter()))
			// 	)
			// 	.uri("http://localhost:8082/")
			// )
			// .route("product_management", r -> r.path("/product_management/**")
			// 	.filters(f -> f.rewritePath("/product_management/(?<path>.*)", "/${path}")
            //                     .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter()))
            //     )
			// 	.uri("http://localhost:8083/")
			// )
			// .route("notification", r -> r.path("/notification/**")
			// 	.filters(f ->f.rewritePath("/notification/(?<path>.*)", "/${path}"))
			// 	.uri("http://localhost:8084/")
			// )
			.build();
	}

	// Implement the functional interface KeyResolver to resolve the key to be used for rate limiting.
	@Bean
	KeyResolver redisKeyResolver() {
		return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
	}

	// @Bean
	// RedisRateLimiter redisRateLimiter() {
	// 	return new RedisRateLimiter(1, 2);
	// }
}