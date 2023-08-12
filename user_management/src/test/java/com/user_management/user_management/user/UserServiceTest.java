package com.user_management.user_management.user;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.user_management.user_management.user.UserRepository;
import com.user_management.user_management.user.UserService;
import com.user_management.user_management.user.UserEntity;
import com.user_management.user_management.user.Dto.*;
import com.user_management.user_management.user.Utils.JwtUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {
    @Mock UserRepository authRepository;
    @Mock RedisTemplate<String, String> redisTemplate;
    @Mock ValueOperations<String, String> valueOperations;

    @InjectMocks
    UserService authService;
    
    // static final Logger logger = LoggerFactory.getLogger(AuthServiceTest.class);

    // public AuthServiceTest() { // doesn't work
    //     MockitoAnnotations.openMocks(this);
    //     JwtUtils = new JwtUtils();
    //     authService = new AuthService(authRepository, redisTemplate, JwtUtils);
    // }

    // @BeforeEach
    // void setUp() {
    //     when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    // }

    @Test
    void registerUser_successful() {
        // Given
        UserRegisterDto userRegisterDto = new UserRegisterDto("username", "password", "email");
        // Mock authRepository.findByUsername() to return null
        when(authRepository.findByUsername(any())).thenReturn(null);
        // Mock authRepository.addUser() to do nothing
        doNothing().when(authRepository).addUser(any(), any(), any());
        // When
        ResponseEntity<String> response = authService.registerUser(userRegisterDto);
        // Then
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void registerUser_duplicateUser() {
        // Given
        UserRegisterDto userRegisterDto = new UserRegisterDto("username", "password", "email");
        // Mock authRepository.findByUsername() to return a user entity
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "username", "password", "email");
        when(authRepository.findByUsername(any())).thenReturn(userEntity);
        // When
        ResponseEntity<String> response = authService.registerUser(userRegisterDto);
        // Then
        assertNotEquals(200, response.getStatusCode().value());
        assertEquals("USERNAME_EXISTS", response.getBody());
    }

    @Test
    void login_successful(){
        // Given
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("username", "password");
        // Mock authRepository.findByUsername() to return a user entity
        String hashedPassword = new BCryptPasswordEncoder().encode("password");
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "username", hashedPassword, "email");
        when(authRepository.findByUsername(any())).thenReturn(userEntity);
        // Mock redis template
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(any())).thenReturn(null);
        // When
        ResponseEntity<?> response = authService.login(userCredentialsDto);
        // Then
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void login_wrongPassword(){
        // Given
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("username", "wrong_password");
        // Mock authRepository.findByUsername() to return a user entity
        String hashedPassword = new BCryptPasswordEncoder().encode("password");
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "username", hashedPassword, "email");
        when(authRepository.findByUsername(any())).thenReturn(userEntity);
        // When
        ResponseEntity<?> response = authService.login(userCredentialsDto);
        // Then
        assertNotEquals(200, response.getStatusCode().value());
        assertEquals("INVALID_CREDENTIALS", response.getBody());
    }

    @Test
    void login_userNotExisting(){
        // Given
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("username", "password");
        // Mock authRepository.findByUsername() to return null
        when(authRepository.findByUsername(any())).thenReturn(null);
        // When
        ResponseEntity<?> response = authService.login(userCredentialsDto);
        // Then check if the status code is 400 BAD REQUEST using response.getStatusCode()
        assertNotEquals(200, response.getStatusCode().value());
        assertEquals("INVALID_CREDENTIALS", response.getBody());
    }

    @Test
    void verifyAccessToken_successful(){
        // Given
        HashMap<String, Object> accessTokenClaims = new HashMap<String, Object>();
        accessTokenClaims.put("userId", UUID.randomUUID().toString());
        accessTokenClaims.put("username", "testUsername");
        String accessToken = JwtUtils.generateToken(accessTokenClaims, 900000);
        // When
        ResponseEntity<?> response = authService.verifyAccessToken(accessToken);
        // Then
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void editBlacklistAdd_successful(){
        // Given
        HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put("userId", UUID.randomUUID());
        String accessToken = JwtUtils.generateToken(claims, 900000);
        // Mock authRepository.findByUserId() to return an admin entity
        UserEntity adminEntity = new UserEntity(UUID.randomUUID(), "admin", "password", "email");
        when(authRepository.findByUsername(any())).thenReturn(adminEntity);
        // Mock redisTemplate
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(any(), any(), anyLong(), any());
        // When
        ResponseEntity<String> response = authService.editBlacklist(accessToken, "blacklisted user", "add");
        // Then
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void verifyToken_invalidToken(){
        // When
        ResponseEntity<?> response = authService.verifyAccessToken("invaild token");
        // Then
        assertNotEquals(200, response.getStatusCode().value());
    }

    @Test
    void verifyToken_expiredToken(){
        // Given
        HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put("userId", UUID.randomUUID());
        String accessToken = JwtUtils.generateToken(claims, -10);
        // When
        ResponseEntity<?> response = authService.verifyAccessToken(accessToken);
        // Then
        assertNotEquals(200, response.getStatusCode().value());
    }

    @Test
    void reissueToken_successful(){
        // Given
        HashMap<String, Object> accessTokenClaims = new HashMap<String, Object>();
        accessTokenClaims.put("userId", UUID.randomUUID().toString());
        accessTokenClaims.put("username", "testUsername");
        String accessToken = JwtUtils.generateToken(accessTokenClaims, 900000);

        HashMap<String, Object> refreshTokenClaims = new HashMap<String, Object>();
        refreshTokenClaims.put("username", "username");
        String refreshToken = JwtUtils.generateToken(refreshTokenClaims, 900000);
        // Mock redis template
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(any())).thenReturn(null);
        // When
        ResponseEntity<?> response = authService.reissueToken(accessToken, refreshToken);
        // Then
        assertEquals(200, response.getStatusCode().value());
    }

    void deleteUser_successful(){
        // Given
        HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put("userId", UUID.randomUUID());
        String accessToken = JwtUtils.generateToken(claims, 900000);
        // Mock authRepository.deleteByUserId() to do nothing
        doNothing().when(authRepository).deleteByUsername(any());
        // When
        ResponseEntity<String> response = authService.deleteUser(accessToken);
        // Then
        assertEquals(200, response.getStatusCode().value());
    }
}