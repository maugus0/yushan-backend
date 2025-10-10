package com.yushan.backend.service;

import com.yushan.backend.util.MailUtil;
import com.yushan.backend.util.RedisUtil;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    @Mock
    private MailUtil mailUtil;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private MailService mailService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String VERIFICATION_CODE = "123456";
    private static final String SUBJECT = "Verify Your Code in Yushan";

    @BeforeEach
    void setUp() {
    }

    /**
     * normal case
     */
    @Test
    void sendVerificationCode_NormalCase_ShouldSendEmailSuccessfully() throws MessagingException, UnsupportedEncodingException {
        // Given
        when(redisUtil.get("email_send_time:" + TEST_EMAIL)).thenReturn(null); // send firstly
        doNothing().when(redisUtil).set(anyString(), anyString(), anyLong(), any());
        doNothing().when(mailUtil).sendEmail(anyString(), anyString(), anyString());

        // When & Then
        assertDoesNotThrow(() -> mailService.sendVerificationCode(TEST_EMAIL));

        // Verify
        verify(redisUtil).set(eq(TEST_EMAIL), anyString(), eq(5L), any());
        verify(redisUtil).set(eq("email_send_time:" + TEST_EMAIL), anyString(), eq(60000L), any());
        verify(mailUtil).sendEmail(eq(TEST_EMAIL), eq(SUBJECT), anyString());
    }

    /**
     * rate limit
     * Expected: throw RuntimeException
     */
    @Test
    void sendVerificationCode_RateLimitExceeded_ShouldThrowException() throws MessagingException, UnsupportedEncodingException {
        // Given
        long currentTime = System.currentTimeMillis();
        long lastSendTime = currentTime - 30000; // sent before 30s
        when(redisUtil.get("email_send_time:" + TEST_EMAIL)).thenReturn(String.valueOf(lastSendTime));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mailService.sendVerificationCode(TEST_EMAIL));

        // Verify
        assertTrue(exception.getMessage().contains("email sends too often"));
        verify(redisUtil, never()).set(anyString(), anyString(), anyInt(), any());
        verify(mailUtil, never()).sendEmail(anyString(), anyString(), anyString());
    }

    /**
     * email sent failed
     * Expected: throw RuntimeException
     */
    @Test
    void sendVerificationCode_EmailSendFailed_ShouldThrowException() throws MessagingException, UnsupportedEncodingException {
        // Given
        when(redisUtil.get("email_send_time:" + TEST_EMAIL)).thenReturn(null);
        doNothing().when(redisUtil).set(anyString(), anyString(), anyLong(), any());
        doThrow(new MessagingException("Failed to send email")).when(mailUtil).sendEmail(anyString(), anyString(), anyString());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mailService.sendVerificationCode(TEST_EMAIL));

        // Verify
        assertTrue(exception.getMessage().contains("failed to send verification email"));
        verify(redisUtil).set(eq(TEST_EMAIL), anyString(), eq(5L), any());
        // Email sent failed should not set email_send_time
        verify(redisUtil, never()).set(eq("email_send_time:" + TEST_EMAIL), anyString(), anyLong(), any());    }

    /**
     * send email
     */
    @Test
    void checkEmailRateLimit_FirstTimeSend_ShouldNotThrowException() {
        // Given
        when(redisUtil.get("email_send_time:" + TEST_EMAIL)).thenReturn(null);

        // When & Then
        assertDoesNotThrow(() -> mailService.sendVerificationCode(TEST_EMAIL));
    }

    /**
     * send time < 60s
     * Expected: throw RuntimeException
     */
    @Test
    void checkEmailRateLimit_WithinLimitTime_ShouldThrowException() {
        // Given
        long currentTime = System.currentTimeMillis();
        long lastSendTime = currentTime - 30000; // sent before 30s
        when(redisUtil.get("email_send_time:" + TEST_EMAIL)).thenReturn(String.valueOf(lastSendTime));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mailService.sendVerificationCode(TEST_EMAIL));

        // Verify
        assertTrue(exception.getMessage().contains("email sends too often"));
    }

    /**
     * send time > 60s
     */
    @Test
    void checkEmailRateLimit_ExceedLimitTime_ShouldNotThrowException() throws MessagingException, UnsupportedEncodingException {
        // Given
        long currentTime = System.currentTimeMillis();
        long lastSendTime = currentTime - 70000; // sent before 70s
        when(redisUtil.get("email_send_time:" + TEST_EMAIL)).thenReturn(String.valueOf(lastSendTime));
        doNothing().when(redisUtil).set(anyString(), anyString(), anyLong(), any());
        doNothing().when(mailUtil).sendEmail(anyString(), anyString(), anyString());

        // When & Then
        assertDoesNotThrow(() -> mailService.sendVerificationCode(TEST_EMAIL));
    }

    /**
     * record send time
     * Expected: RedisUtil.set works
     */
    @Test
    void recordEmailSendTime_NormalCase_ShouldCallRedisSet() throws MessagingException, UnsupportedEncodingException {
        // Given
        when(redisUtil.get("email_send_time:" + TEST_EMAIL)).thenReturn(null);
        doNothing().when(redisUtil).set(anyString(), anyString(), anyLong(), any());
        doNothing().when(mailUtil).sendEmail(anyString(), anyString(), anyString());

        // When
        mailService.sendVerificationCode(TEST_EMAIL);

        // Verify
        verify(redisUtil).set(eq("email_send_time:" + TEST_EMAIL), anyString(), eq(60000L), any());
    }

    /**
     * no record in Redis
     * Expected: return false
     */
    @Test
    void verifyEmail_NoCodeInRedis_ShouldReturnFalse() {
        // Given
        when(redisUtil.hasKey(TEST_EMAIL)).thenReturn(false);

        // When
        boolean result = mailService.verifyEmail(TEST_EMAIL, VERIFICATION_CODE);

        // Then
        assertFalse(result);
        verify(redisUtil, never()).get(TEST_EMAIL);
        verify(redisUtil, never()).delete(TEST_EMAIL);
    }

    /**
     * Test case: verify failed
     * Expected: return false
     */
    @Test
    void verifyEmail_CodeNotMatch_ShouldReturnFalse() {
        // Given
        when(redisUtil.hasKey(TEST_EMAIL)).thenReturn(true);
        when(redisUtil.get(TEST_EMAIL)).thenReturn("654321"); // wrong code

        // When
        boolean result = mailService.verifyEmail(TEST_EMAIL, VERIFICATION_CODE);

        // Then
        assertFalse(result);
        verify(redisUtil, never()).delete(TEST_EMAIL);
    }

    /**
     * successful verification
     * Expected: return true, delete code
     */
    @Test
    void verifyEmail_CodeMatch_ShouldReturnTrueAndDeleteCode() {
        // Given
        when(redisUtil.hasKey(TEST_EMAIL)).thenReturn(true);
        when(redisUtil.get(TEST_EMAIL)).thenReturn(VERIFICATION_CODE);

        // When
        boolean result = mailService.verifyEmail(TEST_EMAIL, VERIFICATION_CODE);

        // Then
        assertTrue(result);
        verify(redisUtil).delete(TEST_EMAIL);
    }

    /**
     * generate secure code in 6 digits
     */
    @Test
    void generateSecureCode_NormalCase_ShouldGenerate6DigitCode() {
        // When
        String code = mailService.generateSecureCode();

        // Then
        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}")); // ensure 6 digits
    }

    /**
     * code should be different
     */
    @Test
    void generateSecureCode_MultipleCalls_ShouldGenerateDifferentCodes() {
        // When
        String code1 = mailService.generateSecureCode();
        String code2 = mailService.generateSecureCode();

        // Then
        assertNotEquals(code1, code2);
    }
}
