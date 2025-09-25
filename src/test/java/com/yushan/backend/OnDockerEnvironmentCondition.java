package com.yushan.backend;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;

public class OnDockerEnvironmentCondition implements Condition {

    @Override
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        // Check whether the environment variable to enable Testcontainers is set
        String enableTestcontainers = System.getenv("ENABLE_TESTCONTAINERS");
        if (enableTestcontainers != null) {
            return Boolean.parseBoolean(enableTestcontainers);
        }

        // Or perform a (simplified) check for Docker environment
        try {
            // More sophisticated Docker environment checks can be added here
            return false; // Disabled by default unless explicitly enabled
        } catch (Exception e) {
            return false;
        }
    }
}
