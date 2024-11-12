//package com.sketch.userTest;
//
//import com.sketch.user.*;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@SpringBootTest
//public class UserServiceTest {
//    @Qualifier("userServiceImpl")
//    @Autowired
//    private UserService us;
//
//    @Test
//    @DisplayName("정상적으로 회원가입 확인하기")
//    void userSaveTest() {
//        UserSaveDTO userSaveDTO = new UserSaveDTO("name","id","password");
//        UserEntity userEntity = UserEntity.UserDTO2Entity(userSaveDTO);
//
//        System.out.println(userEntity.getId());
//        System.out.println(userEntity.getUsername());
//        System.out.println(userEntity.getPassword());
//
//        String id = us.save(userSaveDTO);
//        System.out.println(id);
//    }
//
//    @Test
//    @Transactional
//    @Rollback
//    @DisplayName("로그인 테스트")
//    void userLoginTest() {
//        UserSaveDTO userSaveDTO = new UserSaveDTO("name","id","password");
//        us.save(userSaveDTO);
//
//        UserLoginDTO userLoginDTO = new UserLoginDTO("id","password");
//        boolean loginResult = us.login(userLoginDTO);
//
//        assertThat(loginResult).isEqualTo(true);
//    }
//}
