package com.user_management.user_management.user;

import java.util.Map;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
    final UserService userService;

    @PostMapping("/registerUser")
    boolean registerUser(@RequestBody UserCredentialsDto userCredentialsDto) {
        return userService.registerUser(userCredentialsDto);
    }

    @PostMapping("/login")
    Map<String, String> login(@RequestBody UserCredentialsDto userCredentialsDto) {
        return userService.login(userCredentialsDto);
    }

    @GetMapping("/verifyToken")
    String verifyToken(@RequestHeader("accessToken") UserCredentialsDto accessToken) {
        return userService.verifyToken(accessToken);
    }

    @GetMapping("/reissueToken")
    Map<String, String> reissueToken(@RequestHeader("refreshToken") String refreshToken) {
        return userService.reissueToken(refreshToken);
    }

    @DeleteMapping("/user")
    boolean deleteUser(@RequestHeader("accessToken") String accessToken) {
        return userService.deleteUser(accessToken);
    }
}
