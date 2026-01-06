package com.fintrackpro.infrastructure.adapter.input.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "{email.required}")
        @Email(message = "{email.invalid}")
        String email,

        @NotBlank(message = "{password.required}")
        String password
) {
}
