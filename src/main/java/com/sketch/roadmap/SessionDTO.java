package com.sketch.roadmap;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SessionDTO {
    private String sessionId;
    private int seq;
    private String topic;
    private String description;
    private String startDate;
    private String deadLine;
    private String note;
}
