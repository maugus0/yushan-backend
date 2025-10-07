package com.yushan.backend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Configuration for staging environment to disable Spring Session JDBC
 * and ensure only Redis sessions are used
 */
@Configuration
@Profile("staging")
@ConditionalOnProperty(name = "spring.session.store-type", havingValue = "redis")
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 86400)
public class StagingConfig {
    
    // This configuration ensures that Spring Session JDBC is completely disabled
    // when using Redis-only sessions in staging environment
    // The @EnableRedisHttpSession explicitly enables Redis sessions
    // and disables JDBC session auto-configuration
}
