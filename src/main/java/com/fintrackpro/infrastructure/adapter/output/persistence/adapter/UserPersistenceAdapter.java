package com.fintrackpro.infrastructure.adapter.output.persistence.adapter;

import com.fintrackpro.domain.model.User;
import com.fintrackpro.domain.port.output.UserRepositoryPort;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.repository.JpaUserRepository;
import com.fintrackpro.infrastructure.mapper.UserPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {


    private final UserPersistenceMapper mapper;
    private final JpaUserRepository jpaUserRepository;

    @Override
    public User save(User user) {
        UserEntity entity;
        
        if (user.id() != null) {
            // Update existing user - fetch the entity first
            entity = jpaUserRepository.findById(user.id())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + user.id()));
            
            // Update fields
            entity.setUsername(user.username());
            entity.setEmail(user.email());
            entity.setPassword(user.password());
            entity.setFirstName(user.firstName());
            entity.setLastName(user.lastName());
            entity.setPhoneNumber(user.phoneNumber());
            entity.setDateOfBirth(user.dateOfBirth());
            entity.setProfilePictureUrl(user.profilePictureUrl());
            entity.setEnabled(user.enabled());
            entity.setAccountNonLocked(user.accountNonLocked());
            entity.setEmailVerified(user.emailVerified());
            entity.setFailedLoginAttempts(user.failedLoginAttempts());
            entity.setAccountLockedUntil(user.accountLockedUntil());
            entity.setLastLoginAt(user.lastLoginAt());
            entity.setLastLoginIp(user.lastLoginIp());
            entity.setEmailVerificationToken(user.emailVerificationToken());
            entity.setEmailVerificationTokenExpiry(user.emailVerificationTokenExpiry());
            entity.setPasswordResetToken(user.passwordResetToken());
            entity.setPasswordResetTokenExpiry(user.passwordResetTokenExpiry());
            entity.setDefaultCurrency(user.defaultCurrency());
            entity.setTimezone(user.timezone());
            entity.setLanguage(user.language());
            entity.setUpdatedBy(user.updatedBy());
            entity.setDeleted(user.deleted());
            entity.setDeletedAt(user.deletedAt());
        } else {
            // New user - create new entity
            entity = mapper.toEntity(user);
        }
        
        UserEntity saved = jpaUserRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaUserRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmailOrUsername(String email, String username) {
        return jpaUserRepository.findByEmailOrUsername(email, username).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailVerificationToken(String token) {
        return jpaUserRepository.findByEmailVerificationToken(token).map(mapper::toDomain);
    }
}
