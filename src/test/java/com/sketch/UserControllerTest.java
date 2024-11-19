package com.sketch;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sketch.jwt.JwtTokenProvider;
import com.sketch.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    private String validToken;

    @BeforeEach
    public void setup() {
        // 1. 사용자 초기화
        userRepository.deleteAll();
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        user.setPassword(passwordEncoder.encode("testPassword"));
        userRepository.save(user);

        // 2. 유효한 토큰 생성
        validToken = jwtTokenProvider.generateAccessToken("testUser");

        // 3. 블랙리스트 초기화
        tokenBlacklistService.clearBlacklist(); // 전체 블랙리스트 초기화
    }

    @Test
    public void testRegisterUserSuccess() throws Exception {
        UserSaveDTO userSaveDTO = new UserSaveDTO();
        userSaveDTO.setUsername("newUser");
        userSaveDTO.setPassword("newPassword");

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSaveDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    public void testRegisterUserConflict() throws Exception {
        UserSaveDTO userSaveDTO = new UserSaveDTO();
        userSaveDTO.setUsername("testUser"); // 이미 존재하는 사용자
        userSaveDTO.setPassword("newPassword");

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSaveDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Username already exists"));
    }

    @Test
    public void testLoginUserSuccess() throws Exception {
        UserLoginDTO loginRequest = new UserLoginDTO();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPassword"); // setup()에서 사용한 비밀번호와 동일해야 함

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // 성공적으로 로그인
                .andExpect(jsonPath("$.accessToken").exists()) // accessToken 반환
                .andExpect(jsonPath("$.refreshToken").exists()); // refreshToken 반환
    }

    @Test
    public void testLoginUserUnauthorized() throws Exception {
        UserLoginDTO loginRequest = new UserLoginDTO();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("wrongPassword"); // 잘못된 비밀번호

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        mockMvc.perform(post("/users/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));
    }

    @Test
    public void testLogoutUnauthorizedInvalidToken() throws Exception {
        String invalidToken = "invalid.token.example";
        mockMvc.perform(post("/users/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid token"));
    }

    @Test
    public void testLogoutNoToken() throws Exception {
        mockMvc.perform(post("/users/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
