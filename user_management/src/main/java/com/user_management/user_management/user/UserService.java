package com.user_management.user_management.user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.user_management.user_management.user.Dto.*;
import com.user_management.user_management.user.Utils.JwtUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserService {
    final UserRepository userRepository;
    final RedisTemplate<String, String> redisTemplate;

    ResponseEntity<String> registerUser(UserRegisterDto userRegisterDto) {
        // Check if the username already exists
        if(userRepository.findByUsername(userRegisterDto.getUsername()) != null)
            return ResponseEntity.badRequest().body("USERNAME_EXISTS");
        // Add user to the database
        String hashedPassword = new BCryptPasswordEncoder().encode(userRegisterDto.getPassword());
        userRepository.addUser(userRegisterDto.getUsername(), hashedPassword, userRegisterDto.getEmail());
        return ResponseEntity.ok("OK");
    }

    ResponseEntity<?> login(UserCredentialsDto userCredentialsDto) {
        // Check if the user exists and the password is correct
        UserEntity userEntity = userRepository.findByUsername(userCredentialsDto.getUsername());
        if(userEntity == null || !new BCryptPasswordEncoder().matches(userCredentialsDto.getPassword(), userEntity.getPassword()))
            return ResponseEntity.badRequest().body("INVALID_CREDENTIALS");

        // Check if the user has been blacklisted
        if(redisTemplate.opsForValue().get(userEntity.getUsername().toString()) != null)
            return ResponseEntity.badRequest().body("BLACKLISTED_USER");

        // Generate tokens
        Map<String, Object> accessTokenClaims = new HashMap<>();
        accessTokenClaims.put("userId", userEntity.getUserId());
        accessTokenClaims.put("username", userEntity.getUsername());
        String accessToken = JwtUtils.generateToken(accessTokenClaims, 1800000);

        Map<String, Object> refreshTokenClaims = new HashMap<>();
        refreshTokenClaims.put("username", userEntity.getUsername());
        String refreshToken = JwtUtils.generateToken(refreshTokenClaims, 43200000);
        
        // Return tokens
        return ResponseEntity.ok(new TokenPairDto(accessToken, refreshToken));
    }

    ResponseEntity<?> verifyAccessToken(String accessToken) {
        Claims accessTokenClaims;
        try{
            accessTokenClaims = JwtUtils.getTokenClaims(accessToken);
            // Check if the refresh token has been expired
            // if(refreshTokenClaims.getExpiration().before(new Date()))
            //     throw new IllegalArgumentException("Refresh token has expired");
        } catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest().body("EXPIRED_TOKEN");
            // return "Access token has expired";
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("INVALID_TOKEN");
        }
        // Ensure the token is not a refresh token
        if(accessTokenClaims.get("userId", String.class) == null)
            return ResponseEntity.badRequest().body("REFRESH_TOKEN_NOT_ALLOWED");
        // Return access token claims, which includes userId, username, and expiriation time
        accessTokenClaims.remove("exp");
        return ResponseEntity.ok(accessTokenClaims);
    }

    ResponseEntity<?> getUserInfo(String accessToken){
        Claims accessTokenClaims;
        try{
            accessTokenClaims = JwtUtils.getTokenClaims(accessToken);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest().body("EXPIRED_TOKEN");
            // return "Access token has expired";
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("INVALID_TOKEN");
        }
        UUID userId = UUID.fromString(accessTokenClaims.get("userId", String.class));
        // Ensure the token is not a refresh token
        if(userId == null)
            return ResponseEntity.badRequest().body("REFRESH_TOKEN_NOT_ALLOWED");
        
        UserEntity userEntity = userRepository.findByUserId(userId);
        // Check if the user exists
        if(userEntity == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new UserInfoDto(userEntity.getUserId(), userEntity.getUsername(), userEntity.getEmail()));
    }
    
    ResponseEntity<String> editBlacklist(String username, String action) {
        // Add the user in the blacklist
        if(action == "add"){
            redisTemplate.opsForValue().set(username, "X", 43200000, TimeUnit.MILLISECONDS); // 12hours
            // redisTemplate.opsForValue().set(username, "X");
            // redisTemplate.opsForValue().getOperations().expireAt(userId, new Date(System.currentTimeMillis() + 43200000));
        }
        else if(action.equals("remove")){
            redisTemplate.delete(username);
        }
        return ResponseEntity.ok("OK");
    }
    
    ResponseEntity<?> reissueToken(String accessToken, String refreshToken) {
        Claims refreshTokenClaims, accessTokenClaims;
        try{
            accessTokenClaims = JwtUtils.getTokenClaims(accessToken);
            refreshTokenClaims = JwtUtils.getTokenClaims(refreshToken);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest().body("EXPIRED_TOKEN");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("INVALID_TOKEN");
        }
        
        // Ensure the refresh token is not an access token
        if(refreshTokenClaims.get("userId", String.class) != null)
            return ResponseEntity.badRequest().body("ACCESS_TOKEN_NOT_ALLOWED");
        // Block the request if the user has been blacklisted
        String username = refreshTokenClaims.get("username", String.class);
        if(redisTemplate.opsForValue().get(username) != null)
            return ResponseEntity.badRequest().body("BLACKLISTED_USER");

        // Generate new tokens
        String newAccessToken = JwtUtils.generateToken(accessTokenClaims, 900000);
        String newRefreshToken = JwtUtils.generateToken(refreshTokenClaims, 43200000);
        
        // Return the new tokens
        return ResponseEntity.ok(new TokenPairDto(newAccessToken, newRefreshToken));
    }

    @Transactional
    ResponseEntity<String> deleteUser(String accessToken) {
        Claims accessTokenClaims;
        try{
            accessTokenClaims = JwtUtils.getTokenClaims(accessToken);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest().body("EXPIRED_TOKEN");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("INVALID_TOKEN");
        }
        // Ensure the token is not a refresh token
        if(accessTokenClaims.get("userId", String.class) == null)
            return ResponseEntity.badRequest().body("REFRESH_TOKEN_NOT_ALLOWED");
        // Check if the user exists
        if(userRepository.existsByUsername(accessTokenClaims.get("username", String.class)) == false)
            return ResponseEntity.notFound().build();
        // Delete the user
        userRepository.deleteByUsername(accessTokenClaims.get("username", String.class));
        return ResponseEntity.ok("OK");
    }
}
