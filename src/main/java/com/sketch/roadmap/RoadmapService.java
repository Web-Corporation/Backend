package com.sketch.roadmap;

import java.util.List;

public interface RoadmapService {
    String createRoadmap(String topic, String accessToken);
    void saveRoadmap(RoadmapDTO roadmapDTO);
    List<RoadmapDTO> getAllRoadmaps(String username);
    void deleteRoadmap(Long roadmapId, String username);
    RoadmapDTO getRoadmap(Long roadmapId);
}
