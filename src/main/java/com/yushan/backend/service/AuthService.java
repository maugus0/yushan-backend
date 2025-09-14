package com.yushan.backend.service;

import com.yushan.backend.dto.UserRegisterationDTO;
import com.yushan.backend.entity.User;
import com.yushan.backend.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    public User register(UserRegisterationDTO registrationDTO) {
    // check if email existed
    if (userMapper.selectByEmail(registrationDTO.getEmail()) != null) {
        throw new RuntimeException("email was registered");
    }

    User user = new User();
    user.setUuid(UUID.randomUUID().toString());
    user.setEmail(registrationDTO.getEmail());
    user.setUsername(registrationDTO.getUsername());
    user.setHashPassword(hashPassword(registrationDTO.getPassword()));
    user.setEmailVerified(false);
    user.setCreateDate(new Date());
    user.setUpdateTime(new Date());
    user.setStatus(1); // 1 for normal user

    // set default user profile
    user.setLevel(1);
    user.setExp(0f);
    user.setYuan(0f);
    user.setReadTime(0f);
    user.setReadBookNum(0);

    try {
        userMapper.insert(user);
        verifyEmail(user.getEmail());
    } catch (Exception e) {
        // if email send failed, delete the user
        userMapper.deleteByPrimaryKey(user.getUuid());
        throw new RuntimeException("registered failed", e);
    }

    return user;
}


    public User login(String email, String password) {
        // 输入验证
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }

        try {
            // 使用参数化查询防止SQL注入
            // User user = userRepository.findByEmail(email); // 假设使用ORM或DAO模式

            // 实际项目中应该通过email查询用户，然后比对密码哈希值
            // if (user != null && BCrypt.checkpw(password, user.getHashedPassword())) {
            //     return user;
            // }

            // 这里假设密码存储使用BCrypt等安全哈希算法
            // 在实际实现中，应该使用参数化查询防止SQL注入

            return null; // 需要根据实际数据库查询实现
        } catch (Exception e) {
            // 记录安全相关异常但不暴露具体错误信息
            // logger.warn("Login attempt failed for email: " + email);
            return null;
        }
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private void verifyEmail(String email) {
    }
}
