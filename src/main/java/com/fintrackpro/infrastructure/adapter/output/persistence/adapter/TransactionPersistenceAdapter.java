package com.fintrackpro.infrastructure.adapter.output.persistence.adapter;

import com.fintrackpro.domain.model.Transaction;
import com.fintrackpro.domain.model.TransactionFilter;
import com.fintrackpro.domain.port.output.TransactionRepositoryPort;
import com.fintrackpro.domain.valueobject.TransactionType;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.TransactionEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.repository.JpaTransactionRepository;
import com.fintrackpro.infrastructure.adapter.output.persistence.repository.TransactionSpecification;
import com.fintrackpro.infrastructure.mapper.TransactionPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TransactionPersistenceAdapter implements TransactionRepositoryPort {

    private final JpaTransactionRepository transactionRepository;
    private final TransactionPersistenceMapper transactionMapper;

    @Override
    public Page<Transaction> findAll(Long userId, TransactionFilter filter, Pageable pageable) {
        return transactionRepository.findAll(TransactionSpecification.withFilter(userId, filter), pageable)
                .map(transactionMapper::toDomain);
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = transactionMapper.toEntity(transaction);
        TransactionEntity savedEntity = transactionRepository.save(entity);
        return transactionMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toDomain);
    }

    @Override
    public List<Transaction> findAllByUserId(Long userId) {
        return transactionRepository.findByUserIdAndDeletedFalseOrderByTransactionDateDesc(userId)
                .stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByUserIdAndWalletId(Long userId, Long walletId) {
        return transactionRepository.findByUserIdAndWalletIdAndDeletedFalseOrderByTransactionDateDesc(userId, walletId)
                .stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId) {
        return transactionRepository
                .findByUserIdAndCategoryIdAndDeletedFalseOrderByTransactionDateDesc(userId, categoryId)
                .stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByUserIdAndType(Long userId, TransactionType type) {
        return transactionRepository.findByUserIdAndTypeAndDeletedFalseOrderByTransactionDateDesc(userId, type)
                .stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByUserIdAndDateRange(userId, startDate, endDate)
                .stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findRecentByUserId(Long userId, int limit) {
        return transactionRepository.findRecentByUserId(userId, PageRequest.of(0, limit))
                .stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> searchByDescription(Long userId, String searchTerm) {
        return transactionRepository.searchByDescription(userId, searchTerm)
                .stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCategoryId(Long categoryId) {
        return transactionRepository.existsByCategoryIdAndDeletedFalse(categoryId);
    }

    @Override
    public boolean existsByWalletId(Long walletId) {
        return transactionRepository.existsByWalletIdAndDeletedFalse(walletId);
    }

    @Override
    public long countByUserId(Long userId) {
        return transactionRepository.countByUserIdAndDeletedFalse(userId);
    }

    @Override
    public long countByWalletId(Long walletId) {
        return transactionRepository.countByWalletIdAndDeletedFalse(walletId);
    }

    @Override
    public BigDecimal sumAmountByUserIdAndTypeAndDateRange(Long userId, TransactionType type, LocalDate startDate,
            LocalDate endDate) {
        return transactionRepository.sumAmountByUserIdAndTypeAndDateRange(userId, type, startDate, endDate);
    }

    @Override
    public List<Object[]> sumAmountByUserIdAndTypeGroupByDate(Long userId, TransactionType type, LocalDate startDate,
            LocalDate endDate) {
        return transactionRepository.sumAmountByUserIdAndTypeGroupByDate(userId, type, startDate, endDate);
    }

    @Override
    public List<Object[]> sumAmountByUserIdAndTypeGroupByCategory(Long userId, TransactionType type,
            LocalDate startDate, LocalDate endDate) {
        return transactionRepository.sumAmountByUserIdAndTypeGroupByCategory(userId, type, startDate, endDate);
    }

    @Override
    public List<Object[]> sumAmountByUserIdAndTypeGroupByDayOfWeek(Long userId, TransactionType type,
            LocalDate startDate, LocalDate endDate) {
        return transactionRepository.sumAmountByUserIdAndTypeGroupByDayOfWeek(userId, type, startDate, endDate);
    }

    @Override
    public Long countByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.countByUserIdAndDateRange(userId, startDate, endDate);
    }

    @Override
    public void deleteById(Long id) {
        transactionRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Long id) {
        transactionRepository.findById(id).ifPresent(entity -> {
            entity.setDeleted(true);
            entity.setDeletedAt(java.time.LocalDateTime.now());
            transactionRepository.save(entity);
        });
    }
}
