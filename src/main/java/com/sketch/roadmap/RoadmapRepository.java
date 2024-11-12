package com.sketch.roadmap;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapRepository extends JpaRepository<RoadmapEntity, Long> {
    // Additional query methods can be added here if needed
}
