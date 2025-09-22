package com.yushan.backend.util;

import com.yushan.backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * JWT Utility class for token generation, parsing and validation
 * 
 * This class provides methods to:
 * - Generate access and refresh tokens
 * - Extract information from tokens
 * - Validate tokens
 * - Check token expiration
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.algorithm}")
    private String algorithm;

    /**
     * Get the secret key for JWT signing
     * 
     * @return SecretKey object for JWT operations
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate access token for user
     * 
     * @param user User object containing user information
     * @return JWT access token as String
     */
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUuid().toString());
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());
        claims.put("isAuthor", user.getIsAuthor());
        claims.put("tokenType", "access");
        claims.put("jti", UUID.randomUUID().toString()); // Unique token ID
        
        return createToken(claims, user.getEmail(), accessTokenExpiration);
    }

    /**
     * Generate refresh token for user
     * 
     * @param user User object containing user information
     * @return JWT refresh token as String
     */
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUuid().toString());
        claims.put("email", user.getEmail());
        claims.put("tokenType", "refresh");
        claims.put("jti", UUID.randomUUID().toString()); // Unique token ID
        
        return createToken(claims, user.getEmail(), refreshTokenExpiration);
    }

    /**
     * Create JWT token with claims and expiration
     * 
     * @param claims Map of claims to include in token
     * @param subject Subject (usually username or email)
     * @param expiration Expiration time in milliseconds
     * @return JWT token as String
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract username from JWT token
     * 
     * @param token JWT token
     * @return Username from token subject
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract user ID from JWT token
     * 
     * @param token JWT token
     * @return User ID from token claims
     */
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    /**
     * Extract email from JWT token
     * 
     * @param token JWT token
     * @return Email from token claims
     */
    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    /**
     * Extract isAuthor from JWT token
     * 
     * @param token JWT token
     * @return isAuthor from token claims
     */
    public Boolean extractIsAuthor(String token) {
        return extractClaim(token, claims -> claims.get("isAuthor", Boolean.class));
    }

    /**
     * Extract token type from JWT token
     * 
     * @param token JWT token
     * @return Token type (access/refresh) from token claims
     */
    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("tokenType", String.class));
    }

    /**
     * Extract expiration date from JWT token
     * 
     * @param token JWT token
     * @return Expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract specific claim from JWT token
     * 
     * @param token JWT token
     * @param claimsResolver Function to extract specific claim
     * @return Extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from JWT token
     * 
     * @param token JWT token
     * @return Claims object containing all token claims
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if token is expired
     * 
     * @param token JWT token
     * @return true if token is expired, false otherwise
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate token against user
     * 
     * @param token JWT token
     * @param user User object to validate against
     * @return true if token is valid for user, false otherwise
     */
    public Boolean validateToken(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getEmail()) && !isTokenExpired(token));
    }

    /**
     * Validate token without user (check expiration and signature)
     * 
     * @param token JWT token
     * @return true if token is valid, false otherwise
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if token is access token
     * 
     * @param token JWT token
     * @return true if token is access token, false otherwise
     */
    public Boolean isAccessToken(String token) {
        String tokenType = extractTokenType(token);
        return "access".equals(tokenType);
    }

    /**
     * Check if token is refresh token
     * 
     * @param token JWT token
     * @return true if token is refresh token, false otherwise
     */
    public Boolean isRefreshToken(String token) {
        String tokenType = extractTokenType(token);
        return "refresh".equals(tokenType);
    }
}
