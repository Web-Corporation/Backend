package com.sketch.user;

import com.sketch.jwt.TokenInfo;

public interface UserService {
    void registerUser(UserSaveDTO userSaveDTO);
    TokenInfo loginUser(UserLoginDTO userLoginDTO);
}
