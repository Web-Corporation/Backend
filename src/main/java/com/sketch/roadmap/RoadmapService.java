package com.sketch.roadmap;

import java.util.List;

public interface RoadmapService {
    void saveRoadmap(RoadmapDTO roadmapDTO, String username);
    List<RoadmapDTO> getAllRoadmaps(String username);
    boolean isTokenValid(String accessToken);
    void deleteRoadmap(Long roadmapId, String username);

    // JWT 토큰에서 사용자 이름 추출
    String extractUsername(String accessToken);
}
