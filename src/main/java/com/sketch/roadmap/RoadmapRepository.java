package com.sketch.roadmap;

import com.sketch.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadmapRepository extends JpaRepository<RoadmapEntity, Long> {
    List<RoadmapEntity> findByUser(UserEntity user);
}
