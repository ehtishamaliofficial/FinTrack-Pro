package com.fintrackpro.infrastructure.adapter.input.dto.request;

import com.fintrackpro.domain.valueobject.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank(message = "Category name is required") @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters") String name,

        @NotNull(message = "Category type is required") CategoryType type,

        @Size(max = 50, message = "Icon name cannot exceed 50 characters") String icon,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid HEX value like #A0B1C2") String color) {
    public CreateCategoryRequest {
        // Set defaults if not provided
        if (icon == null || icon.isBlank()) {
            icon = type == CategoryType.INCOME ? "trending-up" : "shopping-cart";
        }
        if (color == null || color.isBlank()) {
            color = type == CategoryType.INCOME ? "#4CAF50" : "#F44336";
        }
    }
}
