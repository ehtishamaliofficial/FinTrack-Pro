package com.fintrackpro.infrastructure.adapter.input.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
    @NotNull(message = "Source wallet ID is required")
    Long sourceWalletId,
    
    @NotNull(message = "Target wallet ID is required")
    Long targetWalletId,
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    BigDecimal amount,
    
    String description
) {}
