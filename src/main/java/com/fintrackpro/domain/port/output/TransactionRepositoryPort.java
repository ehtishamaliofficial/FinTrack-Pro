package com.fintrackpro.domain.port.output;

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
 * Port interface for Transaction repository operations.
 * This interface defines the contract for transaction-related data access.
 */
public interface TransactionRepositoryPort {

    /**
     * Finds all transactions (paginated) for a specific user with filtering.
     *
     * @param userId   the user ID
     * @param filter   the filter criteria
     * @param pageable the pagination information
     * @return page of transactions
     */
    Page<Transaction> findAll(Long userId, TransactionFilter filter, Pageable pageable);

    /**
     * Saves a transaction (create or update).
     *
     * @param transaction the transaction to save
     * @return the saved transaction with generated ID
     */
    Transaction save(Transaction transaction);

    /**
     * Finds a transaction by its ID.
     *
     * @param id the transaction ID
     * @return Optional containing the transaction if found
     */
    Optional<Transaction> findById(Long id);

    /**
     * Finds all transactions for a specific user.
     *
     * @param userId the user ID
     * @return list of transactions
     */
    List<Transaction> findAllByUserId(Long userId);

    /**
     * Finds transactions by user ID and wallet ID.
     *
     * @param userId   the user ID
     * @param walletId the wallet ID
     * @return list of transactions
     */
    List<Transaction> findByUserIdAndWalletId(Long userId, Long walletId);

    /**
     * Finds transactions by user ID and category ID.
     *
     * @param userId     the user ID
     * @param categoryId the category ID
     * @return list of transactions
     */
    List<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId);

    /**
     * Finds transactions by user ID and type.
     *
     * @param userId the user ID
     * @param type   the transaction type
     * @return list of transactions
     */
    List<Transaction> findByUserIdAndType(Long userId, TransactionType type);

    /**
     * Finds transactions within a date range for a user.
     *
     * @param userId    the user ID
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @return list of transactions
     */
    List<Transaction> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Finds recent transactions for a user.
     *
     * @param userId the user ID
     * @param limit  maximum number of transactions to return
     * @return list of recent transactions
     */
    List<Transaction> findRecentByUserId(Long userId, int limit);

    /**
     * Searches transactions by description or notes.
     *
     * @param userId     the user ID
     * @param searchTerm the search term
     * @return list of matching transactions
     */
    List<Transaction> searchByDescription(Long userId, String searchTerm);

    /**
     * Checks if any transactions exist for the given category.
     * This is used to prevent deletion of categories that have associated
     * transactions.
     *
     * @param categoryId the ID of the category to check
     * @return true if at least one transaction exists for this category, false
     *         otherwise
     */
    boolean existsByCategoryId(Long categoryId);

    /**
     * Checks if any transactions exist for the given wallet.
     *
     * @param walletId the wallet ID
     * @return true if transactions exist, false otherwise
     */
    boolean existsByWalletId(Long walletId);

    /**
     * Counts transactions for a user.
     *
     * @param userId the user ID
     * @return count of transactions
     */
    long countByUserId(Long userId);

    /**
     * Counts transactions for a wallet.
     *
     * @param walletId the wallet ID
     * @return count of transactions
     */
    long countByWalletId(Long walletId);

    /**
     * Calculates total amount by user and type within a date range.
     *
     * @param userId    the user ID
     * @param type      the transaction type
     * @param startDate the start date
     * @param endDate   the end date
     * @return total amount
     */
    BigDecimal sumAmountByUserIdAndTypeAndDateRange(
            Long userId, TransactionType type, LocalDate startDate, LocalDate endDate);

    /**
     * Calculates total amount by user and type within a date range and groups by
     * date.
     *
     * @param userId    the user ID
     * @param type      the transaction type
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of results
     */
    List<Object[]> sumAmountByUserIdAndTypeGroupByDate(
            Long userId, TransactionType type, LocalDate startDate, LocalDate endDate);

    /**
     * Calculates total amount by user and type within a date range and groups by
     * category.
     *
     * @param userId    the user ID
     * @param type      the transaction type
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of results
     */
    List<Object[]> sumAmountByUserIdAndTypeGroupByCategory(
            Long userId, TransactionType type, LocalDate startDate, LocalDate endDate);

    /**
     * Calculates total amount by user and type within a date range and groups by
     * day of week.
     *
     * @param userId    the user ID
     * @param type      the transaction type
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of results
     */
    List<Object[]> sumAmountByUserIdAndTypeGroupByDayOfWeek(
            Long userId, TransactionType type, LocalDate startDate, LocalDate endDate);

    /**
     * Counts transactions by user and date range.
     *
     * @param userId    the user ID
     * @param startDate the start date
     * @param endDate   the end date
     * @return count of transactions
     */
    Long countByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Deletes a transaction by ID.
     *
     * @param id the transaction ID
     */
    void deleteById(Long id);

    /**
     * Soft deletes a transaction by ID.
     *
     * @param id the transaction ID
     */
    void softDeleteById(Long id);
}
