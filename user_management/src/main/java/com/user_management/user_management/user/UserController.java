package com.user_management.user_management.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.user_management.user_management.user.Dto.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
    final UserService authService;

    @GetMapping("/test")
    ResponseEntity<String> test(HttpServletRequest request) {
        System.out.println(request.getRemoteAddr());
        return ResponseEntity.ok("test");
    }

    @PostMapping("/signUpEmail")
    ResponseEntity<String> signUpEmail(@RequestBody UserRegisterDto userRegisterDto) throws JsonProcessingException {
        return authService.signUpEmail(userRegisterDto);
    }

    @GetMapping("/registerUser/{signUpToken}")
    ResponseEntity<String> registerUser(@PathVariable String signUpToken) {
        return authService.registerUser(signUpToken);
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

    @PostMapping("/manager/addInBlacklist/{username}")
    ResponseEntity<String> managerEditBlacklist(@PathVariable String username) {
        return authService.editBlacklist(username, "add");
    }

    @GetMapping("/verifyAccessToken")
    ResponseEntity<?> verifyToken(@RequestHeader("accessToken") String accessToken) {
        return authService.verifyAccessToken(accessToken);
    }

    @GetMapping("/getUserInfo")
    ResponseEntity<?> getUserInfo(@RequestHeader("accessToken") String accessToken) {
        return authService.getUserInfo(accessToken);
    }

    @DeleteMapping("/manager/removeFromBlacklist/{username}")
    ResponseEntity<String> removeFromBlacklist(@PathVariable String username) {
        return authService.editBlacklist(username, "remove");
    }

    @GetMapping("/reissueTokens")
    ResponseEntity<?> reissueToken(@RequestHeader("accessToken") String accessToken,
                                    @RequestHeader("refreshToken") String refreshToken) {
        return authService.reissueToken(accessToken, refreshToken);
    }

    @DeleteMapping("/deleteUser")
    ResponseEntity<String> deleteUser(@RequestHeader("accessToken") String accessToken) {
        return authService.deleteUser(accessToken);
    }
}
