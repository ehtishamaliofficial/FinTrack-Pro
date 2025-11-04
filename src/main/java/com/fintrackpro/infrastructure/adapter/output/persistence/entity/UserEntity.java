package com.fintrackpro.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA Entity: User
 * Infrastructure layer - maps to database table
 * This is separate from the domain model to maintain clean architecture
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted = false")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================================
    // Authentication
    // ============================================

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    // ============================================
    // User Profile
    // ============================================

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    // ============================================
    // Account Status
    // ============================================

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked = true;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    // ============================================
    // Security
    // ============================================

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    // ============================================
    // Email Verification
    // ============================================

    @Column(name = "email_verification_token", length = 255)
    private String emailVerificationToken;

    @Column(name = "email_verification_token_expiry")
    private LocalDateTime emailVerificationTokenExpiry;

    // ============================================
    // Password Reset
    // ============================================

    @Column(name = "password_reset_token", length = 255)
    private String passwordResetToken;

    @Column(name = "password_reset_token_expiry")
    private LocalDateTime passwordResetTokenExpiry;

    // ============================================
    // Preferences
    // ============================================

    @Column(name = "default_currency", length = 3)
    private String defaultCurrency = "USD";

    @Column(length = 50)
    private String timezone = "UTC";

    @Column(length = 10)
    private String language = "en";

    // ============================================
    // Audit Fields
    // ============================================

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    // ============================================
    // Soft Delete
    // ============================================

    @Column(nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // ============================================
    // JPA Lifecycle Callbacks
    // ============================================

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.enabled == null) {
            this.enabled = true;
        }
        if (this.accountNonLocked == null) {
            this.accountNonLocked = true;
        }
        if (this.emailVerified == null) {
            this.emailVerified = false;
        }
        if (this.failedLoginAttempts == null) {
            this.failedLoginAttempts = 0;
        }
        if (this.deleted == null) {
            this.deleted = false;
        }
        if (this.version == null) {
            this.version = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ============================================
    // Equals and HashCode (based on business key)
    // ============================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity that)) return false;
        return email != null && email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // ============================================
    // toString (exclude sensitive data)
    // ============================================

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                ", emailVerified=" + emailVerified +
                '}';
    }
}
