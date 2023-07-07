package com.gateway.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@RestController
public class Gateway {

    @RequestMapping("/circuitbreaker_fallback")
	public String circuitbreakerfallback() {
		return "This is a fallback";
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
			// .route("order_management", r -> r
			// 	.path("/order_management/**")
			// 	.filters(f -> f.rewritePath("/order_management/(?<path>.*)", "/${path}")) // Convert /order_management/abc to /abc
			// 	.uri("http://localhost:8081")
			// )
			.route("order_management", r -> r.path("/order_management/**")
				.uri("http://localhost:8081/")
			)
			.route("user_management", r -> r.path("/user_management/**")
				.uri("http://localhost:8082/")
			)
			.route("product_management", r -> r.path("/product_management/**")
				.uri("http://localhost:8083/")
			)
			.route("notification", r -> r.path("/notification/**")
				.uri("http://localhost:8084/")
			)
			.route(p -> p
				.host("*.circuitbreaker.com")
				.filters(f -> f
				.circuitBreaker(config -> config
					.setName("mycmd")
					.setFallbackUri("forward:/circuitbreaker_fallback")))
				.uri("http://localhost:8080"))
			.build();
	}

	@Bean
	RedisRateLimiter redisRateLimiter() {
		return new RedisRateLimiter(1, 2);
	}
}