package com.sketch.roadmap;
import com.sketch.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class RoadmapServiceImpl implements RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;

    @Value("${PYTHON_SERVICE_URL}")
    private String flaskServiceUrl;

    @Autowired
    public RoadmapServiceImpl(RoadmapRepository roadmapRepository, JwtTokenProvider jwtTokenProvider, RestTemplate restTemplate) {
        this.roadmapRepository = roadmapRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.restTemplate = restTemplate;
    }

    @Override
    public String createRoadmap(String topic, String accessToken) {
        String flaskUrl = flaskServiceUrl + "/createroadmap?topic=" + topic;

        try {
            return restTemplate.getForObject(flaskUrl, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with Flask service: " + e.getMessage());
        }
    }

    @Override
    public void saveRoadmap(RoadmapDTO roadmapDTO) {
        // DTO -> Entity 변환
        RoadmapEntity roadmapEntity = new RoadmapEntity();
        roadmapEntity.setUsername(roadmapDTO.getUsername());
        roadmapEntity.setAchieved(roadmapDTO.getAchieved());
        roadmapEntity.setClear(roadmapDTO.isClear());
        roadmapEntity.setSessionData(roadmapDTO.getSessionData()); // JSON 문자열 저장

        roadmapRepository.save(roadmapEntity);
    }

    @Override
    public RoadmapDTO getRoadmap(Long roadmapID) {
        return roadmapRepository.findById(roadmapID)
                .map(roadmapEntity -> {
                    // Entity를 DTO로 변환
                    RoadmapDTO roadmapDTO = new RoadmapDTO();
                    roadmapDTO.setUsername(roadmapEntity.getUsername());
                    roadmapDTO.setAchieved(roadmapEntity.getAchieved());
                    roadmapDTO.setClear(roadmapEntity.isClear());
                    roadmapDTO.setSessionData(roadmapEntity.getSessionData());
                    return roadmapDTO;
                })
                .orElse(null); // 해당 ID가 없으면 null 반환
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
        roadmapDTO.setAchieved(roadmapEntity.getAchieved());
        roadmapDTO.setClear(roadmapEntity.isClear());
        roadmapDTO.setSessionData(roadmapEntity.getSessionData()); // JSON 데이터를 그대로 전달
        return roadmapDTO;
    }

}
