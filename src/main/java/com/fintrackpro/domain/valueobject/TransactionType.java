package com.fintrackpro.domain.valueobject;

/**
 * Represents the type of a financial transaction.
 */
public enum TransactionType {
    /**
     * Income transaction - money coming in
     */
    INCOME,

    /**
     * Expense transaction - money going out
     */
    EXPENSE,

    /**
     * Transfer between wallets
     */
    TRANSFER
}
