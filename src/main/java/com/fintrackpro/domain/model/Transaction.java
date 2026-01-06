package com.fintrackpro.domain.model;

import com.fintrackpro.domain.valueobject.TransactionStatus;
import com.fintrackpro.domain.valueobject.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a financial transaction in the FinTrack Pro application.
 * A transaction records the movement of money either as income, expense, or
 * transfer between wallets.
 */
public record Transaction(
        // Primary Key
        Long id,

        // User and Wallet References
        Long userId,
        Long walletId,
        Long categoryId,

        // Transfer-specific (only for TRANSFER type)
        Long toWalletId,

        // Transaction Details
        TransactionType type,
        BigDecimal amount,
        String currency,
        LocalDate transactionDate,
        String description,
        String notes,

        // Status and Metadata
        TransactionStatus status,
        String referenceNumber,
        String payee,
        String location,
        String tags,

        // Attachments
        String receiptUrl,
        String attachmentUrl,

        // Recurring Transaction (future enhancement)
        Boolean isRecurring,
        String recurringPattern,
        Long recurringGroupId,

        // Audit Fields
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdBy,
        Long updatedBy,

        // Soft Delete
        LocalDateTime deletedAt,
        Boolean deleted) {
    // Compact constructor with validation and defaults
    public Transaction {
        // Set defaults
        status = status != null ? status : TransactionStatus.COMPLETED;
        currency = currency != null && !currency.trim().isEmpty() ? currency : "USD";
        deleted = deleted != null ? deleted : false;
        isRecurring = isRecurring != null ? isRecurring : false;

        // Validation
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }

        if (type == null) {
            throw new IllegalArgumentException("Transaction type is required");
        }

        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (walletId == null) {
            throw new IllegalArgumentException("Wallet ID is required");
        }

        // For TRANSFER type, toWalletId is required
        if (type == TransactionType.TRANSFER && toWalletId == null) {
            throw new IllegalArgumentException("Destination wallet ID is required for transfer transactions");
        }

        // For INCOME/EXPENSE, categoryId is required
        if ((type == TransactionType.INCOME || type == TransactionType.EXPENSE) && categoryId == null) {
            throw new IllegalArgumentException("Category ID is required for income/expense transactions");
        }

        // Transaction date defaults to today if not provided
        if (transactionDate == null) {
            transactionDate = LocalDate.now();
        }
    }

    // Factory method for creating an income transaction
    public static Transaction createIncome(
            Long userId,
            Long walletId,
            Long categoryId,
            BigDecimal amount,
            String description,
            LocalDate transactionDate) {
        return new Transaction(
                null, // id will be generated
                userId,
                walletId,
                categoryId,
                null, // no toWalletId for income
                TransactionType.INCOME,
                amount,
                "USD",
                transactionDate,
                description,
                null, // notes
                TransactionStatus.COMPLETED,
                null, // referenceNumber
                null, // payee
                null, // location
                null, // tags
                null, // receiptUrl
                null, // attachmentUrl
                false, // isRecurring
                null, // recurringPattern
                null, // recurringGroupId
                LocalDateTime.now(), // createdAt
                null, // updatedAt
                userId, // createdBy
                null, // updatedBy
                null, // deletedAt
                false // deleted
        );
    }

    // Factory method for creating an expense transaction
    public static Transaction createExpense(
            Long userId,
            Long walletId,
            Long categoryId,
            BigDecimal amount,
            String description,
            LocalDate transactionDate) {
        return new Transaction(
                null, // id will be generated
                userId,
                walletId,
                categoryId,
                null, // no toWalletId for expense
                TransactionType.EXPENSE,
                amount,
                "USD",
                transactionDate,
                description,
                null, // notes
                TransactionStatus.COMPLETED,
                null, // referenceNumber
                null, // payee
                null, // location
                null, // tags
                null, // receiptUrl
                null, // attachmentUrl
                false, // isRecurring
                null, // recurringPattern
                null, // recurringGroupId
                LocalDateTime.now(), // createdAt
                null, // updatedAt
                userId, // createdBy
                null, // updatedBy
                null, // deletedAt
                false // deleted
        );
    }

    // Factory method for creating a transfer transaction
    public static Transaction createTransfer(
            Long userId,
            Long fromWalletId,
            Long toWalletId,
            BigDecimal amount,
            String description,
            LocalDate transactionDate) {
        return new Transaction(
                null, // id will be generated
                userId,
                fromWalletId,
                null, // no category for transfers
                toWalletId,
                TransactionType.TRANSFER,
                amount,
                "USD",
                transactionDate,
                description,
                null, // notes
                TransactionStatus.COMPLETED,
                null, // referenceNumber
                null, // payee
                null, // location
                null, // tags
                null, // receiptUrl
                null, // attachmentUrl
                false, // isRecurring
                null, // recurringPattern
                null, // recurringGroupId
                LocalDateTime.now(), // createdAt
                null, // updatedAt
                userId, // createdBy
                null, // updatedBy
                null, // deletedAt
                false // deleted
        );
    }

    // Helper methods
    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    public boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }

    public boolean isTransfer() {
        return type == TransactionType.TRANSFER;
    }

    public boolean isCompleted() {
        return status == TransactionStatus.COMPLETED;
    }

    public boolean isPending() {
        return status == TransactionStatus.PENDING;
    }

    public boolean isCancelled() {
        return status == TransactionStatus.CANCELLED;
    }

    public String getFormattedAmount() {
        return String.format("%s %,.2f", currency, amount);
    }

    // Get the effective amount for wallet balance calculation
    // Positive for income, negative for expense
    public BigDecimal getEffectiveAmount() {
        return switch (type) {
            case INCOME -> amount;
            case EXPENSE -> amount.negate();
            case TRANSFER -> amount.negate(); // Deduct from source wallet
        };
    }
}
