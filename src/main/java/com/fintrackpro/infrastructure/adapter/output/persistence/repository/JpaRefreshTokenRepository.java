package com.fintrackpro.infrastructure.adapter.output.persistence.repository;

import com.fintrackpro.infrastructure.adapter.output.persistence.entity.RefreshTokenEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    List<RefreshTokenEntity> findByUser(UserEntity user);

    @Query("SELECT rt FROM RefreshTokenEntity rt WHERE rt.user = :user AND rt.revoked = false AND rt.expiresAt > :now")
    List<RefreshTokenEntity> findValidTokensByUser(UserEntity user, LocalDateTime now);

    @Modifying
    @Query("UPDATE RefreshTokenEntity rt SET rt.revoked = true, rt.revokedAt = :now WHERE rt.user = :user AND rt.revoked = false")
    void revokeAllUserTokens(UserEntity user, LocalDateTime now);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
}
