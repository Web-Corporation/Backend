package com.sketch.roadmap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/roadmap")
public class RoadmapController {
    @Value("${PYTHON_SERVICE_URL}")
    private String pythonUrl;
    private final RoadmapService roadmapService;

    @Autowired
    public RoadmapController(RoadmapService roadmapService) {
        this.roadmapService = roadmapService;
    }

    @GetMapping("/createroadmap")
    public ResponseEntity<String> createRoadmap(@RequestHeader("Authorization") String accesstoken, @RequestBody TopicDTO topicDTO) {
        if (roadmapService.checkToken(accesstoken)) {
            RestTemplate restTemplate = new RestTemplate();

            // Python 서버 URL 설정
            String pythonServiceUrl = String.format("%s/createroadmap?topic=%s", pythonUrl, topicDTO.getTopic());

            // Python 서버에 요청 보내기
            String roadmap = restTemplate.getForObject(pythonServiceUrl, String.class);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accesstoken);
            return ResponseEntity.ok().headers(headers).body(roadmap);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @PostMapping("/saveroadmap")
    public void saveRoadmap() {
        // 로드맵 저장 로직을 추가하세요
    }

    @PutMapping("/modifyroadmap")
    public void modifyRoadmap() {
        // 로드맵 수정 로직을 추가하세요
    }


//    @GetMapping("roadmap/getallroadmap")
//    public RoadmapEntity getRoadmap(){
//
//    }
}
