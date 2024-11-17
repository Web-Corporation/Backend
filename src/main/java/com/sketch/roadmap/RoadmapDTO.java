package com.sketch.roadmap;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoadmapDTO {
    private Long roadmapId;
    private String username;
    private String accessToken; // 저장 시 인증용
    private int profile;
    private int achieved;
    private boolean clear;
    private String sessionData; // Flask 응답 JSON 데이터를 그대로 저장
}
