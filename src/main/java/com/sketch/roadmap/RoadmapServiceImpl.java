package com.sketch.roadmap;

import com.sketch.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoadmapServiceImpl implements RoadmapService{
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean checkToken(String token){
        return jwtTokenProvider.validateToken(token);
    }
}
