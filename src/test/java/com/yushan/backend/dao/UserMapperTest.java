package com.yushan.backend.dao;

import com.yushan.backend.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testInsert() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setAvatarUrl("https://example.com/avatar.png");
        user.setStatus(1);
        user.setEmailVerified(true);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUuid("123e4567-e89b-12d3-a456-426614174000");
        user.setUsername("testuser");
        user.setHashPassword("hashed_password");
        user.setProfileDetail("Test profile");
        user.setIsAuthor(false);
        user.setAuthorVerified(false);
        user.setReadTime(10.5f);
        user.setReadBookNum(5);
        user.setLevel(1);
        user.setBirthday(new Date());
        user.setGender(1);
        user.setPoint(100.0f);
        user.setExp(50);

        int result = userMapper.insert(user);

        Assertions.assertEquals(1, result, "Insert method should return 1");
    }

    @Test
    public void testInsertSelective() {
        User user = new User();
        user.setUuid("123e4567-e89b-12d3-a456-426614174001-testchange");

        int result = userMapper.insertSelective(user);

        Assertions.assertEquals(1, result, "InsertSelective method should return 1");
    }
}