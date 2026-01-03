package com.fintrackpro.infrastructure.adapter.input.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserProfileResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate dateOfBirth,
        String profilePictureUrl,
        String defaultCurrency,
        String timezone,
        String language,
        Boolean emailVerified
) {
}
