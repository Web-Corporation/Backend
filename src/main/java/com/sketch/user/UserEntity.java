package com.sketch.user;

import com.sketch.roadmap.RoadmapEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String username;
    private String password;

    // One-to-Many 관계 설정, 초기 회원가입 시에는 빈 상태
    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoadmapEntity> roadmaps;

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

