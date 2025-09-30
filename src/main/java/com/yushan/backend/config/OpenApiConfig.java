package com.yushan.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure OpenAPI so that generated server URL is relative ("/")
 * which lets Swagger UI use the current request scheme/host.
 * This avoids mixed-content issues when running behind a proxy (e.g. Railway)
 * that terminates TLS and forwards as HTTP to the container.
 */
@Configuration
public class OpenApiConfig {
    public static final String SECURITY_SCHEME_NAME = "BearerAuth";

    // OpenAPI annotations for security and basic info
    @io.swagger.v3.oas.annotations.OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(title = "Yushan API", version = "v1"),
        security = {@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = SECURITY_SCHEME_NAME)}
    )
    @io.swagger.v3.oas.annotations.security.SecurityScheme(
        name = SECURITY_SCHEME_NAME,
        type = io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER
    )
    static class AnnotationsHolder { }

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI();
        openAPI.setInfo(new Info().title("Yushan Backend API").version("v1"));
        // Use relative server so Swagger UI requests keep the browser's https scheme
        openAPI.setServers(List.of(new Server().url("/")));
        return openAPI;
    }
}
