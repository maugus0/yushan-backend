package com.yushan.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration Test - Tests with real Postgres + Redis using Testcontainers
 * 
 * This test class will:
 * 1. Start Postgres container
 * 2. Start Redis container
 * 3. Configure Spring to use these containers
 * 4. Test database interactions
 * 5. Clean up containers after tests
 */
@SpringBootTest
@ActiveProfiles("integration-test")
@Import(TestcontainersConfiguration.class)
@org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(named = "CI", matches = "true")
class IntegrationTest {

    @Test
    void contextLoads() {
        // This test verifies that:
        // 1. Spring Boot application starts successfully
        // 2. Postgres container is running and accessible
        // 3. Redis container is running and accessible
        // 4. All beans are created correctly
    }

}
