package com.sketch.user;

import com.sketch.roadmap.RoadmapEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "user_entity") // 테이블 이름을 users로 설정
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    private Long id;

    @Column(nullable = false, unique = true) // username은 중복 불가
    private String username;

    @Column(nullable = false) // 비밀번호는 null 불가
    private String password;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true) // Roadmap과 연관관계
    private List<RoadmapEntity> roadmaps;
}
