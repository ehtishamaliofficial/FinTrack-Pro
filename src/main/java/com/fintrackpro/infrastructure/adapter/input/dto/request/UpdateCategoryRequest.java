package com.fintrackpro.infrastructure.adapter.input.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @NotBlank(message = "Category name is required") @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters") String name,

        @Size(max = 50, message = "Icon name cannot exceed 50 characters") String icon,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid HEX value like #A0B1C2") String color) {
    // Note: Type is not included as it cannot be changed after creation
}
