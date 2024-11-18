package com.sketch.roadmap;

import com.sketch.user.UserEntity;
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

    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    private UserEntity userEntity;

    private String username; // 로드맵과 연결된 사용자 이름

    private int achieved;

    private boolean clear;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Lob
    private String sessionData; // Flask 서버의 JSON 응답을 문자열로 저장
}

