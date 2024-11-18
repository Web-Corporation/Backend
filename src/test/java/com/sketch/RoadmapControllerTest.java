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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
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
        roadmapRepository.deleteAll();
        userRepository.deleteAll();

        // Add test user
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        user.setPassword("password");
        userRepository.save(user);

        validToken = jwtTokenProvider.generateAccessToken(user.getUsername());
    }

    @Test
    public void testCreateRoadmapSuccess() throws Exception {
        mockMvc.perform(get("/roadmap/createroadmap")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                        .param("topic", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionData").exists());
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
        roadmapDTO.setAchieved(0);
        roadmapDTO.setClear(false);
        roadmapDTO.setSessionData("{\"result\": \"sample roadmap data\"}");

        mockMvc.perform(post("/roadmap/saveroadmap")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roadmapDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Roadmap saved successfully"));
    }

    @Test
    public void testSaveRoadmapUnauthorized() throws Exception {
        RoadmapDTO roadmapDTO = new RoadmapDTO();
        roadmapDTO.setAchieved(0);
        roadmapDTO.setClear(false);
        roadmapDTO.setSessionData("{\"result\": \"sample roadmap data\"}");

        mockMvc.perform(post("/roadmap/saveroadmap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roadmapDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetAllRoadmapsSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername("testUser").orElseThrow();

        RoadmapEntity roadmap = new RoadmapEntity();
        roadmap.setUserEntity(user);
        roadmap.setAchieved(0);
        roadmap.setClear(false);
        roadmap.setSessionData("{\"result\": \"sample roadmap data\"}");
        roadmapRepository.save(roadmap);

        mockMvc.perform(get("/roadmap/getallroadmaps")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sessionData").exists());
    }

    @Test
    public void testGetAllRoadmapsUnauthorized() throws Exception {
        mockMvc.perform(get("/roadmap/getallroadmaps"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetRoadmapSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername("testUser").orElseThrow();

        RoadmapEntity roadmap = new RoadmapEntity();
        roadmap.setUserEntity(user);
        roadmap.setAchieved(0);
        roadmap.setClear(false);
        roadmap.setSessionData("{\"result\": \"sample roadmap data\"}");
        roadmapRepository.save(roadmap);

        mockMvc.perform(get("/roadmap/getroadmap")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                        .param("roadmapID", roadmap.getRoadmapId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionData").exists());
    }

    @Test
    public void testGetRoadmapNotFound() throws Exception {
        mockMvc.perform(get("/roadmap/getroadmap")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                        .param("roadmapID", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteRoadmapSuccess() throws Exception {
        UserEntity user = userRepository.findByUsername("testUser").orElseThrow();

        RoadmapEntity roadmap = new RoadmapEntity();
        roadmap.setUserEntity(user);
        roadmap.setAchieved(0);
        roadmap.setClear(false);
        roadmap.setSessionData("{\"result\": \"sample roadmap data\"}");
        roadmapRepository.save(roadmap);

        mockMvc.perform(delete("/roadmap/delete/" + roadmap.getRoadmapId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Roadmap deleted successfully"));
    }

    @Test
    public void testDeleteRoadmapUnauthorized() throws Exception {
        UserEntity user = userRepository.findByUsername("testUser").orElseThrow();

        RoadmapEntity roadmap = new RoadmapEntity();
        roadmap.setUserEntity(user);
        roadmap.setAchieved(0);
        roadmap.setClear(false);
        roadmap.setSessionData("{\"result\": \"sample roadmap data\"}");
        roadmapRepository.save(roadmap);

        mockMvc.perform(delete("/roadmap/delete/" + roadmap.getRoadmapId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteRoadmapForbidden() throws Exception {
        UserEntity otherUser = new UserEntity();
        otherUser.setUsername("anotherUser");
        otherUser.setPassword("password");
        userRepository.save(otherUser);

        RoadmapEntity roadmap = new RoadmapEntity();
        roadmap.setUserEntity(otherUser);
        roadmap.setAchieved(0);
        roadmap.setClear(false);
        roadmap.setSessionData("{\"result\": \"sample roadmap data\"}");
        roadmapRepository.save(roadmap);

        mockMvc.perform(delete("/roadmap/delete/" + roadmap.getRoadmapId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unauthorized access to roadmap"));
    }
}
