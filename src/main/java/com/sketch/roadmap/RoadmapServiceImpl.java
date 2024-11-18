package com.sketch.roadmap;
import com.sketch.user.UserEntity;
import com.sketch.user.UserRepository;
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
    private final UserRepository userRepository;
    private final RoadmapRepository roadmapRepository;
    private final RestTemplate restTemplate;

    @Value("${PYTHON_SERVICE_URL}")
    private String flaskServiceUrl;

    @Autowired
    public RoadmapServiceImpl(UserRepository userRepository, RoadmapRepository roadmapRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.roadmapRepository = roadmapRepository;
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
        // 사용자 엔티티 가져오기
        UserEntity userEntity = userRepository.findByUsername(roadmapDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // DTO -> Entity 변환
        RoadmapEntity roadmapEntity = new RoadmapEntity();
        roadmapEntity.setUserEntity(userEntity); // 연관관계 설정
        roadmapEntity.setUsername(roadmapDTO.getUsername());
        roadmapEntity.setAchieved(roadmapDTO.getAchieved());
        roadmapEntity.setClear(roadmapDTO.isClear());
        roadmapEntity.setSessionData(roadmapDTO.getSessionData());

        roadmapRepository.save(roadmapEntity);
    }

    @Override
    public RoadmapDTO getRoadmap(Long roadmapID) {
        return roadmapRepository.findById(roadmapID)
                .map(this::convertToDTO)
                .orElse(null);
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
        if (!roadmapEntity.getUserEntity().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized access to roadmap");
        }

        roadmapRepository.delete(roadmapEntity);
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
