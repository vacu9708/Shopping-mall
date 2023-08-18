package com.order_management.order_management.order;

import java.util.UUID;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.order_management.order_management.order.dto.OrderDto;
import com.order_management.order_management.order.dto.ProductDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
// @RequestMapping("/order_management")
public class OrderController {
    final OrderService orderService;

    @GetMapping("/test")
    ResponseEntity<?> getTest() {
        try{
            return WebClient.create()
                .get().uri("http://localhost:8080/product_management/getProduct/0a8fd1c7-3ba0-11ee-81f6-14857f22e600")
                .retrieve()
                .toEntity(ProductDto.class)
                .block();
        } catch(WebClientResponseException e){
            HttpStatusCode status = e.getStatusCode();
            String errorMessage = e.getResponseBodyAsString();
            return ResponseEntity.status(status).body(errorMessage);
        }
    }

    @PostMapping("/makeOrder")
    ResponseEntity<?> makeOrder(@RequestHeader("accessToken") String accessToken, @RequestBody OrderDto orderDto) {
        return orderService.makeOrder(accessToken, orderDto);
    }

    @GetMapping("/getUserOrders")
    ResponseEntity<?> getUserOrders(@RequestHeader("accessToken") String accessToken) {
        return orderService.getUserOrders(accessToken);
    }
}