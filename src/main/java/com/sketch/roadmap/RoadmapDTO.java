package com.sketch.roadmap;


import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
public class RoadmapDTO {
    private String accessToken; // 저장 시 인증용
    private int profile;
    private int roadmapID;
    private int achieved;
    private boolean clear;
    private List<SessionDTO> sessionList;
}

