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
    @Column(name = "roadmap_id")
    private Long roadmapId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Column(name = "achieved")
    private int achieved;

    @Column(name = "clear")
    private boolean clear;

    // One-to-Many relationship with SessionEntity
    @OneToMany(mappedBy = "roadmapEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionEntity> roadmap; // roadmap -> SessionEntity 리스트

    public RoadmapEntity(UserEntity userEntity, int achieved, boolean clear) {
        this.userEntity = userEntity;
        this.achieved = achieved;
        this.clear = clear;
    }
}
