package com.yushan.backend.config;

import com.yushan.backend.security.CustomMethodSecurityExpressionHandler;
import com.yushan.backend.security.CustomUserDetailsService;
import com.yushan.backend.security.JwtAuthenticationEntryPoint;
import com.yushan.backend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
    /**
     * Password encoder bean
     * 
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Authentication manager bean
     * 
     * @param config Authentication configuration
     * @return AuthenticationManager instance
     * @throws Exception if configuration error
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * Authentication provider bean
     * 
     * @return DaoAuthenticationProvider instance
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(passwordEncoder());
        authProvider.setUserDetailsService(customUserDetailsService);
        return authProvider;
    }
    
    /**
     * Custom method security expression handler
     * 
     * @return CustomMethodSecurityExpressionHandler instance
     */
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        return new CustomMethodSecurityExpressionHandler();
    }
    
    /**
     * Security filter chain configuration
     * 
     * @param http HttpSecurity configuration
     * @return SecurityFilterChain instance
     * @throws Exception if configuration error
     */
    @Bean
    @SuppressWarnings("java:S4502") // CSRF protection is not required for REST APIs using JWT tokens
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for JWT authentication (stateless API)
            // CSRF protection is not required for REST APIs using JWT tokens
            // as JWT tokens are stored client-side and not in cookies
            // This is safe because:
            // 1. We use JWT tokens for authentication (not cookies)
            // 2. The API is stateless (SessionCreationPolicy.STATELESS)
            // 3. JWT tokens are sent in Authorization header, not in cookies
            // 4. CSRF attacks rely on cookies which we don't use
            .csrf(csrf -> csrf.disable())
            
            // Configure session management
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Security headers to address ZAP informational findings
            .headers(headers -> headers
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .preload(true)
                    .maxAgeInSeconds(31536000)
                )
                .contentTypeOptions(Customizer.withDefaults())
                .frameOptions(frame -> frame.deny())
                .referrerPolicy(referrer -> referrer.policy(ReferrerPolicy.SAME_ORIGIN))
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives(
                        "default-src 'self'; " +
                        "frame-ancestors 'none'; " +
                        "form-action 'self'; " +
                        "img-src 'self' data:; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "script-src 'self'; " +
                        "object-src 'none'; " +
                        "base-uri 'self'; " +
                        "connect-src 'self'"))
                .addHeaderWriter(new StaticHeadersWriter("Permissions-Policy",
                    "geolocation=(), microphone=(), camera=(), payment=()"))
                .addHeaderWriter(new StaticHeadersWriter("X-XSS-Protection", "1; mode=block"))
                .addHeaderWriter(new StaticHeadersWriter("Cross-Origin-Opener-Policy", "same-origin"))
                .addHeaderWriter(new StaticHeadersWriter("Cross-Origin-Resource-Policy", "same-origin"))
            )
            
            // Configure exception handling
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            
            // Configure authorization
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - no authentication required
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/example/public").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                // Swagger/OpenAPI endpoints
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/error").permitAll()
                
                // CORS preflight requests - allow OPTIONS for all endpoints
                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()

                // Add search endpoints as public
                .requestMatchers("/api/search").permitAll()
                .requestMatchers("/api/search/**").permitAll()

                // Category APIs - public read, admin write
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers("/api/categories/**").hasRole("ADMIN")

                // Novel APIs
                .requestMatchers(HttpMethod.POST, "/api/novels").hasAnyRole("AUTHOR","ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/novels").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/novels/*").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/novels/*").authenticated()
                .requestMatchers("/api/novels/admin/**").hasRole("ADMIN")

                // Voting APIs - require authentication
                .requestMatchers(HttpMethod.POST, "/api/novels/*/vote").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/users/votes").authenticated()

                // Other protected APIs - require authentication
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/library/**").authenticated()
                .requestMatchers("/api/history/**").authenticated()
                .requestMatchers("/api/comments/**").authenticated()
                .requestMatchers("/api/reports/**").authenticated()
                
                // Review APIs - mixed public and authenticated
                .requestMatchers(HttpMethod.GET, "/api/reviews/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reviews/novel/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reviews").permitAll()
                .requestMatchers("/api/reviews/**").authenticated()
                
                // Ranking APIs - public
                .requestMatchers("/api/ranking/**").permitAll()

                // Admin endpoints - require admin role
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Author endpoints - most require author role, but some are for registration
                .requestMatchers("/api/author/send-email-author-verification").authenticated()
                .requestMatchers("/api/author/upgrade-to-author").authenticated()
                .requestMatchers("/api/author/**").hasRole("AUTHOR")

                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
