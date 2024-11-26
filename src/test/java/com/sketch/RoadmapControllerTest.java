package com.sketch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sketch.jwt.JwtTokenProvider;
import com.sketch.roadmap.RoadmapDTO;
import com.sketch.roadmap.RoadmapEntity;
import com.sketch.roadmap.RoadmapRepository;
import com.sketch.user.UserEntity;
import com.sketch.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RoadmapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoadmapRepository roadmapRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    private String validToken;
    private UserEntity testUser;

    @BeforeEach
    public void setup() {
        // 1. 사용자 초기화
        userRepository.deleteAll();
        testUser = new UserEntity();
        testUser.setUsername("testUser");
        testUser.setPassword(passwordEncoder.encode("testPassword"));
        testUser = userRepository.save(testUser);

        // 2. 유효한 토큰 생성
        validToken = jwtTokenProvider.generateAccessToken("testUser");

        // 3. 블랙리스트 초기화
        tokenBlacklistService.clearBlacklist();
    }

    @Test
    public void testCreateRoadmapSuccess() throws Exception {
        mockMvc.perform(get("/roadmap/createroadmap")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                .param("topic", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionData").exists())
                .andExpect(jsonPath("$.roadmapName").exists());
    }

    @Test
    public void testCreateRoadmapUnauthorized() throws Exception {
        mockMvc.perform(get("/roadmap/createroadmap")
                .param("topic", "Java"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testSaveRoadmapSuccess() throws Exception {
        RoadmapDTO roadmapDTO = new RoadmapDTO();
        roadmapDTO.setRoadmapName("Test Roadmap");
        roadmapDTO.setAchieved(0);
        roadmapDTO.setClear(false);
        roadmapDTO.setSessionData("{\"result\": \"test data\"}");

        mockMvc.perform(post("/roadmap/saveroadmap")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roadmapDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Roadmap saved successfully"));

        List<RoadmapEntity> roadmaps = roadmapRepository.findAll();
        assertEquals(1, roadmaps.size());
        assertEquals("testUser", roadmaps.get(0).getUserEntity().getUsername());
    }

    @Test
    public void testUpdateRoadmapSuccess() throws Exception {
        // Given: 저장된 RoadmapEntity
        RoadmapEntity roadmap = new RoadmapEntity();
        roadmap.setRoadmapName("Original Roadmap");
        roadmap.setUserEntity(testUser);
        roadmap.setAchieved(0);
        roadmap.setClear(false);
        roadmap.setSessionData("{\"result\": \"original data\"}");
        roadmap = roadmapRepository.save(roadmap);

        // Update DTO 생성
        RoadmapDTO updatedRoadmapDTO = new RoadmapDTO();
        updatedRoadmapDTO.setRoadmapId(roadmap.getRoadmapId());
        updatedRoadmapDTO.setRoadmapName("Updated Roadmap");
        updatedRoadmapDTO.setAchieved(50);
        updatedRoadmapDTO.setClear(true);
        updatedRoadmapDTO.setSessionData("{\"result\": \"updated data\"}");

        mockMvc.perform(put("/roadmap/updateroadmap")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRoadmapDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Roadmap updated successfully"));

        // 업데이트 확인
        RoadmapEntity updatedRoadmap = roadmapRepository.findById(roadmap.getRoadmapId()).orElseThrow();
        assertEquals("Updated Roadmap", updatedRoadmap.getRoadmapName());
        assertEquals(50, updatedRoadmap.getAchieved());
        assertTrue(updatedRoadmap.isClear());
    }

    @Test
    public void testGetAllRoadmapsSuccess() throws Exception {
        // Given: 저장된 RoadmapEntity
        RoadmapEntity roadmap = new RoadmapEntity();
        roadmap.setRoadmapName("Test Roadmap");
        roadmap.setUserEntity(testUser);
        roadmap.setAchieved(0);
        roadmap.setClear(false);
        roadmap.setSessionData("{\"result\": \"test data\"}");
        roadmapRepository.save(roadmap);

        mockMvc.perform(get("/roadmap/getallroadmaps")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roadmapName").value("Test Roadmap"))
                .andExpect(jsonPath("$[0].achieved").value(0))
                .andExpect(jsonPath("$[0].clear").value(false))
                .andExpect(jsonPath("$[0].sessionData").exists());
    }

    @Test
    public void testDeleteRoadmapSuccess() throws Exception {
        // Given: 저장된 RoadmapEntity
        RoadmapEntity roadmap = new RoadmapEntity();
        roadmap.setRoadmapName("Test Roadmap");
        roadmap.setUserEntity(testUser);
        roadmap.setAchieved(0);
        roadmap.setClear(false);
        roadmap.setSessionData("{\"result\": \"test data\"}");
        roadmap = roadmapRepository.save(roadmap);

        mockMvc.perform(delete("/roadmap/delete/" + roadmap.getRoadmapId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Roadmap deleted successfully"));

        assertTrue(roadmapRepository.findById(roadmap.getRoadmapId()).isEmpty());
    }

    @Test
    public void testUpdateRoadmapNotFound() throws Exception {
        RoadmapDTO nonExistentRoadmapDTO = new RoadmapDTO();
        nonExistentRoadmapDTO.setRoadmapId(999L);
        nonExistentRoadmapDTO.setRoadmapName("Non-existent Roadmap");
        nonExistentRoadmapDTO.setAchieved(0);
        nonExistentRoadmapDTO.setClear(false);
        nonExistentRoadmapDTO.setSessionData("{\"result\": \"test data\"}");

        mockMvc.perform(put("/roadmap/updateroadmap")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentRoadmapDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Roadmap not found"));
    }

    @Test
    public void testDeleteRoadmapNotFound() throws Exception {
        mockMvc.perform(delete("/roadmap/delete/999")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Roadmap not found"));
    }

    @Test
    public void testUnauthorizedAccess() throws Exception {
        // 각 엔드포인트에 대한 인증되지 않은 접근 테스트
        mockMvc.perform(get("/roadmap/getallroadmaps"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/roadmap/saveroadmap")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/roadmap/updateroadmap")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/roadmap/delete/1"))
                .andExpect(status().isUnauthorized());
    }
}
