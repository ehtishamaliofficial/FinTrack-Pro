package com.fintrackpro.application.service;

import com.fintrackpro.application.port.input.TransactionUseCase;
import com.fintrackpro.domain.exception.InvalidRequestException;
import com.fintrackpro.domain.model.Transaction;
import com.fintrackpro.domain.model.TransactionFilter;
import com.fintrackpro.domain.port.output.TransactionRepositoryPort;
import com.fintrackpro.domain.port.output.WalletRepositoryPort;
import com.fintrackpro.domain.valueobject.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for transaction operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService implements TransactionUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;
    private final WalletRepositoryPort walletRepositoryPort;

    private static final String TRANSACTION_NOT_FOUND = "Transaction not found with id: ";

    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        log.info("Creating new transaction of type {} for user: {}",
                transaction.type(), transaction.userId());

        // Validate and get the source wallet
        var wallet = walletRepositoryPort.findById(transaction.walletId())
                .orElseThrow(() -> new InvalidRequestException(
                        "Wallet not found with id: " + transaction.walletId()));

        // For transfers, validate destination wallet
        if (transaction.isTransfer() && transaction.toWalletId() != null) {
            var toWallet = walletRepositoryPort.findById(transaction.toWalletId())
                    .orElseThrow(() -> new InvalidRequestException(
                            "Destination wallet not found with id: " + transaction.toWalletId()));

            // Update destination wallet balance (add the transfer amount)
            var updatedToWallet = toWallet.addTransaction(transaction.amount(), LocalDateTime.now());
            walletRepositoryPort.save(updatedToWallet);
        }

        // Update source wallet balance using the effective amount
        // For INCOME: positive amount (adds to balance)
        // For EXPENSE: negative amount (subtracts from balance)
        // For TRANSFER: negative amount (subtracts from source wallet)
        var updatedWallet = wallet.addTransaction(transaction.getEffectiveAmount(), LocalDateTime.now());
        walletRepositoryPort.save(updatedWallet);

        // Save transaction
        Transaction savedTransaction = transactionRepositoryPort.save(transaction);

        log.info("Successfully created transaction with id: {} and updated wallet balance", savedTransaction.id());
        return savedTransaction;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getFilteredTransactions(Long userId, TransactionFilter filter, Pageable pageable) {
        log.debug("Fetching filtered transactions for user: {}", userId);
        return transactionRepositoryPort.findAll(userId, filter, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> getTransactionById(Long id) {
        log.debug("Fetching transaction with id: {}", id);
        return transactionRepositoryPort.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactions(Long userId) {
        log.debug("Fetching all transactions for user: {}", userId);
        return transactionRepositoryPort.findAllByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getWalletTransactions(Long userId, Long walletId) {
        log.debug("Fetching transactions for wallet: {} and user: {}", walletId, userId);
        return transactionRepositoryPort.findByUserIdAndWalletId(userId, walletId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByCategory(Long userId, Long categoryId) {
        log.debug("Fetching transactions for category: {} and user: {}", categoryId, userId);
        return transactionRepositoryPort.findByUserIdAndCategoryId(userId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByType(Long userId, TransactionType type) {
        log.debug("Fetching {} transactions for user: {}", type, userId);
        return transactionRepositoryPort.findByUserIdAndType(userId, type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching transactions for user: {} between {} and {}", userId, startDate, endDate);
        return transactionRepositoryPort.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getRecentTransactions(Long userId, int limit) {
        log.debug("Fetching {} recent transactions for user: {}", limit, userId);
        return transactionRepositoryPort.findRecentByUserId(userId, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> searchTransactions(Long userId, String searchTerm) {
        log.debug("Searching transactions for user: {} with term: {}", userId, searchTerm);
        return transactionRepositoryPort.searchByDescription(userId, searchTerm);
    }

    @Override
    @Transactional
    public Transaction updateTransaction(Transaction transaction) {
        log.info("Updating transaction with id: {}", transaction.id());

        // Get the existing transaction to reverse its effect on wallet balance
        var existingTransaction = transactionRepositoryPort.findById(transaction.id())
                .orElseThrow(() -> new InvalidRequestException(TRANSACTION_NOT_FOUND + transaction.id()));

        // Reverse the old transaction's effect on the source wallet
        var oldWallet = walletRepositoryPort.findById(existingTransaction.walletId())
                .orElseThrow(() -> new InvalidRequestException(
                        "Wallet not found with id: " + existingTransaction.walletId()));

        // Reverse the effect: negate the effective amount
        var revertedWallet = oldWallet.addTransaction(
                existingTransaction.getEffectiveAmount().negate(), LocalDateTime.now());
        walletRepositoryPort.save(revertedWallet);

        // If old transaction was a transfer, reverse the destination wallet effect
        if (existingTransaction.isTransfer() && existingTransaction.toWalletId() != null) {
            var oldToWallet = walletRepositoryPort.findById(existingTransaction.toWalletId())
                    .orElseThrow(() -> new InvalidRequestException(
                            "Destination wallet not found with id: " + existingTransaction.toWalletId()));
            var revertedToWallet = oldToWallet.addTransaction(
                    existingTransaction.amount().negate(), LocalDateTime.now());
            walletRepositoryPort.save(revertedToWallet);
        }

        // Now apply the new transaction's effect
        // Validate and get the new source wallet
        var newWallet = walletRepositoryPort.findById(transaction.walletId())
                .orElseThrow(() -> new InvalidRequestException(
                        "Wallet not found with id: " + transaction.walletId()));

        // For transfers, validate and update destination wallet
        if (transaction.isTransfer() && transaction.toWalletId() != null) {
            var toWallet = walletRepositoryPort.findById(transaction.toWalletId())
                    .orElseThrow(() -> new InvalidRequestException(
                            "Destination wallet not found with id: " + transaction.toWalletId()));

            // Update destination wallet balance (add the transfer amount)
            var updatedToWallet = toWallet.addTransaction(transaction.amount(), LocalDateTime.now());
            walletRepositoryPort.save(updatedToWallet);
        }

        // Update source wallet balance using the effective amount
        var updatedWallet = newWallet.addTransaction(transaction.getEffectiveAmount(), LocalDateTime.now());
        walletRepositoryPort.save(updatedWallet);

        Transaction updatedTransaction = transactionRepositoryPort.save(transaction);
        log.info("Successfully updated transaction with id: {} and adjusted wallet balances", transaction.id());
        return updatedTransaction;
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        log.info("Deleting transaction with id: {}", id);

        // Get the transaction to reverse its effect on wallet balance
        var transaction = transactionRepositoryPort.findById(id)
                .orElseThrow(() -> new InvalidRequestException(TRANSACTION_NOT_FOUND + id));

        // Reverse the transaction's effect on the source wallet
        var wallet = walletRepositoryPort.findById(transaction.walletId())
                .orElseThrow(() -> new InvalidRequestException(
                        "Wallet not found with id: " + transaction.walletId()));

        // Reverse the effect: negate the effective amount
        var revertedWallet = wallet.addTransaction(
                transaction.getEffectiveAmount().negate(), LocalDateTime.now());
        walletRepositoryPort.save(revertedWallet);

        // If it was a transfer, reverse the destination wallet effect
        if (transaction.isTransfer() && transaction.toWalletId() != null) {
            var toWallet = walletRepositoryPort.findById(transaction.toWalletId())
                    .orElseThrow(() -> new InvalidRequestException(
                            "Destination wallet not found with id: " + transaction.toWalletId()));
            var revertedToWallet = toWallet.addTransaction(
                    transaction.amount().negate(), LocalDateTime.now());
            walletRepositoryPort.save(revertedToWallet);
        }

        transactionRepositoryPort.deleteById(id);
        log.info("Successfully deleted transaction with id: {} and adjusted wallet balances", id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalIncome(Long userId, LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating total income for user: {} between {} and {}", userId, startDate, endDate);
        BigDecimal total = transactionRepositoryPort.sumAmountByUserIdAndTypeAndDateRange(
                userId, TransactionType.INCOME, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalExpenses(Long userId, LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating total expenses for user: {} between {} and {}", userId, startDate, endDate);
        BigDecimal total = transactionRepositoryPort.sumAmountByUserIdAndTypeAndDateRange(
                userId, TransactionType.EXPENSE, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional
    public Transaction transferBetweenWallets(
            Long userId,
            Long fromWalletId,
            Long toWalletId,
            BigDecimal amount,
            String description,
            LocalDate transactionDate) {

        log.info("Creating transfer from wallet {} to wallet {} for user: {}",
                fromWalletId, toWalletId, userId);

        // Validate both wallets exist
        walletRepositoryPort.findById(fromWalletId)
                .orElseThrow(() -> new InvalidRequestException(
                        "Source wallet not found with id: " + fromWalletId));

        walletRepositoryPort.findById(toWalletId)
                .orElseThrow(() -> new InvalidRequestException(
                        "Destination wallet not found with id: " + toWalletId));

        // Create transfer transaction
        Transaction transfer = Transaction.createTransfer(
                userId, fromWalletId, toWalletId, amount, description, transactionDate);

        return transactionRepositoryPort.save(transfer);
    }
}
