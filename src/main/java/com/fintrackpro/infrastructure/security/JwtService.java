package com.fintrackpro.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${app.jwt.expiration:3600000}") // 1 hour in milliseconds
    private long jwtExpiration;

    @Value("${app.jwt.refresh-expiration:604800000}") // 7 days in milliseconds
    private long refreshExpiration;

    /**
     * Generate access token
     */
    public String generateToken(String username, String email, Long userId) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtExpiration, ChronoUnit.MILLIS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("fintrack-pro")
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(username)
                .claim("email", email)
                .claim("userId", userId)
                .claim("type", "access")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    /**
     * Generate refresh token
     */
    public String generateRefreshToken(String username, String email, Long userId) {
        Instant now = Instant.now();
        Instant expiry = now.plus(refreshExpiration, ChronoUnit.MILLIS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("fintrack-pro")
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(username)
                .claim("email", email)
                .claim("userId", userId)
                .claim("type", "refresh")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        try {
            return jwtDecoder.decode(token).getSubject();
        } catch (Exception e) {
            log.error("Error extracting username from token", e);
            return null;
        }
    }

    /**
     * Extract user ID from token
     */
    public Long extractUserId(String token) {
        try {
            return jwtDecoder.decode(token).getClaim("userId");
        } catch (Exception e) {
            log.error("Error extracting userId from token", e);
            return null;
        }
    }

    /**
     * Validate token
     */
    public boolean isTokenValid(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid token", e);
            return false;
        }
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Instant expiry = jwtDecoder.decode(token).getExpiresAt();
            return expiry != null && expiry.isBefore(Instant.now());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Get token expiration time in milliseconds
     */
    public long getJwtExpiration() {
        return jwtExpiration;
    }

    /**
     * Get refresh token expiration time in milliseconds
     */
    public long getRefreshExpiration() {
        return refreshExpiration;
    }
}
