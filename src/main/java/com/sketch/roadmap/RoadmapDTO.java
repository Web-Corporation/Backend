package com.sketch.roadmap;


import com.sketch.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoadmapDTO {
    private UserEntity userEntity;
    private String roadmapId;
    private int achieved;
    private boolean clear;
    private List<SessionEntity> roadmap;
}
