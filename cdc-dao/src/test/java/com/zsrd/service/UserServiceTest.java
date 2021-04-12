package com.zsrd.service;

import com.zsrd.entity.User;
import com.zsrd.enums.UserSexEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {

    @Autowired
    private IUserService userService;

    @Test
    public void list() {
        List<User> users = userService.list();
        for (User user: users) {
            log.info("######:{}",user);
        }
    }

    @Test
    public void update() {
        userService.updateById(User.builder()
                .id(1L)
                .sex(UserSexEnum.WOMAN)
                .updateTime(new Date())
                .build());

        log.info("######:{}",userService.getById(1));
    }

}
