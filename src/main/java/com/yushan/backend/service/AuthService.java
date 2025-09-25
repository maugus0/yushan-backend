package com.yushan.backend.service;

import com.yushan.backend.dto.UserRegisterationDTO;
import com.yushan.backend.entity.User;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.util.MailUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailUtil mailUtil;

    /**
     * register a new user
     * @param registrationDTO
     * @return
     */
    public User register(UserRegisterationDTO registrationDTO) {
        //check if email existed
        User existingUser = userMapper.selectByEmail(registrationDTO.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("email was registered");
        }

        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(registrationDTO.getEmail());
        user.setUsername(registrationDTO.getUsername());
        user.setHashPassword(hashPassword(registrationDTO.getPassword()));
        user.setEmailVerified(true);

        if (registrationDTO.getGender() != null) {
            user.setGender(registrationDTO.getGender());
        } else {
            user.setGender(0); // default for unknown gender
        }
        if (registrationDTO.getBirthday() != null) {
            user.setBirthday(registrationDTO.getBirthday());
        } else {
            user.setBirthday(null);
        }

        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setLastLogin(new Date());
        user.setLastActive(new Date());

        // set default user profile
        user.setAvatarUrl("123"); //todo: set default avatar URL
        user.setStatus(1); // 1 for normal
        user.setIsAuthor(false);
        user.setAuthorVerified(false);
        user.setLevel(1);
        user.setExp(0f);
        user.setYuan(0f);
        user.setReadTime(0f);
        user.setReadBookNum(0);

        try {
            userMapper.insert(user);
        } catch (Exception e) {
            userMapper.deleteByPrimaryKey(user.getUuid());
            throw new RuntimeException("registered failed", e);
        }
        return user;
    }
    /**
    * login a user
    * @param email
    * @param password
    * @return
    */
    public User login(String email, String password) {
        // verify input
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }

        try {
             User user = userMapper.selectByEmail(email);
             if (user != null && BCrypt.checkpw(password, user.getHashPassword())) {
                 return user;
             }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * send verification email
     * @param email
     */
    public void sendVerificationEmail(String email) {
        try {
            mailUtil.sendVerificationEmail(email);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("failed to send verification email", e);
        }
    }

    /**
     * verify email
     * @param email
     * @param code
     * @return if verified successfully
     */
    public boolean verifyEmail(String email, String code) {
        return mailUtil.verifyEmail(email, code);
    }

    /**
     * hash password
     * @param password
     * @return
     */
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
