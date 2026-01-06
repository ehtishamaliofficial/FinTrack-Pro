package com.fintrackpro.application.port.output;

import com.fintrackpro.domain.model.RefreshToken;
import com.fintrackpro.domain.model.User;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken save(RefreshToken refreshToken, Long userId);

    void revokeAllByUserId(Long userId);
}
