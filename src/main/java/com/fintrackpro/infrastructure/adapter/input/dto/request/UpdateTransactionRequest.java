package com.fintrackpro.infrastructure.adapter.input.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for updating an existing transaction.
 */
public record UpdateTransactionRequest(
        @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount,

        Long walletId,
        Long categoryId,
        Long toWalletId,

        LocalDate transactionDate,

        @Size(max = 255, message = "Description cannot exceed 255 characters") String description,

        String notes,
        String referenceNumber,
        String payee,
        String location,
        String tags,
        String receiptUrl,
        String attachmentUrl) {
}
