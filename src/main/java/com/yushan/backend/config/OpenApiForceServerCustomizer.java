package com.yushan.backend.config;

import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.List;

@Configuration
public class OpenApiForceServerCustomizer {
    @Bean
    public OpenApiCustomizer forceRelativeServer() {
        return openApi -> openApi.setServers(List.of(new Server().url("/")));
    }
    
    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}
