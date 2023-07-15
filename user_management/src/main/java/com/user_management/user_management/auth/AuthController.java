package com.user_management.user_management.auth;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.user_management.user_management.auth.Dto.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
    final AuthService authService;

    @PostMapping("/registerUser")
    ResponseEntity<String> registerUser(@RequestBody UserRegisterDto userRegisterDto) {
        return authService.registerUser(userRegisterDto);
        // try {
        //     return authService.registerUser(userRegisterDto);
        // } catch (Exception e) {
        //     return ResponseEntity.badRequest().body("Error adding user");
        // }
    }

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody UserCredentialsDto userCredentialsDto) {
        return authService.login(userCredentialsDto);
    }

    @GetMapping("/verifyToken")
    ResponseEntity<String> verifyToken(@RequestHeader("accessToken") String accessToken) {
        return authService.verifyToken(accessToken);
    }

    @PostMapping("/addInBlacklist/{username}")
    ResponseEntity<String> addInBlacklist(@RequestHeader("accessToken") String accessToken, @PathVariable String username) {
        return authService.addInBlacklist(accessToken, username);
    }

    @GetMapping("/reissueToken")
    ResponseEntity<?> reissueToken(@RequestHeader("refreshToken") String refreshToken) {
        return authService.reissueToken(refreshToken);
    }

    @DeleteMapping("/user")
    ResponseEntity<String> deleteUser(@RequestHeader("accessToken") String accessToken) {
        return authService.deleteUser(accessToken);
    }
}
