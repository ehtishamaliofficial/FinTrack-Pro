package com.fintrackpro.infrastructure.adapter.input.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateUserProfileRequest(
        @Size(min = 2, max = 50, message = "{firstName.size}")
        String firstName,

        @Size(min = 2, max = 50, message = "{lastName.size}")
        String lastName,

        @Pattern(regexp = "^\\+?[0-9\\-\\(\\)\\s]{8,20}$", message = "{phone.invalid}")
        String phoneNumber,

        @Past(message = "{dateOfBirth.past}")
        LocalDate dateOfBirth,

        @Size(max = 500, message = "Profile picture URL cannot exceed 500 characters")
        String profilePictureUrl
) {
}
