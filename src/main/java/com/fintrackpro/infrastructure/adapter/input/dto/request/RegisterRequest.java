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
        message = "{confirmPassword.match}"
)
public record RegisterRequest(
        @NotBlank(message = "{fullName.required}")
        @Size(min = 3, max = 50, message = "{fullName.size}")
        String fullName,

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
        String confirmPassword
) { }