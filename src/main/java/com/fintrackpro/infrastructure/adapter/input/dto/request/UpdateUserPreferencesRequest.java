package com.fintrackpro.infrastructure.adapter.input.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateUserPreferencesRequest(
        @Size(min = 3, max = 3, message = "{currency.size}")
        String defaultCurrency,

        @Size(min = 2, max = 50, message = "{timezone.size}")
        String timezone,

        @Size(min = 2, max = 50, message = "{language.size}")
        String language
) {
}
