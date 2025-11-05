package com.fintrackpro.domain.model;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder(toBuilder = true)
public record User(
        Long id,
        String username,
        String email,
        String password,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate dateOfBirth,
        String profilePictureUrl,
        Boolean enabled,
        Boolean accountNonLocked,
        Boolean emailVerified,
        Integer failedLoginAttempts,
        LocalDateTime accountLockedUntil,
        LocalDateTime lastLoginAt,
        String lastLoginIp,
        String emailVerificationToken,
        LocalDateTime emailVerificationTokenExpiry,
        String passwordResetToken,
        LocalDateTime passwordResetTokenExpiry,
        String defaultCurrency,
        String timezone,
        String language,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy,
        Boolean deleted,
        LocalDateTime deletedAt
) {
    // Compact constructor with defaults
    public User {
        enabled = enabled != null ? enabled : true;
        accountNonLocked = accountNonLocked != null ? accountNonLocked : true;
        emailVerified = emailVerified != null ? emailVerified : false;
        failedLoginAttempts = failedLoginAttempts != null ? failedLoginAttempts : 0;
        defaultCurrency = defaultCurrency != null ? defaultCurrency : "USD";
        timezone = timezone != null ? timezone : "UTC";
        language = language != null ? language : "en";
        deleted = deleted != null ? deleted : false;
    }

    // Immutable update methods using toBuilder()
    public User verifyEmail() {
        return this.toBuilder()
                .emailVerified(true)
                .emailVerificationToken(null)
                .emailVerificationTokenExpiry(null)
                .build();
    }

    public User lockAccountUntil(LocalDateTime lockedUntil) {
        return this.toBuilder()
                .accountNonLocked(false)
                .accountLockedUntil(lockedUntil)
                .build();
    }

    public User unlockAccount() {
        return this.toBuilder()
                .accountNonLocked(true)
                .accountLockedUntil(null)
                .failedLoginAttempts(0)
                .build();
    }

    public User increaseFailedLoginAttempts() {
        return this.toBuilder()
                .failedLoginAttempts(failedLoginAttempts + 1)
                .build();
    }

    public User enable() {
        return this.toBuilder()
                .enabled(true)
                .build();
    }

    public User disable() {
        return this.toBuilder()
                .enabled(false)
                .build();
    }

    public User markDeleted() {
        return this.toBuilder()
                .deleted(true)
                .build();
    }

    public User updateProfile(String firstName, String lastName, LocalDate dateOfBirth,
                              String profilePictureUrl) {
        return this.toBuilder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .profilePictureUrl(profilePictureUrl)
                .build();
    }

    public User updatePassword(String newPassword) {
        return this.toBuilder()
                .password(newPassword)
                .passwordResetToken(null)
                .passwordResetTokenExpiry(null)
                .build();
    }

    public User recordLogin(String ipAddress) {
        return this.toBuilder()
                .lastLoginAt(LocalDateTime.now())
                .lastLoginIp(ipAddress)
                .failedLoginAttempts(0)
                .build();
    }

    public User setEmailVerificationToken(String token, LocalDateTime expiry) {
        return this.toBuilder()
                .emailVerificationToken(token)
                .emailVerificationTokenExpiry(expiry)
                .build();
    }

    public User setPasswordResetToken(String token, LocalDateTime expiry) {
        return this.toBuilder()
                .passwordResetToken(token)
                .passwordResetTokenExpiry(expiry)
                .build();
    }

    public User updatePreferences(String currency, String timezone, String language) {
        return this.toBuilder()
                .defaultCurrency(currency)
                .timezone(timezone)
                .language(language)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                ", emailVerified=" + emailVerified +
                '}';
    }
}