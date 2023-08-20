package com.gateway.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
// import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
// import org.springframework.cloud.gateway.route.RouteLocator;
// import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Configuration
@RestController
public class Gateway {
	@GetMapping("/test")
	public String test() {
		return "test";
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
	// 			)
	// 			.uri("http://localhost:8081/")
	// 		)
	// 		.route("user_management", r -> r.path("/user_management/**")
	// 			.filters(f -> f.rewritePath("/user_management/(?<path>.*)", "/${path}")
	// 							.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter()))
	// 			)
	// 			.uri("http://localhost:8082/")
	// 		)
	// 		.route("product_management", r -> r.path("/product_management/**")
	// 			.filters(f -> f.rewritePath("/product_management/(?<path>.*)", "/${path}")
    //                             .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter()))
    //             )
	// 			.uri("http://localhost:8083/")
	// 		)
	// 		.route("notification", r -> r.path("/notification/**")
	// 			.filters(f ->f.rewritePath("/notification/(?<path>.*)", "/${path}"))
	// 			.uri("http://localhost:8084/")
	// 		)
	// 		.build();
	// }

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