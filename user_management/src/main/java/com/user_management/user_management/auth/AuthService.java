package com.user_management.user_management.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.connector.Response;
import org.hibernate.DuplicateMappingException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.UnexpectedRollbackException;

import com.user_management.user_management.auth.Dto.*;
import com.user_management.user_management.auth.Utils.JwtUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthService {
    final AuthRepository authRepository;
    final RedisTemplate<String, String> redisTemplate;

    @Transactional
    ResponseEntity<String> registerUser(UserRegisterDto userRegisterDto) {
        // Check if the username already exists
        if(authRepository.findByUsername(userRegisterDto.getUsername()) != null)
            return ResponseEntity.badRequest().body("USERNAME_EXISTS");

        String hashedPassword = new BCryptPasswordEncoder().encode(userRegisterDto.getPassword());
        authRepository.addUser(userRegisterDto.getUsername(), hashedPassword, userRegisterDto.getEmail());
        return ResponseEntity.ok("OK");
    }

    ResponseEntity<?> login(UserCredentialsDto userCredentialsDto) {
        // Check if the user exists and the password is correct
        UserEntity userEntity = authRepository.findByUsername(userCredentialsDto.getUsername());
        if(userEntity == null || !new BCryptPasswordEncoder().matches(userCredentialsDto.getPassword(), userEntity.getPassword()))
            return ResponseEntity.badRequest().body("INVALID_CREDENTIALS");

        // Check if the user has been blacklisted
        // if(redisTemplate.opsForValue().get(userEntity.getUserId().toString()) != null)
        //     return ResponseEntity.badRequest().body("User has been blacklisted");

        // Generate tokens
        String username = userEntity.getUserId().toString();
        String accessToken = JwtUtils.generateToken(username, 900000); // 15 minutes
        String refreshToken = JwtUtils.generateToken(username, 43200000); // 12 hours
        
        // Return tokens
        return ResponseEntity.ok(new TokenPairDto(accessToken, refreshToken));
    }

    ResponseEntity<String> verifyToken(String accessToken) {
        try{
            Claims accessTokenClaims = JwtUtils.getTokenClaims(accessToken);
            return ResponseEntity.ok(accessTokenClaims.get("userId", String.class));
        } catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest().body("EXPIRED_TOKEN");
            // return "Access token has expired";
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("INVALID_TOKEN");
        }
    }
    
    ResponseEntity<String> addInBlacklist(String accessToken, String username) {
        // if the access token is invalid, return bad request
        ResponseEntity<String> response = verifyToken(accessToken);
        if(response.getStatusCode().value() != 200)
            return ResponseEntity.badRequest().body("INVALID_TOKEN");
        // if the access token is not from admin, return bad request
        UUID adminId = UUID.fromString(response.getBody());
        UserEntity adminEntity = authRepository.findByUserId(adminId);
        if(adminEntity == null || !adminEntity.getUsername().equals("admin"))
            return ResponseEntity.badRequest().body("NOT_ADMIN");
        // Blacklist the user
        UserEntity userEntity = authRepository.findByUsername(username);
        if(userEntity == null)
            return ResponseEntity.badRequest().body("USER_NOT_FOUND");
        redisTemplate.opsForValue().set(userEntity.getUserId().toString(), "X", 43200000, TimeUnit.MILLISECONDS); // 12hours
        // redisTemplate.opsForValue().set(username, "X");
        // redisTemplate.opsForValue().getOperations().expireAt(userId, new Date(System.currentTimeMillis() + 43200000));
        return ResponseEntity.ok("OK");
    }
    
    ResponseEntity<?> reissueToken(String refreshToken) {
        Claims refreshTokenClaims;
        String userId;
        try{
            refreshTokenClaims = JwtUtils.getTokenClaims(refreshToken);
            userId = refreshTokenClaims.get("userId", String.class);
            // Check if the refresh token is expired
            // if(refreshTokenClaims.getExpiration().before(new Date()))
            //     throw new IllegalArgumentException("Refresh token has expired");
        } catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest().body("EXPIRED_TOKEN");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("INVALID_TOKEN");
        }

        // Block the request if the refresh token is blacklisted
        // System.out.println(redisTemplate.opsForValue().get(userId));
        if(redisTemplate.opsForValue().get(userId) != null)
            return ResponseEntity.badRequest().body("USER_BLACKLISTED");

        // Generate tokens
        String newAccessToken = JwtUtils.generateToken(userId, 900000);
        String newRefreshToken = JwtUtils.generateToken(userId, 43200000);
        
        // Return tokens
        return ResponseEntity.ok(new TokenPairDto(newAccessToken, newRefreshToken));
    }

    @Transactional
    ResponseEntity<String> deleteUser(String accessToken) {
        // if the access token is invalid, return bad request
        ResponseEntity<String> response = verifyToken(accessToken);
        if(response.getStatusCode().value() != 200)
            return ResponseEntity.badRequest().body("INVALID_TOKEN");
        // Delete the user
        UUID userId = UUID.fromString(response.getBody());
        authRepository.deleteByUserId(userId);
        return ResponseEntity.ok("OK");
    }
}
