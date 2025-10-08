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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for JWT
            .csrf(csrf -> csrf.disable())
            
            // Configure session management
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
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
                
                // Novel APIs
                .requestMatchers(HttpMethod.POST, "/api/novels").hasAnyRole("AUTHOR","ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/novels").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/novels/*").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/novels/*").authenticated()

                // Other protected APIs - require authentication
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/libraries/**").authenticated()
                .requestMatchers("/api/history/**").authenticated()
                .requestMatchers("/api/comments/**").authenticated()
                
                // Review APIs - mixed public and authenticated
                .requestMatchers(HttpMethod.GET, "/api/reviews/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reviews/novel/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reviews").permitAll()
                .requestMatchers("/api/reviews/**").authenticated()

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
