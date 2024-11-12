package com.sketch.roadmap;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {
    SessionEntity findById(String sessionId);
}
