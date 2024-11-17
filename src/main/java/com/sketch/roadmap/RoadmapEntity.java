package com.sketch.roadmap;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class RoadmapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roadmapId;

    private String username; // 로드맵과 연결된 사용자 이름
    private String accessToken; // 인증용 토큰
    private int profile;
    private int achieved;
    private boolean clear;

    @Lob
    private String sessionData; // Flask 서버의 JSON 응답을 문자열로 저장
}

