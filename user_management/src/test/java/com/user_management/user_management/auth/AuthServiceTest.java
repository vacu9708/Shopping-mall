package com.user_management.user_management.auth;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.UUID;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.user_management.user_management.auth.Dto.*;
import com.user_management.user_management.auth.Utils.JwtUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthServiceTest {
    @Mock AuthRepository authRepository;
    @Mock RedisTemplate<String, String> redisTemplate;
    @Mock ValueOperations<String, String> valueOperations;

    @InjectMocks
    AuthService authService;
    
    static final Logger logger = LoggerFactory.getLogger(AuthServiceTest.class);

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
        // Mock authRepository.addUser() to return without throwing an exception
        doNothing().when(authRepository).addUser(any(), any(), any());
        // When
        boolean result = authService.registerUser(userRegisterDto);
        // Then
        assertTrue(result, "User registration should be successful");
    }

    @Test
    void registerUser_failed() {
        // Given
        UserRegisterDto userRegisterDto = new UserRegisterDto("username", "password", "email");
        // Mock authRepository.addUser() method to throw an exception
        doThrow(RuntimeException.class).when(authRepository).addUser(any(), any(), any());
        // When
        boolean result = authService.registerUser(userRegisterDto);
        // Then
        assertFalse(result, "User registration should fail");
    }

    @Test
    void login_successful(){
        // Given
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("username", "password");
        // Mock authRepository.findByUsername() to return a user entity
        String hashedPassword = new BCryptPasswordEncoder().encode("password");
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "username", hashedPassword, "email");
        when(authRepository.findByUsername(any())).thenReturn(userEntity);
        // When
        Map<String, String> result = authService.login(userCredentialsDto);
        // Then
        assertNotNull(result.get("accessToken"), "Access token should not be null");
        assertNotNull(result.get("refreshToken"), "Refresh token should not be null");
    }

    @Test
    void login_wrongPassword(){
        // Given
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("username", "wrong_password");
        // Mock authRepository.findByUsername() to return a user entity
        String hashedPassword = new BCryptPasswordEncoder().encode("password");
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "username", hashedPassword, "email");
        when(authRepository.findByUsername(any())).thenReturn(userEntity);
        // When, Then
        assertThrows(IllegalArgumentException.class, () -> authService.login(userCredentialsDto));
    }

    @Test
    void login_userNotExisting(){
        // Given
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("username", "password");
        // Mock authRepository.findByUsername() to return null
        when(authRepository.findByUsername(any())).thenReturn(null);
        // When, Then
        assertThrows(IllegalArgumentException.class, () -> authService.login(userCredentialsDto));
    }

    @Test
    void addInBlacklist_successful(){
        // Given
        UUID userId = UUID.randomUUID();
        String accessToken = JwtUtils.generateToken(userId.toString(), 900000);
        // Mock authRepository.findByUsername() to return a user entity
        UserEntity userEntity = new UserEntity(userId, "admin", "password", "email");
        when(authRepository.findByUserId(any())).thenReturn(userEntity);
        // Mock redisTemplate
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(any(), any());       
        // When
        boolean result = authService.addInBlacklist(accessToken, userEntity.getUsername());
        // Then
        assertTrue(result, "Adding in blacklist should be successful");
    }

    @Test
    void verifyToken_successful(){
        // Given
        String accessToken = JwtUtils.generateToken(UUID.randomUUID().toString(), 900000);
        // When
        String result = authService.verifyToken(accessToken);
        // Then
        assertNotNull(result, "UserId should not be null");
    }

    @Test
    void verifyToken_invalidToken(){
        // Given
        String accessToken = JwtUtils.generateToken(UUID.randomUUID().toString(), 900000);
        // When, Then
        assertThrows(IllegalArgumentException.class, () -> authService.verifyToken(accessToken + "invalid"));
    }

    @Test
    void verifyToken_expiredToken(){
        // Given
        String accessToken = JwtUtils.generateToken(UUID.randomUUID().toString(), -10);
        // When, Then
        assertThrows(IllegalArgumentException.class, () -> authService.verifyToken(accessToken));
    }

    @Test
    void reissueToken_successful(){
        // Given
        String refreshToken = JwtUtils.generateToken(UUID.randomUUID().toString(), 43200000);
        // Mock
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(any())).thenReturn(null);
        // When
        Map<String, String> result = authService.reissueToken(refreshToken);
        // Then
        assertNotNull(result.get("accessToken"), "Access token should not be null");
        assertNotNull(result.get("refreshToken"), "Refresh token should not be null");
    }

    @Test
    void reissueToken_blacklistFail(){
        // Given
        String refreshToken = JwtUtils.generateToken(UUID.randomUUID().toString(), 43200000);
        // Mock
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(any())).thenReturn("X");
        // When, Then
        assertThrows(IllegalArgumentException.class, () -> authService.reissueToken(refreshToken));
    }

    @Test
    void deleteUser_successful(){
        // Given
        String accessToken = JwtUtils.generateToken(UUID.randomUUID().toString(), 900000);
        // Mock authRepository.findByUsername() to return a user entity
        String hashedPassword = new BCryptPasswordEncoder().encode("password");
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "username", hashedPassword, "email");
        when(authRepository.findByUsername(any())).thenReturn(userEntity);
        
        // When
        boolean result = authService.deleteUser(accessToken);
        // Then
        assertTrue(result, "User deletion should be successful");
    }
}
