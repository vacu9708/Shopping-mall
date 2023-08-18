package com.notification.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class NotificationApplication {
	@GetMapping("/test")
	public String test() {
		return "Hello World";
	}

	public static void main(String[] args) {
		SpringApplication.run(NotificationApplication.class, args);
	}

}
