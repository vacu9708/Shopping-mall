package com.user_management.user_management.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_management.user_management.user.Dto.*;
import com.user_management.user_management.user.Utils.JwtUtils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired RedisTemplate<String, String> redisTemplate;
    @Autowired UserService authService;
    @MockBean KafkaTemplate<String, String> kafkaTemplate;

    // Clean up after testing
    @AfterAll
    static void cleanUp(@Autowired UserRepository userRepository) {
        userRepository.deleteByUsername("testUser");
    }

    // Wait for redis to start
    // @BeforeAll
    // static void init(@Autowired RedisTemplate<String, String> redisTemplate) throws InterruptedException {
    //     Thread.sleep(5000);
    // }

    @Test
    void authIntegrationTest() throws Exception {
        signUpEmail_successful();
        registerUser_successful();
        signUpEmail_duplicate();
        login_successful();
        login_wrongPassword();
        addInBlacklist_successful();
        login_BlacklistedUser();
        removeFromBlacklist_successful();
        login_successful();
        reissueToken_successful();
        deleteUser_successful();
    }

    void signUpEmail_successful() throws Exception {
        // Mock KafkaTemplate
        when(kafkaTemplate.send(any(), any())).thenReturn(null);
        UserRegisterDto userRegisterDto = new UserRegisterDto("testUser", "testPassword", "testEmail@naver.com");
        mockMvc.perform(post("/signUpEmail")
                        .content(objectMapper.writeValueAsString(userRegisterDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        )
                .andExpect(status().isOk());
    }

    void registerUser_successful() throws Exception {
        UserRegisterDto userRegisterDto = new UserRegisterDto("testUser", "testPassword", "testEmail@naver.com");
        Map<String, Object> signupTokenClaims = new HashMap<>();
        signupTokenClaims.put("username", userRegisterDto.getUsername());
        signupTokenClaims.put("password", userRegisterDto.getPassword());
        signupTokenClaims.put("email", userRegisterDto.getEmail());
        String signUpToken = JwtUtils.generateToken(signupTokenClaims, 900000);
        mockMvc.perform(get("/registerUser/"+signUpToken)
                        )
                .andExpect(status().isOk());
    }

    void signUpEmail_duplicate() throws Exception {
        UserRegisterDto userRegisterDto = new UserRegisterDto("testUser", "testPassword", "testEmail@naver.com");
        mockMvc.perform(post("/signUpEmail")
                        .content(objectMapper.writeValueAsString(userRegisterDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("USERNAME_EXISTS"));
    }

    void login_successful() throws Exception {
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("testUser", "testPassword");

        mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(userCredentialsDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        )
                .andExpect(status().isOk());
                // .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    void login_wrongPassword() throws Exception {
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("testUser", "wrongPassword");

        mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(userCredentialsDto))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string("INVALID_CREDENTIALS"));
    }

    void addInBlacklist_successful() throws Exception {
        String username = "testUser";
        mockMvc.perform(post("/manager/addInBlacklist/"+username)
                        .header("password", "123")
                        )
                        .andExpect(status().isOk());
    }

    void login_BlacklistedUser() throws Exception {
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("testUser", "testPassword");

        mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(userCredentialsDto))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string("BLACKLISTED_USER"));
    }

    void removeFromBlacklist_successful() throws Exception {
        String username = "testUser";
        mockMvc.perform(delete("/manager/removeFromBlacklist/"+username)
                        .header("password", "123")
                        )
                        .andExpect(status().isOk());
    }

    void reissueToken_successful() throws Exception{
        // Login to get access token
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("testUser", "testPassword");
        TokenPairDto tokenPairDto = (TokenPairDto) authService.login(userCredentialsDto).getBody();
        String accessToken = tokenPairDto.getAccessToken();
        String refreshToken = tokenPairDto.getRefreshToken();

        mockMvc.perform(get("/reissueTokens")
                        .header("accessToken", accessToken)
                        .header("refreshToken", refreshToken)
                        )
                        .andExpect(status().isOk());
    }

    void deleteUser_successful() throws Exception {
        // Login to get access token
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("testUser", "testPassword");
        TokenPairDto tokenPairDto = (TokenPairDto) authService.login(userCredentialsDto).getBody();
        String accessToken = tokenPairDto.getAccessToken();

        mockMvc.perform(delete("/deleteUser")
                        .header("accessToken", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }
}
