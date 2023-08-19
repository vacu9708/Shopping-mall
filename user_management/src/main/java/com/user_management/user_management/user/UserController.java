package com.user_management.user_management.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/getUserInfo")
    ResponseEntity<?> getUserInfo(@RequestHeader("accessToken") String accessToken) {
        return authService.getUserInfo(accessToken);
    }

    @PostMapping("/manager/addInBlacklist/{username}")
    ResponseEntity<String> managerEditBlacklist(@PathVariable String username) {
        return authService.editBlacklist(username, "add");
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
