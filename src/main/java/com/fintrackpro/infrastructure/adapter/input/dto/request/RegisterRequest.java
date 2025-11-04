package com.fintrackpro.infrastructure.adapter.input.dto.request;


import com.fintrackpro.infrastructure.validation.annotation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


/**
 * DTO for user registration requests
 */
@FieldMatch(
        first = "password",
        second = "confirmPassword",
        message = "Passwords do not match"
)
public record RegisterRequest(
        @NotBlank(message = "Full name is required")
        @Size(min = 3, max = 50, message = "Full name must be between 3 and 50 characters")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Username is required")
        @Pattern(
                regexp = "^(?=.{3,20}$)(?!_)(?!.*__)[A-Za-z0-9_]+(?<!_)$",
                message = "Username can only contain letters, numbers, and underscores, must be 3-20 chars, cannot start/end with underscore, and no consecutive underscores."
        )
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
                message = "Password must contain uppercase, lowercase, digit and special character"
        )
        String password,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) { }