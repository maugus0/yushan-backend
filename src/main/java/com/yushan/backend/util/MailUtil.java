package com.yushan.backend.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class MailUtil {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String EMAIL_FROM;

    /**
     * send an email
     * @param to
     * @param subject
     * @param content
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public void sendEmail(String to, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(EMAIL_FROM, "Yushan");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);
        } catch (MessagingException | RuntimeException e) {
            throw new MessagingException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
