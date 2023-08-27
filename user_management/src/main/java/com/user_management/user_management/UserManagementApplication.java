package com.user_management.user_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
// @SpringBootApplication
public class UserManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserManagementApplication.class, args);
	}

}
