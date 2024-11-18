package com.sketch.roadmap;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadmapRepository extends JpaRepository<RoadmapEntity, Long> {
    List<RoadmapEntity> findByUsername(String username);
}

