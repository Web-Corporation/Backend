package com.sketch;

import com.sketch.jwt.JwtTokenProvider;
import com.sketch.roadmap.RoadmapService;
import com.sketch.roadmap.RoadmapServiceImpl;
import com.sketch.user.UserRepository;
import com.sketch.user.UserService;
import com.sketch.user.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public AppConfig(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserRepository UserRepository(){
        return null;
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userRepository, passwordEncoder(), jwtTokenProvider, redisTemplate);
    }

    @Bean
    public RoadmapService roadmapService() {
        return new RoadmapServiceImpl(jwtTokenProvider);
    }

    
}
