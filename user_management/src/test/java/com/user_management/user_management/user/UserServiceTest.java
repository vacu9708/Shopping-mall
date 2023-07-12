package com.user_management.user_management.user;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.UUID;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.user_management.user_management.user.Dto.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    void registerUser_successful() {
        // Given
        UserRegisterDto userRegisterDto = new UserRegisterDto("username", "password", "email");
        // Mock the userRepository.addUser method to return without throwing an exception
        doNothing().when(userRepository).addUser(any(), any(), any());

        // When
        boolean result = userService.registerUser(userRegisterDto);
        // Then
        assertTrue(result, "User registration should be successful");

    }

    @Test
    void registerUser_failed() {
        // Given
        UserRegisterDto userRegisterDto = new UserRegisterDto("username", "password", "email");
        // Mock the userRepository.addUser method to throw an exception
        doThrow(RuntimeException.class).when(userRepository).addUser(any(), any(), any());

        // When
        boolean result = userService.registerUser(userRegisterDto);
        // Then
        assertFalse(result, "User registration should fail");
    }

    @Test
    void login_successful(){
        // Given
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("username", "password");
        // Mock the userRepository.findByUsername method to return a user entity
        String hashedPassword = new BCryptPasswordEncoder().encode("password");
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "username", hashedPassword, "email");
        when(userRepository.findByUsername(any())).thenReturn(userEntity);

        // When
        Map<String, String> result = userService.login(userCredentialsDto);
        // Then
        assertNotNull(result.get("accessToken"), "Access token should not be null");
        assertNotNull(result.get("refreshToken"), "Refresh token should not be null");
    }

    @Test
    void login_wrongPassword(){
        // Given
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("username", "wrong_password");
        // Mock the userRepository.findByUsername method to return a user entity
        String hashedPassword = new BCryptPasswordEncoder().encode("password");
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "username", hashedPassword, "email");
        when(userRepository.findByUsername(any())).thenReturn(userEntity);

        // When, Then
        assertThrows(IllegalArgumentException.class, () -> userService.login(userCredentialsDto));
    }

    @Test
    void login_userNotExisting(){
        // Given
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("username", "password");
        // Mock the userRepository.findByUsername method to return null
        when(userRepository.findByUsername(any())).thenReturn(null);

        // When, Then
        assertThrows(IllegalArgumentException.class, () -> userService.login(userCredentialsDto));
    }

    @Test
    void verifyToken_successful(){
        // Given
        // Login to get a token
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("username", "password");
        // Mock the userRepository.findByUsername method to return a user entity
        String hashedPassword = new BCryptPasswordEncoder().encode("password");
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "username", hashedPassword, "email");
        when(userRepository.findByUsername(any())).thenReturn(userEntity);

        Map<String, String> tokens = userService.login(userCredentialsDto);

        // When
        String result = userService.verifyToken(tokens.get("accessToken"));
        // Then
        assertNotNull(result, "UserId should not be null");
    }

    @Test
    void reissueToken_successful(){
        // Given
        // Login to get a token
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("username", "password");
        // Mock the userRepository.findByUsername method to return a user entity
        String hashedPassword = new BCryptPasswordEncoder().encode("password");
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "username", hashedPassword, "email");
        when(userRepository.findByUsername(any())).thenReturn(userEntity);

        Map<String, String> tokens = userService.login(userCredentialsDto);

        // When
        Map<String, String> result = userService.reissueToken(tokens.get("refreshToken"));
        // Then
        assertNotNull(result.get("accessToken"), "Access token should not be null");
        assertNotNull(result.get("refreshToken"), "Refresh token should not be null");
    }

    @Test
    void deleteUser_successful(){
        // Given
        // Login to get a token
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("username", "password");
        // Mock the userRepository.findByUsername method to return a user entity
        String hashedPassword = new BCryptPasswordEncoder().encode("password");
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "username", hashedPassword, "email");
        when(userRepository.findByUsername(any())).thenReturn(userEntity);

        Map<String, String> tokens = userService.login(userCredentialsDto);

        // When
        boolean result = userService.deleteUser(tokens.get("accessToken"));
        // Then
        assertTrue(result, "User deletion should be successful");
    }
}
