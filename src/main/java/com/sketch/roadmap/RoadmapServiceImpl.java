package com.sketch.roadmap;
import com.sketch.jwt.JwtTokenProvider;
import com.sketch.user.UserEntity;
import com.sketch.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoadmapServiceImpl implements RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public RoadmapServiceImpl(RoadmapRepository roadmapRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.roadmapRepository = roadmapRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void saveRoadmap(RoadmapDTO roadmapDTO, String username) {
        // 사용자 정보 확인
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 로드맵 엔티티 생성
        RoadmapEntity roadmapEntity = new RoadmapEntity();
        roadmapEntity.setUser(user);
        roadmapEntity.setAchieved(roadmapDTO.getAchieved());
        roadmapEntity.setClear(roadmapDTO.isClear());

        // 세션 엔티티 생성 및 추가
        roadmapEntity.setSessions(
                roadmapDTO.getSessionList().stream()
                        .map(sessionDTO -> convertToEntity(sessionDTO, roadmapEntity))
                        .collect(Collectors.toList())
        );

        // 저장
        roadmapRepository.save(roadmapEntity);
    }

    @Override
    public List<RoadmapDTO> getAllRoadmaps(String username) {
        // 사용자 정보 확인
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 사용자의 모든 로드맵 조회 및 변환
        return roadmapRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRoadmap(Long roadmapId, String username) {
        RoadmapEntity roadmapEntity = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        // 사용자 권한 확인
        if (!roadmapEntity.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized access to roadmap");
        }

        // 로드맵 삭제
        roadmapRepository.delete(roadmapEntity);
    }

    @Override
    public boolean isTokenValid(String accessToken) {
        return jwtTokenProvider.validateToken(accessToken);
    }

    // 세션 DTO를 엔티티로 변환
    private SessionEntity convertToEntity(SessionDTO sessionDTO, RoadmapEntity roadmapEntity) {
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setRoadmapEntity(roadmapEntity);
        sessionEntity.setSeq(sessionDTO.getSeq());
        sessionEntity.setTopic(sessionDTO.getTopic());
        sessionEntity.setDescription(sessionDTO.getDescription());
        sessionEntity.setStartDate(sessionDTO.getStartDate());
        sessionEntity.setDeadLine(sessionDTO.getDeadLine());
        sessionEntity.setNote(sessionDTO.getNote());
        return sessionEntity;
    }

    // 로드맵 엔티티를 DTO로 변환
    private RoadmapDTO convertToDTO(RoadmapEntity roadmapEntity) {
        RoadmapDTO roadmapDTO = new RoadmapDTO();
        roadmapDTO.setRoadmapID(Math.toIntExact(roadmapEntity.getId()));
        roadmapDTO.setAchieved(roadmapEntity.getAchieved());
        roadmapDTO.setClear(roadmapEntity.isClear());
        roadmapDTO.setSessionList(
                roadmapEntity.getSessions().stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList())
        );
        return roadmapDTO;
    }

    // 세션 엔티티를 DTO로 변환
    private SessionDTO convertToDTO(SessionEntity sessionEntity) {
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setSessionId(sessionEntity.getId().toString());
        sessionDTO.setSeq(sessionEntity.getSeq());
        sessionDTO.setTopic(sessionEntity.getTopic());
        sessionDTO.setDescription(sessionEntity.getDescription());
        sessionDTO.setStartDate(sessionEntity.getStartDate());
        sessionDTO.setDeadLine(sessionEntity.getDeadLine());
        sessionDTO.setNote(sessionEntity.getNote());
        return sessionDTO;
    }

    @Override
    public String extractUsername(String accessToken) {
        return jwtTokenProvider.extractUsername(accessToken);
    }
}
