package com.sketch.user;

import com.sketch.jwt.TokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserSaveDTO userSaveDTO) {
        userService.registerUser(userSaveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenInfo> loginUser(@RequestBody UserLoginDTO userLoginDTO) {
        TokenInfo tokenInfo = userService.loginUser(userLoginDTO);
        if (tokenInfo != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + tokenInfo.getAccessToken());
            headers.add("Refresh-Token", tokenInfo.getRefreshToken());
            return ResponseEntity.ok().headers(headers).body(tokenInfo);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        if (userService.logoutUser(jwtToken)) {
            return ResponseEntity.ok("Logout successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
