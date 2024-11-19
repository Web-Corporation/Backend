package com.sketch.user;

import com.sketch.TokenBlacklistService;
import com.sketch.jwt.JwtTokenProvider;
import com.sketch.jwt.TokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider, TokenBlacklistService tokenBlacklistService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserSaveDTO userSaveDTO) {
        try {
            userService.registerUser(userSaveDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<TokenInfo> loginUser(@RequestBody UserLoginDTO userLoginDTO) {
        TokenInfo tokenInfo = userService.loginUser(userLoginDTO);
        if (tokenInfo != null) {
            return ResponseEntity.ok(tokenInfo);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String accessToken) {
        try {
            String token = accessToken.replace("Bearer ", "");

            // 유효성 검사
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            long remainingTime = jwtTokenProvider.getExpiration(token);
            tokenBlacklistService.blacklistToken(token, remainingTime);
            return ResponseEntity.ok("Logged out successfully");

        } catch (IllegalArgumentException e) {
            // validateToken에서 발생할 수 있는 예외 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        } catch (Exception e) {
            // 기타 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during logout");
        }
    }
}
