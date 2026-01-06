package com.fintrackpro.application.service;

import com.fintrackpro.application.port.input.WalletUseCase;
import com.fintrackpro.domain.exception.InvalidRequestException;
import com.fintrackpro.domain.model.Wallet;
import com.fintrackpro.domain.port.output.WalletRepositoryPort;
import com.fintrackpro.domain.valueobject.WalletType;
import com.fintrackpro.infrastructure.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service implementation for wallet operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService implements WalletUseCase {

    private final WalletRepositoryPort walletRepositoryPort;
    private static final String WALLET_NOT_FOUND = "Wallet not found with id: ";
    private final MessageUtil messageUtil;


    @Override
    @Transactional
    public Wallet createWallet(Wallet wallet) {
        // 1. Validate input
        validateWalletRequest(wallet);

        Long userId = wallet.userId();
        log.info("Creating new wallet '{}' for user: {}", wallet.name(), userId);

        // 2. Get existing wallets (single DB call)
        List<Wallet> existingWallets = walletRepositoryPort.findByUserId(userId);

        // 3. Validate uniqueness
        validateUniqueWalletName(wallet.name(), existingWallets);

        // 4. Handle default wallet logic
        boolean isFirstWallet = existingWallets.isEmpty();
        boolean shouldBeDefault = isFirstWallet || wallet.isDefault();

        if (shouldBeDefault && !isFirstWallet) {
            unsetExistingDefaultWallet(existingWallets);
        }

        // 5. Build and save wallet
        Wallet walletToCreate = prepareWalletForCreation(wallet, shouldBeDefault);
        Wallet createdWallet = walletRepositoryPort.save(walletToCreate);

        // 6. Log success
        log.info("Successfully created wallet '{}' (ID: {}) for user {}. Is default: {}",
                createdWallet.name(), createdWallet.id(), userId, createdWallet.isDefault());

        return createdWallet;
    }

    /**
     * Prepares the wallet for creation with all required fields
     */
    private Wallet prepareWalletForCreation(Wallet wallet, boolean shouldBeDefault) {
        LocalDateTime now = LocalDateTime.now();

        return wallet.toBuilder()
                .name(wallet.name())
                .createdAt(now)
                .updatedAt(now)
                .isActive(true)
                .deleted(false)
                .color(wallet.color())
                .initialBalance(wallet.initialBalance())
                .currency(wallet.currency())
                .currentBalance(wallet.currentBalance())
                .creditLimit(wallet.creditLimit())
                .accountNumber(wallet.accountNumber())
                .accountType(wallet.accountType())
                .walletType(wallet.walletType())
                .notes(wallet.notes())
                .description(wallet.description())
                .displayOrder(wallet.displayOrder())
                .institutionName(wallet.institutionName())
                .icon(wallet.icon())
                .isDefault(shouldBeDefault)
                .userId(wallet.userId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Wallet> getWalletById(Long id) {
        log.debug("Fetching wallet with id: {}", id);
        return walletRepositoryPort.findById(id)
                .filter(wallet -> !wallet.deleted());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Wallet> getUserWallets(Long userId) {
        log.debug("Fetching all wallets for user: {}", userId);
        return walletRepositoryPort.findByUserId(userId).stream()
                .filter(wallet -> !wallet.deleted())
                .toList();
    }

    @Override
    @Transactional
    public Wallet updateWallet(Wallet wallet) {
        log.info("Updating wallet with id: {}", wallet.id());
        
        Wallet existingWallet = walletRepositoryPort.findById(wallet.id())
                .filter(w -> !w.deleted())
                .orElseThrow(() -> new InvalidRequestException(WALLET_NOT_FOUND + wallet.id()));

        // If changing name, check for duplicates
        if (!existingWallet.name().equals(wallet.name()) && 
            walletRepositoryPort.existsByUserIdAndName(wallet.userId(), wallet.name())) {
            throw new InvalidRequestException("Wallet with name " + wallet.name() + " already exists");
        }

        // If setting as default, unset any existing default wallet
        if (wallet.isDefault() && !existingWallet.isDefault()) {
            walletRepositoryPort.findDefaultByUserId(wallet.userId())
                    .ifPresent(defaultWallet -> 
                            walletRepositoryPort.updateDefaultStatus(defaultWallet.id(), false));
        }

        Wallet updatedWallet = wallet.toBuilder()
                .updatedAt(LocalDateTime.now())
                .build();

        return walletRepositoryPort.save(updatedWallet);
    }

    @Override
    @Transactional
    public void deleteWallet(Long id) {
        log.info("Deleting wallet with id: {}", id);
        Wallet wallet = walletRepositoryPort.findById(id)
                .filter(w -> !w.deleted())
                .orElseThrow(() -> new InvalidRequestException(WALLET_NOT_FOUND + id));

        // Soft delete
        Wallet deletedWallet = wallet.toBuilder()
                .deleted(true)
                .deletedAt(LocalDateTime.now())
                .isActive(false)
                .updatedAt(LocalDateTime.now())
                .build();

        walletRepositoryPort.save(deletedWallet);
    }

    @Override
    @Transactional
    public Wallet updateWalletBalance(Long walletId, BigDecimal amount) {
        log.info("Updating balance for wallet: {} by amount: {}", walletId, amount);
        
        Wallet wallet = walletRepositoryPort.findById(walletId)
                .filter(w -> !w.deleted())
                .orElseThrow(() -> new InvalidRequestException(WALLET_NOT_FOUND + walletId));

        // For credit cards, we allow negative balances up to the credit limit
        BigDecimal newBalance = wallet.currentBalance().add(amount);
        if (wallet.walletType() != WalletType.CREDIT_CARD && newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRequestException(messageUtil.getMessage("error.wallet.insufficientBalance") + wallet.name());
        }
        if (wallet.walletType() == WalletType.CREDIT_CARD && wallet.creditLimit() != null && 
            newBalance.negate().compareTo(wallet.creditLimit()) > 0) {
            throw new InvalidRequestException("Credit limit exceeded for wallet: " + walletId);
        }

        return walletRepositoryPort.updateBalance(walletId, newBalance)
                .orElseThrow(() -> new InvalidRequestException(WALLET_NOT_FOUND + walletId));
    }

    @Override
    @Transactional
    public Wallet[] transferBetweenWallets(Long sourceWalletId, Long targetWalletId, BigDecimal amount, String description) {
        log.info("Transferring {} from wallet {} to {}", amount, sourceWalletId, targetWalletId);
        
        if (sourceWalletId.equals(targetWalletId)) {
            throw new InvalidRequestException("Source and target wallets cannot be the same");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRequestException("Transfer amount must be positive");
        }

        Wallet source = walletRepositoryPort.findById(sourceWalletId)
                .filter(w -> !w.deleted())
                .orElseThrow(() -> new InvalidRequestException(WALLET_NOT_FOUND + sourceWalletId));

        Wallet target = walletRepositoryPort.findById(targetWalletId)
                .filter(w -> !w.deleted())
                .orElseThrow(() -> new InvalidRequestException(WALLET_NOT_FOUND + targetWalletId));

        // Check if source has sufficient balance
        if (source.currentBalance().compareTo(amount) < 0) {
            throw new InvalidRequestException(messageUtil.getMessage("error.wallet.insufficientBalance") + sourceWalletId);
        }

        // Update balances
        Wallet updatedSource = walletRepositoryPort.updateBalance(
                source.id(), 
                source.currentBalance().subtract(amount)
        ).orElseThrow(() -> new InvalidRequestException(WALLET_NOT_FOUND + sourceWalletId));

        Wallet updatedTarget = walletRepositoryPort.updateBalance(
                target.id(), 
                target.currentBalance().add(amount)
        ).orElseThrow(() -> new InvalidRequestException(WALLET_NOT_FOUND + targetWalletId));

        return new Wallet[]{updatedSource, updatedTarget};
    }

    @Override
    @Transactional(readOnly = true)
    public List<Wallet> getWalletsByType(Long userId, WalletType type) {
        log.debug("Fetching {} wallets for user: {}", type, userId);
        return walletRepositoryPort.findByUserIdAndType(userId, type).stream()
                .filter(wallet -> !wallet.deleted())
                .toList();
    }

    @Override
    @Transactional
    public Wallet setDefaultWallet(Long walletId, boolean isDefault) {
        log.info("Setting wallet {} as default: {}", walletId, isDefault);
        
        Wallet wallet = walletRepositoryPort.findById(walletId)
                .filter(w -> !w.deleted())
                .orElseThrow(() -> new InvalidRequestException(WALLET_NOT_FOUND + walletId));

        // If setting as default, unset any existing default wallet
        if (isDefault) {
            walletRepositoryPort.findDefaultByUserId(wallet.userId())
                    .filter(defaultWallet -> !defaultWallet.id().equals(walletId))
                    .ifPresent(defaultWallet -> 
                            walletRepositoryPort.updateDefaultStatus(defaultWallet.id(), false));
        }

        return walletRepositoryPort.updateDefaultStatus(walletId, isDefault)
                .orElseThrow(() -> new InvalidRequestException(WALLET_NOT_FOUND + walletId));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalBalance(Long userId) {
        log.debug("Calculating total balance for user: {}", userId);
        return walletRepositoryPort.findByUserId(userId).stream()
                .filter(wallet -> !wallet.deleted() && wallet.isActive() && !wallet.isExcludedFromTotal())
                .map(Wallet::currentBalance)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    /**
     * Validates the wallet request
     */
    private void validateWalletRequest(Wallet wallet) {
        Objects.requireNonNull(wallet, "Wallet cannot be null");

        if (wallet.userId() == null) {
            throw new InvalidRequestException("User ID must be provided");
        }

        if (wallet.name() == null || wallet.name().trim().isEmpty()) {
            throw new InvalidRequestException("Wallet name is required");
        }
    }

    /**
     * Validates that wallet name is unique for the user
     */
    private void validateUniqueWalletName(String walletName, List<Wallet> existingWallets) {
        boolean nameExists = existingWallets.stream()
                .anyMatch(w -> w.name().equalsIgnoreCase(walletName));

        if (nameExists) {
            throw new InvalidRequestException(
                    messageUtil.getMessage("error.wallet.exitsWithName") + walletName
            );
        }
    }

    /**
     * Determines if the wallet should be set as default
     * - First wallet is always default
     * - Otherwise, respects user's choice
     */
    private boolean determineDefaultStatus(Wallet wallet, List<Wallet> existingWallets) {
        if (existingWallets.isEmpty()) {
            log.info("First wallet for user. Setting as default automatically.");
            return true;
        }

        return wallet.isDefault();
    }

    /**
     * Unsets the existing default wallet if one exists
     */
    private void unsetExistingDefaultWallet(List<Wallet> existingWallets) {
        existingWallets.stream()
                .filter(Wallet::isDefault)
                .findFirst()
                .ifPresent(existingDefault -> {
                    log.info("Removing default status from wallet '{}' (ID: {})",
                            existingDefault.name(), existingDefault.id());

                    Wallet updatedWallet = existingDefault.removeDefaultStatus();
                    // Use repository directly to avoid self-invocation transaction issue
                    walletRepositoryPort.save(updatedWallet);
                });
    }

    /**
     * Builds the wallet with all system-managed fields and validations
     */
    private Wallet buildWalletForCreation(Wallet wallet, boolean shouldBeDefault) {
        return wallet.toBuilder()
                // Audit fields
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())

                // Status fields
                .isActive(true)
                .deleted(false)
                .isDefault(shouldBeDefault)

                // Financial fields (preserve or use defaults from record constructor)
                .initialBalance(wallet.initialBalance())
                .currentBalance(wallet.currentBalance())
                .creditLimit(wallet.creditLimit())
                .currency(wallet.currency())

                // Customization (preserve or use defaults from record constructor)
                .color(wallet.color())
                .icon(wallet.icon())
                .displayOrder(wallet.displayOrder())

                // Bank-specific fields
                .bankName(wallet.bankName())
                .accountNumber(wallet.accountNumber())
                .accountType(wallet.accountType())

                // Additional metadata
                .notes(wallet.notes())

                .build();
    }
}
