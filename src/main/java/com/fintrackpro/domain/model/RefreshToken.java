package com.fintrackpro.domain.model;


import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record RefreshToken(
        Long id,
        String token,
        User user,
        LocalDateTime expiresAt,
        boolean revoked,
        LocalDateTime revokedAt,
        String ipAddress,
        String userAgent
) {
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }
}
