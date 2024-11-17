package com.sketch.roadmap;
import com.sketch.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoadmapServiceImpl implements RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public RoadmapServiceImpl(RoadmapRepository roadmapRepository, JwtTokenProvider jwtTokenProvider) {
        this.roadmapRepository = roadmapRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void saveRoadmap(RoadmapDTO roadmapDTO) {
        // DTO -> Entity 변환
        RoadmapEntity roadmapEntity = new RoadmapEntity();
        roadmapEntity.setUsername(roadmapDTO.getUsername());
        roadmapEntity.setAccessToken(roadmapDTO.getAccessToken());
        roadmapEntity.setProfile(roadmapDTO.getProfile());
        roadmapEntity.setAchieved(roadmapDTO.getAchieved());
        roadmapEntity.setClear(roadmapDTO.isClear());
        roadmapEntity.setSessionData(roadmapDTO.getSessionData()); // JSON 문자열 저장

        roadmapRepository.save(roadmapEntity);
    }

    @Override
    public List<RoadmapDTO> getAllRoadmaps(String username) {
        // 사용자의 모든 로드맵 조회 및 변환
        return roadmapRepository.findByUsername(username).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRoadmap(Long roadmapId, String username) {
        RoadmapEntity roadmapEntity = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        // 사용자 권한 확인
        if (!roadmapEntity.getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized access to roadmap");
        }

        // 로드맵 삭제
        roadmapRepository.delete(roadmapEntity);
    }

    @Override
    public boolean isTokenValid(String accessToken) {
        return jwtTokenProvider.validateToken(accessToken);
    }

    private RoadmapDTO convertToDTO(RoadmapEntity roadmapEntity) {
        RoadmapDTO roadmapDTO = new RoadmapDTO();
        roadmapDTO.setUsername(roadmapEntity.getUsername());
        roadmapDTO.setAccessToken(roadmapEntity.getAccessToken());
        roadmapDTO.setProfile(roadmapEntity.getProfile());
        roadmapDTO.setAchieved(roadmapEntity.getAchieved());
        roadmapDTO.setClear(roadmapEntity.isClear());
        roadmapDTO.setSessionData(roadmapEntity.getSessionData()); // JSON 데이터를 그대로 전달
        return roadmapDTO;
    }

    @Override
    public String extractUsername(String accessToken) {
        return jwtTokenProvider.extractUsername(accessToken);
    }
}
