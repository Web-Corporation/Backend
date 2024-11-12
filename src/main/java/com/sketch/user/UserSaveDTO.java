package com.sketch.user;

import com.sketch.roadmap.SessionEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserSaveDTO {

    private String username;
    private String password;
}
