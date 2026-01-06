package com.fintrackpro.infrastructure.adapter.output.persistence.adapter;

import com.fintrackpro.application.port.output.RefreshTokenRepositoryPort;
import com.fintrackpro.domain.exception.ResourceNotFoundException;
import com.fintrackpro.domain.model.RefreshToken;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.RefreshTokenEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.repository.JpaRefreshTokenRepository;
import com.fintrackpro.infrastructure.adapter.output.persistence.repository.JpaUserRepository;
import com.fintrackpro.infrastructure.mapper.RefreshTokenPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenPersistenceAdapter implements RefreshTokenRepositoryPort {

    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;
    private final JpaUserRepository jpaUserRepository;
    private final RefreshTokenPersistenceMapper refreshTokenMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRefreshTokenRepository.findByToken(token)
                .map(refreshTokenMapper::toDomain);
    }

    @Override
    @Transactional
    public RefreshToken save(RefreshToken refreshToken, Long userId) {
        UserEntity userEntity = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        RefreshTokenEntity refreshTokenEntity = refreshTokenMapper.toEntity(refreshToken);
        refreshTokenEntity.setUser(userEntity);

        RefreshTokenEntity savedEntity = jpaRefreshTokenRepository.save(refreshTokenEntity);
        return refreshTokenMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public void revokeAllByUserId(Long userId) {
        UserEntity userEntity = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        jpaRefreshTokenRepository.revokeAllUserTokens(userEntity, LocalDateTime.now());
    }
}
