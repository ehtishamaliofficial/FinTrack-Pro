package com.fintrackpro.application.service;

import com.fintrackpro.application.port.input.AuthUseCase;
import com.fintrackpro.application.port.output.EmailServicePort;
import com.fintrackpro.application.port.output.RefreshTokenRepositoryPort;
import com.fintrackpro.domain.exception.InvalidRequestException;
import com.fintrackpro.domain.model.RefreshToken;
import com.fintrackpro.domain.model.User;
import com.fintrackpro.domain.port.output.UserRepositoryPort;
import com.fintrackpro.infrastructure.adapter.input.dto.response.AuthResponse;
import com.fintrackpro.infrastructure.adapter.input.dto.response.UserInfo;
import com.fintrackpro.infrastructure.security.JwtService;
import com.fintrackpro.infrastructure.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordValidator passwordValidator;
    private final PasswordEncoder passwordEncoder;
    private final MessageUtil messageUtil;
    private final EmailServicePort emailServicePort;
    private final JwtService jwtService;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Value("${security.login.max-failed-attempts}")
    private int maxFailedAttempts;

    @Value("${security.login.lock-duration-minutes}")
    private long lockDurationMinutes;

    @Override
    public User register(User user) {
        if (userRepositoryPort.findByUsername(user.username()).isPresent()) {
            throw new InvalidRequestException(messageUtil.getMessage("error.username.exists"));
        }
        if (userRepositoryPort.findByEmail(user.email()).isPresent()) {
            throw new InvalidRequestException(messageUtil.getMessage("error.email.exists"));
        }
        if (!passwordValidator.validate(new PasswordData(user.password())).isValid()) {
            throw new InvalidRequestException(messageUtil.getMessage("error.password.invalid"));
        }

        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(24);

        // Encode password and set verification token
        User newUser = user.toBuilder()
                .password(passwordEncoder.encode(user.password()))
                .emailVerified(false)
                .emailVerificationToken(verificationToken)
                .emailVerificationTokenExpiry(tokenExpiry)
                .build();

        User savedUser = userRepositoryPort.save(newUser);

        // Send verification email
        emailServicePort.sendVerificationEmail(
                savedUser.email(),
                savedUser.username(),
                verificationToken
        );

        return savedUser;
    }

    @Override
    @Transactional(noRollbackFor = InvalidRequestException.class)
    public AuthResponse login(String email, String password, String ipAddress, String userAgent) {
        Optional<User> userOptional = userRepositoryPort.findByEmail(email);

        if (userOptional.isEmpty()) {
            log.warn("Login attempt for non-existent email: {}", email);
            throw new InvalidRequestException(messageUtil.getMessage("error.invalid.credentials"));
        }

        User user = userOptional.get();

        // Check if account is locked
        if (user.accountLockedUntil() != null && user.accountLockedUntil().isAfter(LocalDateTime.now())) {
            long minutesRemaining = java.time.Duration.between(LocalDateTime.now(), user.accountLockedUntil()).toMinutes();
            throw new InvalidRequestException(
                    messageUtil.getMessage("error.account.locked", minutesRemaining)
            );
        }

        // Check if email is verified
        if (!user.emailVerified()) {
            throw new InvalidRequestException(messageUtil.getMessage("error.email.not.verified"));
        }

        // Check if account is enabled
        if (!user.enabled()) {
            throw new InvalidRequestException(messageUtil.getMessage("error.account.disabled"));
        }

        // Verify password
        if (!passwordEncoder.matches(password, user.password())) {
            handleFailedLogin(user, ipAddress, userAgent);
            throw new InvalidRequestException(messageUtil.getMessage("error.invalid.credentials"));
        }

        // Reset failed attempts and unlock account on successful login
        if (user.failedLoginAttempts() > 0 || !user.accountNonLocked()) {
            user = user.unlockAccount();
        }

        // Record successful login
        user = user.recordLogin(ipAddress);
        userRepositoryPort.save(user);

        // Generate tokens
        String accessToken = jwtService.generateToken(user.username(), user.email(), user.id());
        String refreshToken = jwtService.generateRefreshToken(user.username(), user.email(), user.id());

        // Save refresh token
        saveRefreshToken(user.id(), refreshToken, ipAddress, userAgent);

        // Build response
        UserInfo userInfo = UserInfo.builder()
                .id(user.id())
                .username(user.username())
                .email(user.email())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .emailVerified(user.emailVerified())
                .build();

        log.info("User logged in successfully: {}", user.email());
        // Notify user of successful login
        emailServicePort.sendLoginSuccessEmail(
                user.email(),
                user.username(),
                ipAddress,
                userAgent,
                LocalDateTime.now()
        );

        return AuthResponse.of(accessToken, refreshToken, jwtService.getJwtExpiration(), userInfo);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshTokenValue) {
        // Find refresh token
        RefreshToken refreshToken = refreshTokenRepositoryPort.findByToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidRequestException(messageUtil.getMessage("error.invalid.refresh.token")));

        // Validate token
        if (!refreshToken.isValid()) {
            throw new InvalidRequestException(messageUtil.getMessage("error.refresh.token.expired"));
        }

        // Get user
        User user = refreshToken.user();

        // Check if user is still active
        if (!user.enabled() || !user.emailVerified()) {
            throw new InvalidRequestException(messageUtil.getMessage("error.account.disabled"));
        }

        // Generate new access token
        String newAccessToken = jwtService.generateToken(user.username(), user.email(), user.id());

        // Build response
        UserInfo userInfo = UserInfo.builder()
                .id(user.id())
                .username(user.username())
                .email(user.email())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .emailVerified(user.emailVerified())
                .build();

        log.info("Token refreshed for user: {}", user.email());

        return AuthResponse.of(newAccessToken, refreshTokenValue, jwtService.getJwtExpiration(), userInfo);
    }

    @Override
    @Transactional
    public void logout(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepositoryPort.findByToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidRequestException(messageUtil.getMessage("error.invalid.refresh.token")));

        RefreshToken revokedToken = refreshToken.toBuilder()
                .revoked(true)
                .revokedAt(LocalDateTime.now())
                .build();

        refreshTokenRepositoryPort.save(revokedToken, refreshToken.user().id());

        log.info("User logged out: {}", refreshToken.user().email());
    }

    @Override
    @Transactional
    public void logoutAll(Long userId) {
        userRepositoryPort.findById(userId)
                .orElseThrow(() -> new InvalidRequestException(messageUtil.getMessage("error.user.not.found")));

        refreshTokenRepositoryPort.revokeAllByUserId(userId);

        log.info("All sessions logged out for user ID: {}", userId);
    }

    private void handleFailedLogin(User user, String ipAddress, String userAgent) {
        User updatedUser = user.increaseFailedLoginAttempts();

        LocalDateTime lockUntil = null;
        if (updatedUser.failedLoginAttempts() >= maxFailedAttempts) {
            lockUntil = LocalDateTime.now().plusMinutes(lockDurationMinutes);
            updatedUser = updatedUser.lockAccountUntil(lockUntil);
            log.warn("Account locked due to too many failed attempts: {}", user.email());
        }

        userRepositoryPort.save(updatedUser);

        // Notify user of failed login attempt
        emailServicePort.sendLoginFailureEmail(
                updatedUser.email(),
                updatedUser.username(),
                ipAddress,
                userAgent,
                updatedUser.failedLoginAttempts(),
                lockUntil
        );
    }

    private void saveRefreshToken(Long userId, String token, String ipAddress, String userAgent) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshExpiration() / 1000))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        refreshTokenRepositoryPort.save(refreshToken, userId);
    }

    @Override
    public void verifyEmail(String token) {
        User user = userRepositoryPort.findByEmailVerificationToken(token)
                .orElseThrow(() -> new InvalidRequestException(messageUtil.getMessage("error.verification.token.invalid")));

        if (user.emailVerified()) {
            throw new InvalidRequestException(messageUtil.getMessage("error.email.already.verified"));
        }

        User verifiedUser = user.verifyEmail();
        userRepositoryPort.save(verifiedUser);

        // Send verification success email
        emailServicePort.sendVerificationSuccessEmail(verifiedUser.email(), verifiedUser.username());

        // Also send welcome email
        emailServicePort.sendWelcomeEmail(verifiedUser.email(), verifiedUser.username());
    }
}
