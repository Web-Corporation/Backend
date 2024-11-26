package com.sketch.roadmap;

import com.sketch.user.UserEntity;
import com.sketch.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class RoadmapServiceImpl implements RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${PYTHON_SERVICE_URL}")
    private String flaskServiceUrl;

    @Autowired
    public RoadmapServiceImpl(RoadmapRepository roadmapRepository,
                              UserRepository userRepository,
                              RestTemplate restTemplate) {
        this.roadmapRepository = roadmapRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public RoadmapDTO createRoadmap(String topic, String accessToken) {
        String flaskUrl = flaskServiceUrl + "/createroadmap?topic=" + topic;
        try {
            String flaskResponse = restTemplate.getForObject(flaskUrl, String.class);
            RoadmapDTO roadmapDTO = new RoadmapDTO();
            roadmapDTO.setRoadmapName(topic);
            roadmapDTO.setSessionData(flaskResponse);
            roadmapDTO.setAchieved(0);
            roadmapDTO.setClear(false);
            return roadmapDTO;
        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with Flask service: " + e.getMessage());
        }
    }

    @Override
    public void saveRoadmap(RoadmapDTO roadmapDTO, String username) {
        // 데이터베이스에서 사용자 조회
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RoadmapEntity roadmapEntity = new RoadmapEntity();
        roadmapEntity.setRoadmapName(roadmapDTO.getRoadmapName());
        roadmapEntity.setAchieved(roadmapDTO.getAchieved());
        roadmapEntity.setClear(roadmapDTO.isClear());
        roadmapEntity.setSessionData(roadmapDTO.getSessionData());
        roadmapEntity.setUserEntity(userEntity); // 조회한 UserEntity 설정

        roadmapRepository.save(roadmapEntity);
    }

    // 로드맵 수정
    @Override
    public void updateRoadmap(RoadmapDTO roadmapDTO, String username) {
        RoadmapEntity roadmapEntity = roadmapRepository.findById(roadmapDTO.getRoadmapId())
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        if (!roadmapEntity.getUserEntity().getUsername().equals(username)) {
            throw new AccessDeniedException("Unauthorized access to roadmap");
        }
        roadmapEntity.setRoadmapName(roadmapDTO.getRoadmapName());
        roadmapEntity.setAchieved(roadmapDTO.getAchieved());
        roadmapEntity.setClear(roadmapDTO.isClear());
        roadmapEntity.setSessionData(roadmapDTO.getSessionData());

        roadmapRepository.save(roadmapEntity);
    }

    @Override
    public List<RoadmapDTO> getAllRoadmaps(String username) {
        return roadmapRepository.findByUserEntityUsername(username).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoadmapDTO getRoadmap(Long roadmapID) {
        return roadmapRepository.findById(roadmapID)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));
    }

    @Override
    public void deleteRoadmap(Long roadmapId, String username) {
        RoadmapEntity roadmapEntity = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        if (!roadmapEntity.getUserEntity().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized access to roadmap");
        }

        roadmapRepository.delete(roadmapEntity);
    }

    private RoadmapDTO convertToDTO(RoadmapEntity roadmapEntity) {
        RoadmapDTO roadmapDTO = new RoadmapDTO();
        roadmapDTO.setRoadmapName(roadmapEntity.getRoadmapName());
        roadmapDTO.setAchieved(roadmapEntity.getAchieved());
        roadmapDTO.setClear(roadmapEntity.isClear());
        roadmapDTO.setSessionData(roadmapEntity.getSessionData());
        roadmapDTO.setRoadmapId(roadmapEntity.getRoadmapId());
        return roadmapDTO;
    }
}
