package com.sketch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sketch.jwt.JwtTokenProvider;
import com.sketch.roadmap.*;
import com.sketch.user.UserEntity;
import com.sketch.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
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

    private String validToken;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        roadmapRepository.deleteAll();

        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        userRepository.save(user);

        validToken = jwtTokenProvider.generateAccessToken("testUser");
    }

    @Test
    public void testCreateRoadmapSuccess() throws Exception {
        mockMvc.perform(get("/roadmap/createroadmap")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                        .param("topic", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roadmap").exists());
    }

    @Test
    public void testCreateRoadmapUnauthorized() throws Exception {
        mockMvc.perform(get("/roadmap/createroadmap")
                        .param("topic", "Java")) // 토큰 없음
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testSaveRoadmapSuccess() throws Exception {
        RoadmapDTO roadmapDTO = new RoadmapDTO();
        roadmapDTO.setUsername("testUser");
        roadmapDTO.setAchieved(50);
        roadmapDTO.setClear(false);
        roadmapDTO.setSessionData("{\"step\":\"1\",\"progress\":\"50%\"}");

        mockMvc.perform(post("/roadmap/saveroadmap")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roadmapDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Roadmap saved successfully"));
    }

    @Test
    public void testGetRoadmapSuccess() throws Exception {
        RoadmapEntity roadmap = new RoadmapEntity();
        roadmap.setUsername("testUser");
        roadmap.setAchieved(75);
        roadmap.setClear(false);
        roadmap.setSessionData("{\"step\":\"3\",\"progress\":\"75%\"}");
        roadmap = roadmapRepository.save(roadmap);

        mockMvc.perform(get("/roadmap/getroadmap")
                        .param("roadmapID", roadmap.getRoadmapId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.achieved").value(75))
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    public void testGetRoadmapNotFound() throws Exception {
        mockMvc.perform(get("/roadmap/getroadmap")
                        .param("roadmapID", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteRoadmapSuccess() throws Exception {
        RoadmapEntity roadmap = new RoadmapEntity();
        roadmap.setUsername("testUser");
        roadmap.setAchieved(100);
        roadmap.setClear(true);
        roadmap.setSessionData("{\"step\":\"5\",\"progress\":\"100%\"}");
        roadmap = roadmapRepository.save(roadmap);

        mockMvc.perform(delete("/roadmap/delete/" + roadmap.getRoadmapId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Roadmap deleted successfully"));
    }
}

