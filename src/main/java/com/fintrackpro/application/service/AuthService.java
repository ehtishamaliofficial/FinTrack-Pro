package com.fintrackpro.application.service;

import com.fintrackpro.application.port.input.AuthUseCase;
import com.fintrackpro.application.port.output.EmailServicePort;
import com.fintrackpro.domain.exception.InvalidRequestException;
import com.fintrackpro.domain.model.User;
import com.fintrackpro.domain.port.output.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import com.fintrackpro.infrastructure.util.MessageUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordValidator passwordValidator;
    private final PasswordEncoder passwordEncoder;
    private final MessageUtil messageUtil;
    private final EmailServicePort emailServicePort;

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
    public User authenticate(String email, String password) {
        return null;
    }

    @Override
    public void verifyEmail(String token) {
        User user = userRepositoryPort.findByEmailVerificationToken(token)
                .orElseThrow(() -> new InvalidRequestException(messageUtil.getMessage("error.verification.token.invalid")));

        if (user.emailVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException(messageUtil.getMessage("error.verification.token.expired"));
        }

        if (user.emailVerified()) {
            throw new InvalidRequestException(messageUtil.getMessage("error.email.already.verified"));
        }

        User verifiedUser = user.verifyEmail();
        userRepositoryPort.save(verifiedUser);

        // Send welcome email
        emailServicePort.sendWelcomeEmail(verifiedUser.email(), verifiedUser.username());
    }
}
