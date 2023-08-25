package com.notification.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootApplication
@RestController
public class NotificationApplication {
	@GetMapping("/test")
	ResponseEntity<String> test(HttpServletRequest request) {
        System.out.println(request.getRemoteAddr());
        return ResponseEntity.ok("test");
    }

	@GetMapping("/errorTest")
	ResponseEntity<String> errorTest() throws InterruptedException{
		// Thread.sleep(1000);
		throw new RuntimeException("error test");
		// return ResponseEntity.internalServerError().body("error test");
		// return ResponseEntity.ok("error test");
	}

	public static void main(String[] args) {
		SpringApplication.run(NotificationApplication.class, args);
	}

}
