package com.sketch.roadmap;

import com.sketch.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roadmap")
public class RoadmapController {

    private final RoadmapService roadmapService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public RoadmapController(RoadmapService roadmapService, JwtTokenProvider jwtTokenProvider) {
        this.roadmapService = roadmapService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/createroadmap")
    public ResponseEntity<RoadmapDTO> createRoadmap(@RequestHeader("Authorization") String accessToken,
                                                    @RequestParam String topic) {
        try {
            RoadmapDTO roadmapDTO = roadmapService.createRoadmap(topic, accessToken);
            return ResponseEntity.ok(roadmapDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/saveroadmap")
    public ResponseEntity<String> saveRoadmap(@RequestBody RoadmapDTO roadmapDTO,
                                              @RequestHeader("Authorization") String accessToken) {
        try {
            String username = jwtTokenProvider.extractUsername(accessToken.replace("Bearer ", ""));
            roadmapService.saveRoadmap(roadmapDTO, username);
            return ResponseEntity.status(HttpStatus.CREATED).body("Roadmap saved successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getallroadmaps")
    public ResponseEntity<List<RoadmapDTO>> getAllRoadmaps(@RequestHeader("Authorization") String accessToken) {
        try {
            String username = jwtTokenProvider.extractUsername(accessToken.replace("Bearer ", ""));
            List<RoadmapDTO> roadmaps = roadmapService.getAllRoadmaps(username);
            return ResponseEntity.ok(roadmaps);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getroadmap")
    public ResponseEntity<RoadmapDTO> getRoadmap(@RequestParam("roadmapID") Long roadmapID) {
        try {
            RoadmapDTO roadmapDTO = roadmapService.getRoadmap(roadmapID);
            return ResponseEntity.ok(roadmapDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/delete/{roadmapId}")
    public ResponseEntity<String> deleteRoadmap(@PathVariable("roadmapId") Long roadmapId,
                                                @RequestHeader("Authorization") String accessToken) {
        try {
            String username = jwtTokenProvider.extractUsername(accessToken.replace("Bearer ", ""));
            roadmapService.deleteRoadmap(roadmapId, username);
            return ResponseEntity.ok("Roadmap deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
