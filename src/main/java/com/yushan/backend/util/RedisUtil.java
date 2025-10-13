package com.yushan.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisUtil {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final ZSetOperations<String, String> zSetOperations;

    @SuppressFBWarnings({"EI_EXPOSE_REP2"})
    public RedisUtil(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
        this.zSetOperations = stringRedisTemplate.opsForZSet();
    }

    /**
     * set string & timeout
     */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * set string &　never timeout
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * get string
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * if key exists
     */
    public boolean hasKey(String key) {
        if (stringRedisTemplate == null) {
            return false;
        }
        Boolean result = stringRedisTemplate.hasKey(key);
        return result != null && result;
    }

    /**
     * set timeout
     */
    public void expire(String key, long timeout, TimeUnit unit) {
        stringRedisTemplate.expire(key, timeout, unit);
    }

    /**
     * get timeout(second)
     * @return -1 never timeout, -2 dont exist
     */
    public Long getExpire(String key, TimeUnit unit) {
        return stringRedisTemplate.getExpire(key, unit);
    }

    /**
     * delete key
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * self-increase(for countdown)
     */
    public Long incr(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * self-decrease
     */
    public Long decr(String key) {
        return stringRedisTemplate.opsForValue().decrement(key);
    }

    public <T> void setJson(String key, T value, long timeout, TimeUnit unit) {
        try {
            String json = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, json, timeout, unit);
        } catch (Exception e) {
            log.info("JSON serialization failed: {}",ExceptionUtils.getStackTrace(e));
        }
    }

    public <T> T getJson(String key, Class<T> clazz) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.info("JSON deserialization failed: {}",ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * add member to sorted set or update score
     */
    public void zAdd(String key, String member, double score) {
        zSetOperations.add(key, member, score);
    }

    /**
     * get a range of members from a sorted set high->low
     * @param key Redis key
     * @param start (0-based)
     * @param end  (0-based)
     * @return member set
     */
    public Set<String> zReverseRange(String key, long start, long end) {
        return zSetOperations.reverseRange(key, start, end);
    }

    /**
     * get total count of members in a sorted set
     */
    public Long zCard(String key) {
        return zSetOperations.zCard(key);
    }

    /**
     * get a member's rank in a sorted set high->low
     */
    public Long zReverseRank(String key, String member) {
        return zSetOperations.reverseRank(key, member);
    }

    /**
     * get a member's score in a sorted set
     */
    public Double zScore(String key, String member) {
        return zSetOperations.score(key, member);
    }

    /**
     * batch delete
     */
    public void delete(Collection<String> keys) {
        stringRedisTemplate.delete(keys);
    }

    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }
}