package com.user_management.user_management.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_management.user_management.user.UserRepository;
import com.user_management.user_management.user.UserService;
import com.user_management.user_management.user.Dto.*;
import com.user_management.user_management.user.Utils.JwtUtils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UserService authService;

    // Clean up after testing
    @AfterAll
    static void cleanUp(@Autowired UserRepository userRepository) {
        userRepository.deleteByUsername("testUser");
    }

    @Test
    void authIntegrationTest() throws Exception {
        registerUser_successful();
        login_successful();
        login_wrongPassword();
        login_BlacklistedUser();
        deleteUser_successful();
    }

    void registerUser_successful() throws Exception {
        UserRegisterDto userRegisterDto = new UserRegisterDto("testUser", "testPassword", "testEmail");

        mockMvc.perform(post("/registerUser")
                        .content(objectMapper.writeValueAsString(userRegisterDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    void login_successful() throws Exception {
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("testUser", "testPassword");

        mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(userCredentialsDto))
                        .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    void login_wrongPassword() throws Exception {
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("testUser", "wrongPassword");

        mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(userCredentialsDto))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string("INVALID_CREDENTIALS"));
    }

    void login_BlacklistedUser() throws Exception {
        String username = "testUser";
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto(username, "testPassword");
        // Blacklist the user
        redisTemplate.opsForValue().set(username, "X", 43200000, TimeUnit.MILLISECONDS); // 12hours

        mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(userCredentialsDto))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string("BLACKLISTED_USER"));
        
        // Clean up
        redisTemplate.delete(username);
    }

    void deleteUser_successful() throws Exception {
        // Get access token
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("testUser", "testPassword");
        TokenPairDto tokenPairDto = (TokenPairDto) authService.login(userCredentialsDto).getBody();
        String accessToken = tokenPairDto.getAccessToken();

        mockMvc.perform(delete("/deleteUser")
                        .header("accessToken", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }
}
