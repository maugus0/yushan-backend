package com.yushan.backend.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Component
public class MailUtil {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private RedisUtil redisUtil;

    private final SecureRandom secureRandom = new SecureRandom();
    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRE_MINUTES = 5;

    @Value("${MAIL_USERNAME:1784304095@qq.com}")
    private String EMAIL_FROM;

    /**
     * send verification code email
     * @param email
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public void sendVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            String code = generateSecureCode();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);

            helper.setFrom(EMAIL_FROM, "Yushan");
            helper.setTo(email);
            helper.setSubject("Yushan Verification Code");
            helper.setText("Dear user, your verification code is: " + code, false);

            redisUtil.set(email, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            javaMailSender.send(message);
        } catch (MessagingException | RuntimeException e) {
            throw new MessagingException("Failed to send verification email: " + e.getMessage(), e);
        }
    }

    /**
     * verify email
     * @param email, code
     * @param code
     * @return if right return true, else return false
     */
    public boolean verifyEmail(String email, String code) {
        try {
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
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generate a secure verification code
     * @return a string representation of the verification code
     */
    private String generateSecureCode() {
        int bound = (int) Math.pow(10, CODE_LENGTH);
        int codeNum = secureRandom.nextInt(bound);
        return String.format("%0" + CODE_LENGTH + "d", codeNum);
    }
}
