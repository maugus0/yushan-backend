package com.yushan.backend.service;

import com.yushan.backend.util.MailUtil;
import com.yushan.backend.util.RedisUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class MailService {

    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private RedisUtil redisUtil;

    // email rate limit
    private static final int LIMIT_TIME = 60000;

    private final SecureRandom secureRandom = new SecureRandom();
    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRE_MINUTES = 5;

    /**
     * send vverification email, limit_time 60s
     * @param email
     */
    public void sendVerificationCode(String email) {
        // check rate limit
        checkEmailRateLimit(email);
        String verificationCode = generateSecureCode();
        // content
        String subject = "Verify Your Code in Yushan";
        String content = String.format("Your code is: %s, valid for 5 minutes.", verificationCode);

        redisUtil.set(email, verificationCode, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        try {
            mailUtil.sendEmail(email, subject, content);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("failed to send verification email", e);
        }

        recordEmailSendTime(email);
    }

    /**
     * check email rate limit
     * @param email
     */
    private void checkEmailRateLimit(String email) {
        String key = "email_send_time:" + email;
        String lastSendTimeStr = redisUtil.get(key);

        if (lastSendTimeStr != null) {
            long lastSendTime = Long.parseLong(lastSendTimeStr);
            long currentTime = System.currentTimeMillis();

            // limit_time: 60s
            if (currentTime - lastSendTime < LIMIT_TIME) {
                long remainingTime = (LIMIT_TIME - (currentTime - lastSendTime)) / 1000;
                throw new RuntimeException("email sends too often, please try again after " + remainingTime + " second(s)");
            }
        }
    }

    /**
     * record email send time
     * @param email
     */
    private void recordEmailSendTime(String email) {
        String key = "email_send_time:" + email;
        redisUtil.set(key, String.valueOf(System.currentTimeMillis()), LIMIT_TIME, TimeUnit.MILLISECONDS);
    }

    /**
     * verify email
     * @param email, code
     * @param code
     * @return if right return true, else return false
     */
    public boolean verifyEmail(String email, String code) {
        if (redisUtil.hasKey(email)) {
            String storedCode = redisUtil.get(email);
            boolean isValid = code.equals(storedCode);
            // delete code
            if (isValid) {
                redisUtil.delete(email);
            }
            return isValid;
        }
        else {
            return false;
        }
    }

    /**
     * Generate a secure verification code
     * @return a string representation of the verification code
     */
   protected String generateSecureCode() {
        int bound = (int) Math.pow(10, CODE_LENGTH);
        int codeNum = secureRandom.nextInt(bound);
        return String.format("%0" + CODE_LENGTH + "d", codeNum);
    }
}