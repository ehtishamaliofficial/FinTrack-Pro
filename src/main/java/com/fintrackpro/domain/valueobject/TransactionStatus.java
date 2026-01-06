package com.fintrackpro.domain.valueobject;

/**
 * Represents the status of a transaction.
 */
public enum TransactionStatus {
    /**
     * Transaction is pending processing
     */
    PENDING,

    /**
     * Transaction has been completed successfully
     */
    COMPLETED,

    /**
     * Transaction was cancelled by user
     */
    CANCELLED,

    /**
     * Transaction failed due to an error
     */
    FAILED
}
