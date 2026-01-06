package com.fintrackpro.application.service;

import com.fintrackpro.application.port.input.UserUseCase;
import com.fintrackpro.domain.exception.InvalidRequestException;
import com.fintrackpro.domain.model.User;
import com.fintrackpro.domain.port.output.UserRepositoryPort;
import com.fintrackpro.infrastructure.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final MessageUtil messageUtil;

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser(Long userId) {
        return getExistingUserOrThrow(userId);
    }

    @Override
    @Transactional
    public User updateProfile(Long userId, String firstName, String lastName, String phoneNumber, LocalDate dateOfBirth,
                              String profilePictureUrl) {
        User existing = getExistingUserOrThrow(userId);

        User updated = existing.toBuilder()
                .firstName(firstName != null ? firstName : existing.firstName())
                .lastName(lastName != null ? lastName : existing.lastName())
                .phoneNumber(phoneNumber != null ? phoneNumber : existing.phoneNumber())
                .dateOfBirth(dateOfBirth != null ? dateOfBirth : existing.dateOfBirth())
                .profilePictureUrl(profilePictureUrl != null ? profilePictureUrl : existing.profilePictureUrl())
                .updatedAt(LocalDateTime.now())
                .updatedBy(String.valueOf(userId))
                .build();

        log.info("Updated profile for user: {}", userId);
        return userRepositoryPort.save(updated);
    }

    @Override
    @Transactional
    public User updatePreferences(Long userId, String defaultCurrency, String timezone, String language) {
        User existing = getExistingUserOrThrow(userId);

        User updated = existing.toBuilder()
                .defaultCurrency(defaultCurrency != null ? defaultCurrency : existing.defaultCurrency())
                .timezone(timezone != null ? timezone : existing.timezone())
                .language(language != null ? language : existing.language())
                .updatedAt(LocalDateTime.now())
                .updatedBy(String.valueOf(userId))
                .build();

        log.info("Updated preferences for user: {}", userId);
        return userRepositoryPort.save(updated);
    }

    private User getExistingUserOrThrow(Long userId) {
        return userRepositoryPort.findById(userId)
                .orElseThrow(() -> new InvalidRequestException(messageUtil.getMessage("error.user.not.found")));
    }
}
