package com.sketch.user;

import com.sketch.jwt.JwtTokenProvider;
import com.sketch.jwt.TokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public void registerUser(UserSaveDTO userSaveDTO) {
        UserEntity user = new UserEntity();
        user.setUsername(userSaveDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userSaveDTO.getPassword()));
        userRepository.save(user);
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
                .orElse(null); // 인증 실패 시 null 반환
    }

    @Override
    public boolean logoutUser(String accessToken) {
        // 토큰 유효성 확인
        if (jwtTokenProvider.validateToken(accessToken)) {
            Date expirationDate = jwtTokenProvider.getExpirationDate(accessToken);
            long expirationInMillis = expirationDate.getTime() - System.currentTimeMillis();

            // Redis에 토큰 블랙리스트로 저장
            redisTemplate.opsForValue().set(accessToken, "logout", expirationInMillis, TimeUnit.MILLISECONDS);
            return true;
        }
        return false;
    }
}
