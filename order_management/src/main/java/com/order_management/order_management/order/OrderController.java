package com.order_management.order_management.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order_management")
public class OrderController {
    @GetMapping("/test/{msg}")
    String getTest(@PathVariable("msg") String msg) {
        return "Received path parameter value: " + msg;
    }

}
