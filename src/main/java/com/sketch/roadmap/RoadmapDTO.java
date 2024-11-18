package com.sketch.roadmap;

import com.sketch.user.UserEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoadmapDTO {
    private UserEntity userEntity;
    private Long roadmapId;
    private String username;
    private int achieved;
    private boolean clear;
    private String sessionData; // Flask 응답 JSON 데이터를 그대로 저장
}
