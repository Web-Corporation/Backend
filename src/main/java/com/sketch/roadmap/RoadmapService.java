package com.sketch.roadmap;

import java.util.List;

public interface RoadmapService {
    RoadmapDTO createRoadmap(String topic, String accessToken);
    void saveRoadmap(RoadmapDTO roadmapDTO, String username);
    List<RoadmapDTO> getAllRoadmaps(String username);
    void deleteRoadmap(Long roadmapId, String username);
    RoadmapDTO getRoadmap(Long roadmapId);
    void updateRoadmap(RoadmapDTO roadmapDTO, String username);
}
