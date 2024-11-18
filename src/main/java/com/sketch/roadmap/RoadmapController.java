package com.sketch.roadmap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roadmap")
public class RoadmapController {

    private final RoadmapService roadmapService;

    @Autowired
    public RoadmapController(RoadmapService roadmapService) {
        this.roadmapService = roadmapService;
    }

    @GetMapping("/createroadmap")
    public ResponseEntity<String> createRoadmap(@RequestHeader("Authorization") String accessToken,
                                                @RequestParam String topic) {
        try {
            String roadmap = roadmapService.createRoadmap(topic, accessToken);
            return ResponseEntity.ok(roadmap);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //로드맵 저장
    @PostMapping("/saveroadmap")
    public ResponseEntity<String> saveRoadmap(@RequestBody RoadmapDTO roadmapDTO) {
        String username = getCurrentUsername(); // 현재 사용자 정보 추출
        roadmapDTO.setUsername(username);
        roadmapService.saveRoadmap(roadmapDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Roadmap saved successfully");
    }

    //로드맵 단일 조회
    @GetMapping("/getroadmap")
    public ResponseEntity<RoadmapDTO> getRoadmap(@RequestParam("roadmapID") Long roadmapID) {
        RoadmapDTO roadmap = roadmapService.getRoadmap(roadmapID);
        return roadmap != null
                ? ResponseEntity.ok(roadmap)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    //사용자 전체 로드맵 조회
    @GetMapping("/getallroadmap")
    public ResponseEntity<List<RoadmapDTO>> getAllRoadmaps() {
        String username = getCurrentUsername(); // 현재 사용자 정보 추출
        List<RoadmapDTO> roadmaps = roadmapService.getAllRoadmaps(username);
        return ResponseEntity.ok(roadmaps);
    }

    //로드맵 수정
    @PutMapping("/updateroadmap")
    public ResponseEntity<String> updateRoadmap(@RequestBody RoadmapDTO roadmapDTO) {
        roadmapService.saveRoadmap(roadmapDTO);
        return ResponseEntity.ok("Roadmap updated successfully");
    }


     //로드맵 삭제
    @DeleteMapping("/delete/{roadmapId}")
    public ResponseEntity<String> deleteRoadmap(@PathVariable("roadmapId") Long roadmapId) {
        String username = getCurrentUsername(); // 현재 사용자 정보 추출
        roadmapService.deleteRoadmap(roadmapId, username);
        return ResponseEntity.ok("Roadmap deleted successfully");
    }

     //현재 사용자 이름 추출
    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
