package com.sketch.roadmap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class RoadmapController {
    @Value("${openai.key}")
    String openai_apikey;

    @GetMapping("/createroadmap")
    public String createRoadmap(@RequestBody TopicDTO topicDTO) {
        String key = this.openai_apikey;
        RestTemplate restTemplate = new RestTemplate();
        String pythonServiceUrl = "http://python-service:8080/run-script?topic="+topicDTO.getTopic();
        return restTemplate.getForObject(pythonServiceUrl, String.class);
    }

    @GetMapping()
    public void saveRoadmap(){
        return;
    }

    @GetMapping()
    public void modifyRoadmap(){
        return;
    }

//    @GetMapping("roadmap/getallroadmap")
//    public RoadmapEntity getRoadmap(){
//
//    }
}
