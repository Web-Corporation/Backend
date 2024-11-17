package com.sketch.roadmap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/roadmap")
public class RoadmapController {

    private final RoadmapService roadmapService;

    @Value("${PYTHON_SERVICE_URL}") // Flask 서버 URL
    private String flaskServiceUrl;

    @Autowired
    public RoadmapController(RoadmapService roadmapService) {
        this.roadmapService = roadmapService;
    }

    @GetMapping("/createroadmap")
    public ResponseEntity<String> createRoadmap(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam String topic) {
        // 1. 토큰 유효성 검사
        if (!roadmapService.isTokenValid(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        // 2. Flask 서비스로 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        String flaskUrl = flaskServiceUrl + "/createroadmap?topic=" + topic;

        try {
            String roadmapResponse = restTemplate.getForObject(flaskUrl, String.class);
            return ResponseEntity.ok(roadmapResponse); // Flask의 결과를 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate roadmap: " + e.getMessage());
        }
    }


    @PostMapping("/save")
    public ResponseEntity<String> saveRoadmap(@RequestBody RoadmapDTO roadmapDTO) {
        // 1. 토큰 유효성 검사
        if (!roadmapService.isTokenValid(roadmapDTO.getAccessToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        // 2. 토큰에서 사용자 이름 추출
        String username = roadmapService.extractUsername(roadmapDTO.getAccessToken());

        // 3. 로드맵 저장
        roadmapService.saveRoadmap(roadmapDTO, username);

        return ResponseEntity.status(HttpStatus.CREATED).body("Roadmap saved successfully");
    }

    @GetMapping("/list")
    public ResponseEntity<List<RoadmapDTO>> getAllRoadmaps(@RequestHeader("Authorization") String accessToken) {
        // 토큰 검증
        if (!roadmapService.isTokenValid(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 사용자 이름 추출
        String username = roadmapService.extractUsername(accessToken);

        // 로드맵 조회
        List<RoadmapDTO> roadmaps = roadmapService.getAllRoadmaps(username);
        return ResponseEntity.ok(roadmaps);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateRoadmap(@RequestBody RoadmapDTO roadmapDTO) {
        // 토큰 검증
        if (!roadmapService.isTokenValid(roadmapDTO.getAccessToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        // 사용자 이름 추출
        String username = roadmapService.extractUsername(roadmapDTO.getAccessToken());

        // 로드맵 업데이트 로직 (로드맵 저장과 동일한 로직 사용 가능)
        roadmapService.saveRoadmap(roadmapDTO, username);

        return ResponseEntity.ok("Roadmap updated successfully");
    }

    @DeleteMapping("/delete/{roadmapId}")
    public ResponseEntity<String> deleteRoadmap(
            @PathVariable("roadmapId") Long roadmapId,
            @RequestHeader("Authorization") String accessToken
    ) {
        // 토큰 검증
        if (!roadmapService.isTokenValid(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        // 사용자 이름 추출
        String username = roadmapService.extractUsername(accessToken);

        // 로드맵 삭제 로직
        roadmapService.deleteRoadmap(roadmapId, username);

        return ResponseEntity.ok("Roadmap deleted successfully");
    }
}
