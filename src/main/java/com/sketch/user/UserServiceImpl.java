package com.sketch.user;

import com.sketch.jwt.JwtTokenProvider;
import com.sketch.jwt.TokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void registerUser(UserSaveDTO userSaveDTO) {
        // UserEntity 생성 및 저장 (로드맵은 초기 생성 시 추가하지 않음)
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
                    // Access Token과 Refresh Token 생성
                    String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername());
                    String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

                    // TokenInfo 객체 생성 후 반환
                    return TokenInfo.builder()
                            .grantType("Bearer")
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build();
                })
                .orElse(null); // 인증 실패 시 null 반환
    }

    @Override
    public boolean logoutUser(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}
