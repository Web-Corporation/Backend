package com.sketch.roadmap;

import com.sketch.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RoadmapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 로드맵 고유 ID

    @Column(nullable = false)
    private int achieved; // 달성률

    @Column(nullable = false)
    private boolean clear; // 완료 여부

    @OneToMany(mappedBy = "roadmapEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionEntity> sessions; // 세션 목록

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; // 로드맵을 소유한 사용자

    @Column(nullable = false)
    private String title; // 로드맵 제목
}
