package com.sketch.user;

import com.sketch.jwt.JwtTokenProvider;
import com.sketch.jwt.TokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public TokenInfo loginUser(UserLoginDTO userLoginDTO) {
        return userRepository.findByUsername(userLoginDTO.getUsername())
                .filter(user -> passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword()))
                .map(user -> {
                    String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername());
                    String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

                    return TokenInfo.builder()
                            .grantType("Bearer")
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build();
                })
                .orElse(null);
    }

    @Override
    public boolean logoutUser(String accessToken) {
        // 토큰 유효성 확인
        if (jwtTokenProvider.validateToken(accessToken)) {
            // 토큰 만료 시간 계산
            Date expirationDate = jwtTokenProvider.getExpirationDate(accessToken);
            long expirationInMillis = expirationDate.getTime() - System.currentTimeMillis();

            // Redis에 토큰 블랙리스트로 저장
            redisTemplate.opsForValue().set(accessToken, "logout", expirationInMillis, TimeUnit.MILLISECONDS);
            return true;
        }
        return false;
    }

    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }
}
