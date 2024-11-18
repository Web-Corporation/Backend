package com.sketch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sketch.jwt.JwtTokenProvider;
import com.sketch.user.UserEntity;
import com.sketch.user.UserLoginDTO;
import com.sketch.user.UserRepository;
import com.sketch.user.UserSaveDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

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

    @BeforeEach
    public void setup() {
        // 테스트 사용자 초기화
        userRepository.deleteAll();
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        user.setPassword(passwordEncoder.encode("testPassword"));
        userRepository.save(user);
        validToken = jwtTokenProvider.generateAccessToken("testUser");
    }

    private String validToken;

    @Test
    public void testRegisterUser() throws Exception {
        // 회원가입 요청 데이터
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
    public void testLoginUser() throws Exception {
        // 로그인 요청 데이터
        UserLoginDTO loginRequest = new UserLoginDTO();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPassword");

        ResultActions result = mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    public void testLogoutUser() throws Exception {
        // 유효한 토큰으로 로그아웃 요청
        mockMvc.perform(post("/users/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful"));
    }

    @Test
    public void testLogoutWithInvalidToken() throws Exception {
        // 잘못된 토큰으로 로그아웃 요청
        mockMvc.perform(post("/users/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogoutWithoutToken() throws Exception {
        // 토큰 없이 로그아웃 요청
        mockMvc.perform(post("/users/logout"))
                .andExpect(status().isUnauthorized());
    }
}
