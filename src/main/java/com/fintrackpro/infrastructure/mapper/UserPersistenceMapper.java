package com.fintrackpro.infrastructure.mapper;

import com.fintrackpro.domain.model.User;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;
        return UserEntity.builder()
                .id(domain.id())
                .username(domain.username())
                .email(domain.email())
                .password(domain.password())
                .firstName(domain.firstName())
                .lastName(domain.lastName())
                .phoneNumber(domain.phoneNumber())
                .dateOfBirth(domain.dateOfBirth())
                .profilePictureUrl(domain.profilePictureUrl())
                .enabled(domain.enabled())
                .accountNonLocked(domain.accountNonLocked())
                .emailVerified(domain.emailVerified())
                .failedLoginAttempts(domain.failedLoginAttempts())
                .accountLockedUntil(domain.accountLockedUntil())
                .lastLoginAt(domain.lastLoginAt())
                .lastLoginIp(domain.lastLoginIp())
                .emailVerificationToken(domain.emailVerificationToken())
                .emailVerificationTokenExpiry(domain.emailVerificationTokenExpiry())
                .passwordResetToken(domain.passwordResetToken())
                .passwordResetTokenExpiry(domain.passwordResetTokenExpiry())
                .defaultCurrency(domain.defaultCurrency())
                .timezone(domain.timezone())
                .language(domain.language())
                .createdAt(domain.createdAt())
                .updatedAt(domain.updatedAt())
                .createdBy(domain.createdBy())
                .updatedBy(domain.updatedBy())
                .deleted(domain.deleted())
                .deletedAt(domain.deletedAt())
                .build();
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .phoneNumber(entity.getPhoneNumber())
                .dateOfBirth(entity.getDateOfBirth())
                .profilePictureUrl(entity.getProfilePictureUrl())
                .enabled(entity.getEnabled())
                .accountNonLocked(entity.getAccountNonLocked())
                .emailVerified(entity.getEmailVerified())
                .failedLoginAttempts(entity.getFailedLoginAttempts())
                .accountLockedUntil(entity.getAccountLockedUntil())
                .lastLoginAt(entity.getLastLoginAt())
                .lastLoginIp(entity.getLastLoginIp())
                .emailVerificationToken(entity.getEmailVerificationToken())
                .emailVerificationTokenExpiry(entity.getEmailVerificationTokenExpiry())
                .passwordResetToken(entity.getPasswordResetToken())
                .passwordResetTokenExpiry(entity.getPasswordResetTokenExpiry())
                .defaultCurrency(entity.getDefaultCurrency())
                .timezone(entity.getTimezone())
                .language(entity.getLanguage())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .deleted(entity.getDeleted())
                .deletedAt(entity.getDeletedAt())
                .build();
    }
}