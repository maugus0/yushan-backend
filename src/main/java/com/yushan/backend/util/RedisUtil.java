package com.yushan.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {
    @Resource
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 设置字符串值 + 过期时间
     */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 设置字符串值（永不过期）
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 获取字符串值
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 判断 key 是否存在
     */
    public boolean hasKey(String key) {
        return redisTemplate != null && redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     */
    public void expire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取剩余过期时间（秒）
     * @return 秒数，-1 表示永不过期，-2 表示不存在
     */
    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 删除 key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 自增（适用于计数器）
     */
    public Long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 自减
     */
    public Long decr(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    public <T> void setJson(String key, T value, long timeout, TimeUnit unit) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, timeout, unit);
        } catch (Exception e) {
            log.info("JSON序列化失败: {}",ExceptionUtils.getStackTrace(e));
        }
    }

    public <T> T getJson(String key, Class<T> clazz) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.info("JSON反序列化失败: {}",ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}