package com.order_management.order_management.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.order_management.order_management.order.dto.OrderDto;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
// @RequestMapping("/order_management")
public class OrderController {
    final OrderService orderService;

    @GetMapping("/test")
    ResponseEntity<String> test(HttpServletRequest request) {
        System.out.println(request.getRemoteAddr());
        return ResponseEntity.ok("test");
    }

    @GetMapping("/errorTest")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallback")
    ResponseEntity<String> errorTest(){
        // return new RestTemplate().getForEntity("http://localhost:8084/errorTest", String.class);
        WebClient.create().get().uri("http://localhost:8084/errorTest").retrieve().toEntity(String.class).block();
        return ResponseEntity.ok("test");
        // CircuitBreaker circuitBreaker = circuitBreakerFactory.create("myCircuitBreaker");
        // return circuitBreaker.run(()-> WebClient.create().get().uri("http://localhost:8084/errorTest").retrieve().toEntity(String.class).block(),
        //     throwable ->ResponseEntity.internalServerError().body("Circuit closed"));
    }

    String fallback(Exception e){
        return "Circuit closed";
    }

    @PostMapping("/makeOrder")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "closedCircuit")
    ResponseEntity<?> makeOrder(@RequestHeader("accessToken") String accessToken, @RequestBody OrderDto orderDto) {
        return orderService.makeOrder(accessToken, orderDto);
    }

    @GetMapping("/getUserOrders")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "closedCircuit")
    ResponseEntity<?> getUserOrders(@RequestHeader("accessToken") String accessToken) {
        return orderService.getUserOrders(accessToken);
    }
}