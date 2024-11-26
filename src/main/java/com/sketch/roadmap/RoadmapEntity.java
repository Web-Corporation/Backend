package com.sketch.roadmap;

import com.sketch.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "roadmap_entity") // 테이블 이름을 roadmaps로 설정
public class RoadmapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    private Long roadmapId; // 로드맵 ID

    @Column(nullable = false) //로드맵 이름
    private String roadmapName;

    @ManyToOne // 다대일 관계
    @JoinColumn(name = "user_id", nullable = false) // 외래 키 설정
    private UserEntity userEntity; // 연결된 사용자 엔티티

    @Column(nullable = false) // 로드맵 진행률
    private int achieved;

    @Column(nullable = false) // 로드맵 완료 여부
    private boolean clear;

    @Lob // 대량 데이터 저장
    @Column(nullable = false, columnDefinition = "TEXT") // Flask 서버의 JSON 응답 저장
    private String sessionData;
}
