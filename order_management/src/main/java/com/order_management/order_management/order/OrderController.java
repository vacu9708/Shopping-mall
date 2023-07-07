package com.order_management.order_management.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order_management")
public class OrderController {
    @GetMapping("/test")
    String getTest() {
        return "test";
    }

    
}
