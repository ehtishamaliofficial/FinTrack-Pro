package com.fintrackpro.domain.model;

import com.fintrackpro.domain.valueobject.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Domain model for transaction filtering criteria.
 */
public record TransactionFilter(
        LocalDate startDate,
        LocalDate endDate,
        Long categoryId,
        Long walletId,
        TransactionType type,
        BigDecimal minAmount,
        BigDecimal maxAmount) {
}
