package com.order_management.order_management.order;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/order_management")
public class OrderController {
    @GetMapping("/test/{msg}")
    String getTest(@PathVariable("msg") String msg) {
        return "Received path parameter value: " + msg;
    }

    @GetMapping("/internal_test")
    String getInternalTest() {
        CompletableFuture<String> promise1 = WebClient.create()
            .get()
            .uri("http://localhost:8080/circuitbreaker_fallback")
            .retrieve()
            .bodyToMono(String.class)
            .toFuture();

        String res1 = promise1.join();

        System.out.println(res1);
        return "OK";
    }

    @GetMapping("/external_test")
    String getExternalTest() {
        CompletableFuture<String> promise1 = WebClient.create()
            .get()
            .uri("https://www.google.com")
            .retrieve()
            .bodyToMono(String.class)
            .toFuture();

        String res1 = promise1.join();

        System.out.println(res1);
        return "OK";
    }
}
