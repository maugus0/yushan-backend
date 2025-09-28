package com.yushan.backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * RedisUtil unit tests
 */
@SpringBootTest
@ActiveProfiles("test")
public class RedisUtilTest {

    private RedisUtil redisUtil;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        redisUtil = new RedisUtil(stringRedisTemplate, objectMapper);
    }

    @AfterEach
    void tearDown() {
        // clear mock
        reset(stringRedisTemplate, objectMapper, valueOperations);
    }

    // TC1: setJson normal case
    @Test
    void testSetJson_Normal() throws Exception {
        String key = "testKey";
        Object value = "testValue";
        long timeout = 10L;
        TimeUnit unit = TimeUnit.SECONDS;
        String json = "\"testValue\"";

        when(objectMapper.writeValueAsString(value)).thenReturn(json);

        redisUtil.setJson(key, value, timeout, unit);

        verify(valueOperations).set(key, json, timeout, unit);
    }

    // TC2: setJson failed
    @Test
    void testSetJson_SerializationException() throws Exception {
        String key = "testKey";
        Object value = new Object();
        long timeout = 10L;
        TimeUnit unit = TimeUnit.SECONDS;

        when(objectMapper.writeValueAsString(value)).thenThrow(new JsonProcessingException("serialization failed") {});

        redisUtil.setJson(key, value, timeout, unit);

        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any());
    }

    // TC3: getJson normal case
    @Test
    void testGetJson_Normal() throws Exception {
        String key = "testKey";
        String json = "\"testValue\"";
        String value = "testValue";

        when(valueOperations.get(key)).thenReturn(json);
        when(objectMapper.readValue(json, String.class)).thenReturn(value);

        String result = redisUtil.getJson(key, String.class);

        assertEquals(value, result);
    }

    // TC4: getJson key don't exist
    @Test
    void testGetJson_KeyNotFound() {
        String key = "nonExistentKey";

        when(valueOperations.get(key)).thenReturn(null);

        String result = redisUtil.getJson(key, String.class);

        assertNull(result);
    }

    // TC5: getJson deserialization failed
    @Test
    void testGetJson_DeserializationException() throws Exception {
        String key = "testKey";
        String json = "invalidJson";

        when(valueOperations.get(key)).thenReturn(json);
        when(objectMapper.readValue(json, String.class)).thenThrow(new JsonProcessingException("deserialization failed") {});

        String result = redisUtil.getJson(key, String.class);

        assertNull(result);
    }
}
