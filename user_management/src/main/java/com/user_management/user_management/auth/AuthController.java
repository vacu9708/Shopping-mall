package com.user_management.user_management.auth;

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

    @GetMapping("/verifyAccessToken")
    ResponseEntity<?> verifyToken(@RequestHeader("accessToken") String accessToken) {
        return authService.verifyAccessToken(accessToken);
    }

    @PostMapping("/addInBlacklist/{username}")
    ResponseEntity<String> editBlacklist(@RequestHeader("accessToken") String accessToken, @PathVariable String username) {
        return authService.editBlacklist(accessToken, username, "add");
    }
    @DeleteMapping("/removeFromBlacklist/{username}")
    ResponseEntity<String> removeFromBlacklist(@RequestHeader("accessToken") String accessToken, @PathVariable String username) {
        return authService.editBlacklist(accessToken, username, "remove");
    }

    @GetMapping("/reissueTokens")
    ResponseEntity<?> reissueToken(@RequestHeader("accessToken") String accessToken,
                                    @RequestHeader("refreshToken") String refreshToken) {
        return authService.reissueToken(accessToken, refreshToken);
    }

    @DeleteMapping("/user")
    ResponseEntity<String> deleteUser(@RequestHeader("accessToken") String accessToken) {
        return authService.deleteUser(accessToken);
    }
}
