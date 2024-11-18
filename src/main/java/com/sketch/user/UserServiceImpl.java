package com.sketch.user;

import com.sketch.jwt.JwtTokenProvider;
import com.sketch.jwt.TokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Primary
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void registerUser(UserSaveDTO userSaveDTO) {
        if (userRepository.findByUsername(userSaveDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        UserEntity user = new UserEntity();
        user.setUsername(userSaveDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userSaveDTO.getPassword()));
        userRepository.save(user);
    }

    @Override
    public TokenInfo loginUser(UserLoginDTO userLoginDTO) {
        return userRepository.findByUsername(userLoginDTO.getUsername())
                .filter(user -> {
                    boolean matches = passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword());
                    if (!matches) {
                        System.out.println("Password does not match for user: " + userLoginDTO.getUsername());
                    }
                    return matches;
                })
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
        // JWT가 유효한지만 확인
        return jwtTokenProvider.validateToken(accessToken);
    }
}
