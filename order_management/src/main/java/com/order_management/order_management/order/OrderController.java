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

import com.order_management.order_management.order.api.OrderApis;
import com.order_management.order_management.order.dto.EmailDto;
import com.order_management.order_management.order.dto.OrderDto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
// @RequestMapping("/order_management")
public class OrderController {
    final OrderService orderService;
    final OrderApis orderApis;

    @GetMapping("/test")
    ResponseEntity<String> test(HttpServletRequest request) {
        System.out.println(request.getRemoteAddr());
        return ResponseEntity.ok("test");
    }


    @GetMapping("/emailTest")
    void emailTest() {
        EmailDto emailDto = EmailDto.builder()
            .to("vacu9708@naver.com")
            .subject("Test email")
            .text("Test email")
            .build();
        orderApis.sendEmail(emailDto);
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