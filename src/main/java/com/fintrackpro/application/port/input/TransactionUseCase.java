package com.fintrackpro.application.port.input;

import com.fintrackpro.domain.model.Transaction;
import com.fintrackpro.domain.model.TransactionFilter;
import com.fintrackpro.domain.valueobject.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Use case interface for transaction operations.
 * Defines all business operations related to transactions.
 */
public interface TransactionUseCase {

    /**
     * Creates a new transaction.
     *
     * @param transaction the transaction to create
     * @return the created transaction
     */
    Transaction createTransaction(Transaction transaction);

    /**
     * Retrieves a transaction by ID.
     *
     * @param id the transaction ID
     * @return Optional containing the transaction if found
     */
    Optional<Transaction> getTransactionById(Long id);

    /**
     * Retrieves transactions with filtering and pagination.
     *
     * @param userId   the user ID
     * @param filter   the filter criteria
     * @param pageable pagination info
     * @return page of transactions
     */
    Page<Transaction> getFilteredTransactions(Long userId, TransactionFilter filter, Pageable pageable);

    /**
     * Retrieves all transactions for a user.
     *
     * @param userId the user ID
     * @return list of transactions
     */
    List<Transaction> getUserTransactions(Long userId);

    /**
     * Retrieves transactions for a specific wallet.
     *
     * @param userId   the user ID
     * @param walletId the wallet ID
     * @return list of transactions
     */
    List<Transaction> getWalletTransactions(Long userId, Long walletId);

    /**
     * Retrieves transactions by category.
     *
     * @param userId     the user ID
     * @param categoryId the category ID
     * @return list of transactions
     */
    List<Transaction> getTransactionsByCategory(Long userId, Long categoryId);

    /**
     * Retrieves transactions by type.
     *
     * @param userId the user ID
     * @param type   the transaction type
     * @return list of transactions
     */
    List<Transaction> getTransactionsByType(Long userId, TransactionType type);

    /**
     * Retrieves transactions within a date range.
     *
     * @param userId    the user ID
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of transactions
     */
    List<Transaction> getTransactionsByDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves recent transactions.
     *
     * @param userId the user ID
     * @param limit  maximum number of transactions
     * @return list of recent transactions
     */
    List<Transaction> getRecentTransactions(Long userId, int limit);

    /**
     * Searches transactions by description.
     *
     * @param userId     the user ID
     * @param searchTerm the search term
     * @return list of matching transactions
     */
    List<Transaction> searchTransactions(Long userId, String searchTerm);

    /**
     * Updates an existing transaction.
     *
     * @param transaction the transaction with updated data
     * @return the updated transaction
     */
    Transaction updateTransaction(Transaction transaction);

    /**
     * Deletes a transaction.
     *
     * @param id the transaction ID
     */
    void deleteTransaction(Long id);

    /**
     * Calculates total income for a user within a date range.
     *
     * @param userId    the user ID
     * @param startDate the start date
     * @param endDate   the end date
     * @return total income amount
     */
    BigDecimal calculateTotalIncome(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Calculates total expenses for a user within a date range.
     *
     * @param userId    the user ID
     * @param startDate the start date
     * @param endDate   the end date
     * @return total expense amount
     */
    BigDecimal calculateTotalExpenses(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Transfers money between two wallets.
     *
     * @param userId          the user ID
     * @param fromWalletId    the source wallet ID
     * @param toWalletId      the destination wallet ID
     * @param amount          the amount to transfer
     * @param description     the transfer description
     * @param transactionDate the transaction date
     * @return the created transfer transaction
     */
    Transaction transferBetweenWallets(
            Long userId,
            Long fromWalletId,
            Long toWalletId,
            BigDecimal amount,
            String description,
            LocalDate transactionDate);
}
