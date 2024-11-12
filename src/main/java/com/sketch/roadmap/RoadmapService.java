package com.sketch.roadmap;

import com.sketch.jwt.TokenInfo;
import com.sketch.user.UserLoginDTO;

public interface RoadmapService {
    boolean checkToken(String token);
}
