package com.sketch.roadmap;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "roadmap_id", nullable = false)
    private RoadmapEntity roadmapEntity; // 연관된 로드맵

    @Column(nullable = false)
    private int seq;                // 순서 번호

    @Column(nullable = false)
    private String topic;           // 세션 주제

    @Column(nullable = false)
    private String description;     // 세션 설명

    @Column(nullable = false)
    private String startDate;       // 시작 날짜 (YYYY-MM-DD 형식)

    @Column(nullable = false)
    private String deadLine;        // 마감 날짜 (YYYY-MM-DD 형식)

    @Column
    private String note;            // 추가 메모
}
