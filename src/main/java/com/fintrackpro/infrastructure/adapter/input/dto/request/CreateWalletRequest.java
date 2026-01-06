package com.fintrackpro.infrastructure.adapter.input.dto.request;

import com.fintrackpro.domain.valueobject.WalletType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateWalletRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name,

        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description,

        @NotNull(message = "Wallet type is required")
        WalletType walletType,

        @Size(min = 3, max = 3, message = "Currency code must be 3 characters")
        String currency,

        @DecimalMin(value = "0.0", message = "Initial balance cannot be negative")
        BigDecimal initialBalance,

        @DecimalMin(value = "0.0", message = "Credit limit cannot be negative")
        BigDecimal creditLimit,

        @Pattern(
                regexp = "^#[0-9A-F]{6}$",
                message = "Color must be a valid HEX value like #A0B1C2"
        )
        String color,
        String icon,
        boolean isDefault,
        boolean isExcludedFromTotal,
        String bankName,
        String accountNumber,
        String accountType,
        String investmentType,
        String institutionName,
        Integer displayOrder,
        String notes
) {
    public CreateWalletRequest {
        if (initialBalance == null) {
            initialBalance = BigDecimal.ZERO;
        }
        if (currency == null) {
            currency = "USD";
        }
        if (color == null) {
            color = "#4A90E2";
        }
        if (icon == null) {
            icon = "wallet";
        }
    }
}