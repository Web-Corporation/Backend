package com.sketch.roadmap;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "session_table")
public class SessionEntity {
    @ManyToOne
    private RoadmapEntity roadmapEntity;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "seq")
    private int seq;

    @Column(name = "topic")
    private String topic;

    @Column(name = "description")
    private String description;

    @Column(name = "startDate")
    private String startDate;

    @Column(name = "deadLine")
    private String deadLine;

    @Column(name = "note")
    private String note;

    public static SessionEntity SessionDTO2Entity(SessionDTO sessionDTO){
        SessionEntity sessionEntity = new SessionEntity();

        sessionEntity.setSessionId(sessionDTO.getSessionId());
        sessionEntity.setSeq(sessionDTO.getSeq());
        sessionEntity.setTopic(sessionDTO.getTopic());
        sessionEntity.setDescription(sessionDTO.getDescription());
        sessionEntity.setStartDate(sessionDTO.getStartDate());
        sessionEntity.setDeadLine(sessionDTO.getDeadLine());
        sessionEntity.setNote(sessionDTO.getNote());

        return sessionEntity;
    }
}
