package com.yushan.backend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
/**
 * Configuration to completely disable Spring Session JDBC
 * This is a more aggressive approach to prevent JDBC session initialization
 */
@Configuration
@Profile("staging")
@ConditionalOnProperty(name = "spring.session.store-type", havingValue = "redis")
public class DisableJdbcSessionConfig {
    
    // This configuration aggressively disables Spring Session JDBC
    // The actual exclusion is handled via application-staging.properties
}
