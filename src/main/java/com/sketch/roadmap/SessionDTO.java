package com.sketch.roadmap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SessionDTO {
    private RoadmapEntity roadmapEntity;
    private String sessionId;
    private int seq;
    private String topic;
    private String description;
    private String startDate;
    private String deadLine;
    private String note;
}
