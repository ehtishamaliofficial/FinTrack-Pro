package com.fintrackpro.infrastructure.adapter.output.persistence.repository;

import com.fintrackpro.domain.valueobject.TransactionType;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface JpaTransactionRepository
                extends JpaRepository<TransactionEntity, Long>, JpaSpecificationExecutor<TransactionEntity> {

        List<TransactionEntity> findByUserIdAndDeletedFalseOrderByTransactionDateDesc(Long userId);

        List<TransactionEntity> findByUserIdAndWalletIdAndDeletedFalseOrderByTransactionDateDesc(
                        Long userId, Long walletId);

        List<TransactionEntity> findByUserIdAndCategoryIdAndDeletedFalseOrderByTransactionDateDesc(
                        Long userId, Long categoryId);

        List<TransactionEntity> findByUserIdAndTypeAndDeletedFalseOrderByTransactionDateDesc(
                        Long userId, TransactionType type);

        @Query("SELECT t FROM TransactionEntity t WHERE t.user.id = :userId " +
                        "AND t.transactionDate BETWEEN :startDate AND :endDate " +
                        "AND t.deleted = false ORDER BY t.transactionDate DESC")
        List<TransactionEntity> findByUserIdAndDateRange(
                        @Param("userId") Long userId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT t FROM TransactionEntity t WHERE t.user.id = :userId " +
                        "AND t.deleted = false ORDER BY t.transactionDate DESC, t.createdAt DESC")
        List<TransactionEntity> findRecentByUserId(@Param("userId") Long userId,
                        org.springframework.data.domain.Pageable pageable);

        @Query("SELECT t FROM TransactionEntity t WHERE t.user.id = :userId " +
                        "AND (LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "OR LOWER(t.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
                        "AND t.deleted = false ORDER BY t.transactionDate DESC")
        List<TransactionEntity> searchByDescription(
                        @Param("userId") Long userId,
                        @Param("searchTerm") String searchTerm);

        boolean existsByCategoryIdAndDeletedFalse(Long categoryId);

        boolean existsByWalletIdAndDeletedFalse(Long walletId);

        long countByUserIdAndDeletedFalse(Long userId);

        long countByWalletIdAndDeletedFalse(Long walletId);

        @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t " +
                        "WHERE t.user.id = :userId AND t.type = :type " +
                        "AND t.transactionDate BETWEEN :startDate AND :endDate " +
                        "AND t.deleted = false")
        BigDecimal sumAmountByUserIdAndTypeAndDateRange(
                        @Param("userId") Long userId,
                        @Param("type") TransactionType type,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT t.category.id, t.category.name, t.category.color, SUM(t.amount), COUNT(t) " +
                        "FROM TransactionEntity t " +
                        "WHERE t.user.id = :userId AND t.type = :type AND t.transactionDate BETWEEN :startDate AND :endDate AND t.deleted = false "
                        +
                        "GROUP BY t.category.id, t.category.name, t.category.color " +
                        "ORDER BY SUM(t.amount) DESC")
        List<Object[]> sumAmountByUserIdAndTypeGroupByCategory(
                        @Param("userId") Long userId,
                        @Param("type") TransactionType type,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT t.transactionDate, SUM(t.amount) " +
                        "FROM TransactionEntity t " +
                        "WHERE t.user.id = :userId AND t.type = :type AND t.transactionDate BETWEEN :startDate AND :endDate AND t.deleted = false "
                        +
                        "GROUP BY t.transactionDate " +
                        "ORDER BY t.transactionDate ASC")
        List<Object[]> sumAmountByUserIdAndTypeGroupByDate(
                        @Param("userId") Long userId,
                        @Param("type") TransactionType type,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT FUNCTION('DAYNAME', t.transactionDate) as day, SUM(t.amount), COUNT(t) " +
                        "FROM TransactionEntity t " +
                        "WHERE t.user.id = :userId AND t.type = :type AND t.transactionDate BETWEEN :startDate AND :endDate AND t.deleted = false "
                        +
                        "GROUP BY day " +
                        "ORDER BY SUM(t.amount) DESC")
        List<Object[]> sumAmountByUserIdAndTypeGroupByDayOfWeek(
                        @Param("userId") Long userId,
                        @Param("type") TransactionType type,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT COUNT(t) FROM TransactionEntity t WHERE t.user.id = :userId AND t.transactionDate BETWEEN :startDate AND :endDate AND t.deleted = false")
        Long countByUserIdAndDateRange(
                        @Param("userId") Long userId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);
}
