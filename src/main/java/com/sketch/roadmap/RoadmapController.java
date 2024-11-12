package com.sketch.roadmap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RoadmapController {

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
