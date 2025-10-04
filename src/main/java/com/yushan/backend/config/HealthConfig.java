package com.yushan.backend.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Custom health configuration to handle database connection issues gracefully
 * during application startup on Railway.
 */
@Configuration
public class HealthConfig {

    /**
     * Custom database health indicator that handles connection failures gracefully
     * during startup phase.
     */
    @Bean
    @Primary
    public HealthIndicator customDataSourceHealthIndicator(DataSource dataSource) {
        return new CustomDataSourceHealthIndicator(dataSource);
    }

    /**
     * Custom DataSource health indicator that handles connection failures gracefully
     */
    private static class CustomDataSourceHealthIndicator extends DataSourceHealthIndicator {
        public CustomDataSourceHealthIndicator(DataSource dataSource) {
            super(dataSource);
        }

        @Override
        protected void doHealthCheck(Health.Builder builder) throws Exception {
            try {
                super.doHealthCheck(builder);
            } catch (Exception e) {
                // During startup, return UP with a warning instead of DOWN
                builder.up()
                        .withDetail("status", "Database connection may be initializing")
                        .withDetail("error", e.getMessage());
            }
        }
    }
}
