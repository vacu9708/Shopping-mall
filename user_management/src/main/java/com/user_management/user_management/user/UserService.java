package com.user_management.user_management.user;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.user_management.user_management.user.Dto.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserService {
    final UserRepository userRepository;

    @Transactional
    boolean registerUser(UserRegisterDto UserRegisterDto) {
        System.out.println(UserRegisterDto);
        String hashedPassword = new BCryptPasswordEncoder().encode(UserRegisterDto.getPassword());
        try{
            userRepository.addUser(UserRegisterDto.getUsername(), hashedPassword, UserRegisterDto.getEmail());
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    static Key jwtKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode("aerkjfsdanfjsdanflkjadnhfdajkdnfhfaljkasfdj"));
    Map<String, String> login(UserCredentialsDto userCredentialsDto) {
        UserEntity userEntity = userRepository.findByUsername(userCredentialsDto.getUsername());
        // Check if the user exists
        if(userEntity == null || !new BCryptPasswordEncoder().matches(userCredentialsDto.getPassword(), userEntity.getPassword()))
            throw new IllegalArgumentException("Invalid username or password");
        
        //#JWT contents
        // Header
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");
        // Payload
        Map<String, UUID> payloads = new HashMap<>();
        payloads.put("userId", userEntity.getUserId());
        // Acess token expiration time
        Date accessTokenExp = new Date();
        accessTokenExp.setTime(accessTokenExp.getTime() + 3600000); // 1 hour
        // Refresh token expiration time
        Date refreshTokenExp = new Date();
        refreshTokenExp.setTime(refreshTokenExp.getTime() + 86_400_000); // 24 hours

        //#Generate JWT
        // Generate an access token
        String accessToken = Jwts.builder()
            .setHeader(headers)
            .setClaims(payloads)
            .setExpiration(accessTokenExp)
            .signWith(jwtKey)
            .compact();
        // Generate a refresh token
        String refreshToken = Jwts.builder()
            .setHeader(headers)
            .setExpiration(refreshTokenExp)
            .signWith(jwtKey)
            .compact();
        
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    String verifyToken(String accessToken) {
        // Parse the token
        Claims accessTokenClaims = Jwts.parserBuilder()
            .setSigningKey(jwtKey).build()
            .parseClaimsJws(accessToken)
            .getBody();

        // Check if the token is expired
        if(accessTokenClaims.getExpiration().before(new Date()))
            throw new IllegalArgumentException("Access token has expired");
    
        return accessTokenClaims.get("userId", String.class);
    }

    Map<String, String> reissueToken(String refreshToken) {
        // Parse the refresh token
        Claims refreshTokenClaims = Jwts.parserBuilder()
            .setSigningKey(jwtKey).build()
            .parseClaimsJws(refreshToken)
            .getBody();

        // Check if the refresh token is expired
        if(refreshTokenClaims.getExpiration().before(new Date()))
            throw new IllegalArgumentException("Refresh token has expired");
        
        // Header
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");
        
        // Generate a new access token
        String newAccessToken = Jwts.builder()
            .setHeader(headers)
            .setClaims(refreshTokenClaims)
            .setExpiration(new Date(new Date().getTime() + 3600000))
            .signWith(jwtKey)
            .compact();
        // Generate a new refresh token
        String newRefreshToken = Jwts.builder()
            .setHeader(headers)
            .setExpiration(new Date(new Date().getTime() + 86_400_000))
            .signWith(jwtKey)
            .compact();
        
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);
        return tokens;
    }

    boolean deleteUser(String accessToken) {
        UUID userId = UUID.fromString(verifyToken(accessToken));
        try{
            userRepository.deleteByUserId(userId);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    
}
