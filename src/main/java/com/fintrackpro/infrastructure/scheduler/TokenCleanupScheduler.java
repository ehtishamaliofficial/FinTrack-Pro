package com.fintrackpro.infrastructure.scheduler;

import com.fintrackpro.infrastructure.adapter.output.persistence.repository.JpaRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {

    private final JpaRefreshTokenRepository refreshTokenRepository;

    /**
     * Clean up expired refresh tokens every day at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Starting cleanup of expired refresh tokens");
        
        try {
            refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
            log.info("Successfully cleaned up expired refresh tokens");
        } catch (Exception e) {
            log.error("Error cleaning up expired tokens", e);
        }
    }
}
