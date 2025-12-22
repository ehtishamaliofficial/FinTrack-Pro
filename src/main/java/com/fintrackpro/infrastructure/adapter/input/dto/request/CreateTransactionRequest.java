package com.fintrackpro.infrastructure.adapter.input.dto.request;

import com.fintrackpro.domain.valueobject.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating a new transaction.
 */
public record CreateTransactionRequest(
        @NotNull(message = "Wallet ID is required") Long walletId,

        Long categoryId,

        Long toWalletId,

        @NotNull(message = "Transaction type is required") TransactionType type,

        @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount,

        @Size(min = 3, max = 3, message = "Currency code must be 3 characters") String currency,

        LocalDate transactionDate,

        @Size(max = 255, message = "Description cannot exceed 255 characters") String description,

        String notes,
        String referenceNumber,
        String payee,
        String location,
        String tags,
        String receiptUrl,
        String attachmentUrl,
        boolean isRecurring,
        String recurringPattern) {
    public CreateTransactionRequest {
        if (transactionDate == null) {
            transactionDate = LocalDate.now();
        }
        if (currency == null) {
            currency = "USD";
        }
    }
}
