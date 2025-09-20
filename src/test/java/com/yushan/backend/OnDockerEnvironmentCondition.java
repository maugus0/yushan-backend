package com.yushan.backend;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;

public class OnDockerEnvironmentCondition implements Condition {

    @Override
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        // 检查是否设置了启用testcontainers的环境变量
        String enableTestcontainers = System.getenv("ENABLE_TESTCONTAINERS");
        if (enableTestcontainers != null) {
            return Boolean.parseBoolean(enableTestcontainers);
        }

        // 或者检查是否有Docker环境（简化检查）
        try {
            // 这里可以添加更复杂的Docker环境检查逻辑
            return false; // 默认在没有明确启用时禁用
        } catch (Exception e) {
            return false;
        }
    }
}
