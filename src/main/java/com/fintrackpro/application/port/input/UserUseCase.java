package com.fintrackpro.application.port.input;

import com.fintrackpro.domain.model.User;

import java.time.LocalDate;

public interface UserUseCase {

    User getCurrentUser(Long userId);

    User updateProfile(Long userId, String firstName, String lastName, String phoneNumber, LocalDate dateOfBirth,
                       String profilePictureUrl);

    User updatePreferences(Long userId, String defaultCurrency, String timezone, String language);
}
