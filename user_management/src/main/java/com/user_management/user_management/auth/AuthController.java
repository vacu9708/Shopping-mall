package com.user_management.user_management.auth;

import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.user_management.user_management.auth.Dto.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
    final AuthService authService;

    @PostMapping("/registerUser")
    boolean registerUser(@RequestBody UserRegisterDto userCredentialsDto) {
        return authService.registerUser(userCredentialsDto);
    }

    @PostMapping("/login")
    Map<String, String> login(@RequestBody UserCredentialsDto userCredentialsDto) {
        return authService.login(userCredentialsDto);
    }

    @GetMapping("/verifyToken")
    String verifyToken(@RequestHeader("accessToken") String accessToken) {
        return authService.verifyToken(accessToken);
    }

    @GetMapping("/addInBlacklist")
    boolean addInBlacklist(@RequestHeader("accessToken") String accessToken, String username) {
        return authService.addInBlacklist(accessToken, username);
    }

    @GetMapping("/reissueToken")
    Map<String, String> reissueToken(@RequestHeader("refreshToken") String refreshToken) {
        return authService.reissueToken(refreshToken);
    }

    @DeleteMapping("/user")
    boolean deleteUser(@RequestHeader("accessToken") String accessToken) {
        return authService.deleteUser(accessToken);
    }
}
