package com.user_management.user_management.user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${gateway.url}") String gatewayUrl;

    ResponseEntity<String> signUpEmail(UserRegisterDto userRegisterDto) throws JsonProcessingException {
        // Check if the username already exists
        if(userRepository.findByUsername(userRegisterDto.getUsername()) != null)
            return ResponseEntity.badRequest().body("USERNAME_EXISTS");
        // Validate the dto
        if(userRegisterDto.getUsername().length() < 4 || userRegisterDto.getUsername().length() > 20
        || userRegisterDto.getPassword().length() < 3 || userRegisterDto.getPassword().length() > 20
        || userRegisterDto.getEmail().length() < 8 || userRegisterDto.getEmail().length() > 50
        || !userRegisterDto.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
        ){
            return ResponseEntity.badRequest().body("INVALID_SIGNUP_INFO");
        }
        // Create a signUp token
        Map<String, Object> signupTokenClaims = new HashMap<>();
        signupTokenClaims.put("username", userRegisterDto.getUsername());
        signupTokenClaims.put("password", userRegisterDto.getPassword());
        signupTokenClaims.put("email", userRegisterDto.getEmail());
        String signUpToken = JwtUtils.generateToken(signupTokenClaims, 900000);
        // Send email
        EmailDto emailDto = EmailDto.builder()
            .to(userRegisterDto.getEmail())
            .subject("Sign up email")
            .text("Dear "+userRegisterDto.getUsername()+" Click the link to complete your sign-up.\n"+
            gatewayUrl+"/userManagement/registerUser/"+signUpToken)
            .build();
        String emailJson = new ObjectMapper().writeValueAsString(emailDto);
        try{
            kafkaTemplate.send("email", emailJson);
        } catch(Exception e){
            return ResponseEntity.badRequest().body("EMAIL_FAILED");
        }
        return ResponseEntity.ok("OK");
    }

    ResponseEntity<String> registerUser(String signUpToken) {
        // Decrypt the token
        Claims signUpTokenClaims;
        try{
            signUpTokenClaims = JwtUtils.getTokenClaims(signUpToken);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest().body("EXPIRED_TOKEN");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("INVALID_TOKEN");
        }
        // Add user to the database
        String username = signUpTokenClaims.get("username", String.class);
        String hashedPassword = new BCryptPasswordEncoder().encode(signUpTokenClaims.get("password", String.class));
        String email = signUpTokenClaims.get("email", String.class);
        try{
            userRepository.addUser(username, hashedPassword, email);
        } catch(Exception e){
            return ResponseEntity.internalServerError().body("ADD_USER_FAILED");
        }
        return ResponseEntity.ok("OK");
    }

    ResponseEntity<?> login(UserCredentialsDto userCredentialsDto) {
        // Check if the user exists and the password is correct
        UserEntity userEntity = userRepository.findByUsername(userCredentialsDto.getUsername());
        if(userEntity == null || !new BCryptPasswordEncoder().matches(userCredentialsDto.getPassword(), userEntity.getPassword()))
            return ResponseEntity.badRequest().body("INVALID_CREDENTIALS");

        // Check if the user has been blacklisted
        if(redisTemplate.opsForValue().get(userEntity.getUserId().toString()) != null)
            return ResponseEntity.badRequest().body("BLACKLISTED_USER");

        // Generate tokens
        Map<String, Object> accessTokenClaims = new HashMap<>();
        accessTokenClaims.put("userId", userEntity.getUserId());
        accessTokenClaims.put("username", userEntity.getUsername());
        String accessToken = JwtUtils.generateToken(accessTokenClaims, 1800000);

        Map<String, Object> refreshTokenClaims = new HashMap<>();
        // refreshTokenClaims.put("username", userEntity.getUsername());
        String refreshToken = JwtUtils.generateToken(refreshTokenClaims, 10800000);
        
        // Return tokens
        return ResponseEntity.ok(new TokenPairDto(accessToken, refreshToken));
    }

    ResponseEntity<String> editBlacklist(String username, String action) {
        // Find username
        UserEntity userEntity = userRepository.findByUsername(username);
        if(userEntity == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("USER_NOT_FOUND");
        // Add the user in the blacklist
        if(action == "add"){
            redisTemplate.opsForValue().set(userEntity.getUserId().toString(), "X", 43200000, TimeUnit.MILLISECONDS); // 12hours
            // redisTemplate.opsForValue().set(username, "X");
            // redisTemplate.opsForValue().getOperations().expireAt(userId, new Date(System.currentTimeMillis() + 43200000));
        }
        else if(action.equals("remove")){
            redisTemplate.delete(userEntity.getUserId().toString());
        }
        return ResponseEntity.ok("OK");
    }

    ResponseEntity<?> verifyAccessToken(String accessToken) {
        // Decrypt the token
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
        // Decrypt the token
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("USER_NOT_FOUND");

        return ResponseEntity.ok(new UserInfoDto(userEntity.getUserId(), userEntity.getUsername(), userEntity.getEmail()));
    }
    
    ResponseEntity<?> reissueToken(String accessToken, String refreshToken) {
        // Decrypt the token
        Claims accessTokenClaims = null, refreshTokenClaims;
        try{
            accessTokenClaims = JwtUtils.getTokenClaims(accessToken);
        } catch (ExpiredJwtException e) {
            accessTokenClaims = e.getClaims();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("INVALID_TOKEN");
        }
        try{
            refreshTokenClaims = JwtUtils.getTokenClaims(refreshToken);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest().body("EXPIRED_TOKEN");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("INVALID_TOKEN");
        }


        // Block the request if the user has been blacklisted
        String userId = accessTokenClaims.get("userId", String.class);
        if(redisTemplate.opsForValue().get(userId) != null)
            return ResponseEntity.badRequest().body("BLACKLISTED_USER");

        // Generate new tokens
        String newAccessToken = JwtUtils.generateToken(accessTokenClaims, 1800000);
        String newRefreshToken = JwtUtils.generateToken(refreshTokenClaims, 10800000);
        
        // Return the new tokens
        return ResponseEntity.ok(new TokenPairDto(newAccessToken, newRefreshToken));
    }

    @Transactional
    ResponseEntity<String> deleteUser(String accessToken) {
        // Decrypt the token
        Claims accessTokenClaims;
        try{
            accessTokenClaims = JwtUtils.getTokenClaims(accessToken);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest().body("EXPIRED_TOKEN");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("INVALID_TOKEN");
        }
        // Ensure the token is not a refresh token
        UUID userId = UUID.fromString(accessTokenClaims.get("userId", String.class));
        if(userId == null)
            return ResponseEntity.badRequest().body("REFRESH_TOKEN_NOT_ALLOWED");
        // Check if the user exists
        if(userRepository.existsByUserId(userId) == false)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("USER_NOT_FOUND");
        // Delete the user
        userRepository.deleteByUserId(userId);
        return ResponseEntity.ok("OK");
    }
}