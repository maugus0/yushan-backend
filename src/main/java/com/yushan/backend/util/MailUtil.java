package com.yushan.backend.util;

import com.yushan.backend.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

@Component
public class MailUtil {
    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * send verification code email
     * @param user
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public void sendVerificationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        SecureRandom secureRandom = new SecureRandom();

        MimeMessage message = javaMailSender.createMimeMessage();
        String code = String.format("%06d", secureRandom.nextInt(1000000));
        MimeMessageHelper helper = new MimeMessageHelper(message, false);

        helper.setFrom("1784304095@qq.com", "Yushan");
        helper.setTo(user.getEmail());
        // title
        helper.setSubject("Code");
        helper.setText("dear " + user.getUsername() + ", your code is: " + code, false);

        javaMailSender.send(message);

        //todo: code should save in redis
    }

    //todo verify email
    public void verifyEmail() {
    }
}
