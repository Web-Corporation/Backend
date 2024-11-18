package com.sketch.userTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RoadmapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateRoadmap() throws Exception {
        String topic = "java";
        String token = "Bearer sample_valid_token";

        mockMvc.perform(get("/roadmap/createroadmap")
                        .header("Authorization", token)
                        .param("topic", topic))
                .andExpect(status().isOk())
                .andExpect(content().string("Roadmap created successfully")); // Replace with actual Flask response
    }
}
