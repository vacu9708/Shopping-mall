package com.user_management.user_management.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

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
    boolean registerUser(UserRegisterDto UserRegisterDto) {
        System.out.println(UserRegisterDto);
        String hashedPassword = new BCryptPasswordEncoder().encode(UserRegisterDto.getPassword());
        try{
            authRepository.addUser(UserRegisterDto.getUsername(), hashedPassword, UserRegisterDto.getEmail());
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    Map<String, String> login(UserCredentialsDto userCredentialsDto) {
        UserEntity userEntity = authRepository.findByUsername(userCredentialsDto.getUsername());
        // Check if the user exists
        if(userEntity == null || !new BCryptPasswordEncoder().matches(userCredentialsDto.getPassword(), userEntity.getPassword()))
            throw new IllegalArgumentException("Invalid username or password");

        // Generate tokens
        String accessToken = JwtUtils.generateToken(userEntity.getUserId().toString(), 900000); // 15 minutes
        String refreshToken = JwtUtils.generateToken(userEntity.getUserId().toString(), 43200000); // 12 hours
        
        // Return tokens
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    boolean addInBlacklist(String userId) {
        try{
            redisTemplate.opsForValue().set(userId, "X", 43200000, TimeUnit.MINUTES); // 12hours
            // redisTemplate.opsForValue().getOperations().expireAt(userId, new Date(System.currentTimeMillis() + 43200000));
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    String verifyToken(String accessToken) {
        try{
            Claims accessTokenClaims = JwtUtils.getTokenClaims(accessToken);
            return accessTokenClaims.get("userId", String.class);
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Token has expired");
            // return "Access token has expired";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new IllegalArgumentException("Invalid token");
        }
    }
    

    Map<String, String> reissueToken(String refreshToken) {
        Claims refreshTokenClaims;
        String userId;
        try{
            refreshTokenClaims = JwtUtils.getTokenClaims(refreshToken);
            userId = refreshTokenClaims.get("userId", String.class);
            // Check if the refresh token is expired
            // if(refreshTokenClaims.getExpiration().before(new Date()))
            //     throw new IllegalArgumentException("Refresh token has expired");
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Token has expired");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token");
        }

        // Block the request if the refresh token is blacklisted
        if(redisTemplate.opsForValue().get(userId) != null)
            throw new IllegalArgumentException("Refresh token has been blacklisted");

        // Generate tokens
        String newAccessToken = JwtUtils.generateToken(userId, 900000);
        String newRefreshToken = JwtUtils.generateToken(userId, 43200000);
        
        // Return tokens
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);
        return tokens;
    }

    boolean deleteUser(String accessToken) {
        UUID userId = UUID.fromString(verifyToken(accessToken));
        try{
            authRepository.deleteByUserId(userId);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    
}
