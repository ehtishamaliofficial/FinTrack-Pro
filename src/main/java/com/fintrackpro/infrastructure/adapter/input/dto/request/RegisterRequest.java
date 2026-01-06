package com.fintrackpro.infrastructure.adapter.input.dto.request;


import com.fintrackpro.infrastructure.validation.annotation.FieldMatch;
import jakarta.validation.constraints.*;

import java.time.LocalDate;


/**
 * DTO for user registration requests
 */
@FieldMatch(
        first = "password",
        second = "confirmPassword",
        message = "{confirmPassword.match}"
)
public record RegisterRequest(
        @NotBlank(message = "{firstName.required}")
        @Size(min = 2, max = 50, message = "{firstName.size}")
        String firstName,

        @NotBlank(message = "{lastName.required}")
        @Size(min = 2, max = 50, message = "{lastName.size}")
        String lastName,

        @NotBlank(message = "{email.required}")
        @Email(message = "{email.invalid}")
        String email,

        @NotBlank(message = "{username.required}")
        @Pattern(
                regexp = "^(?=.{3,20}$)(?!_)(?!.*__)[A-Za-z0-9_]+(?<!_)$",
                message = "{username.pattern}"
        )
        String username,

        @NotBlank(message = "{password.required}")
        @Size(min = 8, max = 30, message = "{password.size}")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
                message = "{password.pattern}"
        )
        String password,

        @NotBlank(message = "{confirmPassword.required}")
        String confirmPassword,
        
        // New required fields
        @NotBlank(message = "{phone.required}")
        @Pattern(regexp = "^\\+?[0-9\\-\\(\\)\\s]{8,20}$", message = "{phone.invalid}")
        String phoneNumber,
        
        @NotNull(message = "{dateOfBirth.required}")
        @Past(message = "{dateOfBirth.past}")
        LocalDate dateOfBirth,
        
        // Optional fields
        String profilePictureUrl,
        
        @Size(min = 2, max = 50, message = "{language.size}")
        String language,
        
        @Size(min = 2, max = 50, message = "{timezone.size}")
        String timezone,
        
        @Size(min = 3, max = 3, message = "{currency.size}")
        String currency
) {
    public RegisterRequest {
        // Set default values for optional fields if not provided
        language = language != null ? language : "en";
        timezone = timezone != null ? timezone : "UTC";
    }
}