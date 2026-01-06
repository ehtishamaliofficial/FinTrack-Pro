package com.fintrackpro.domain.port.output;

import com.fintrackpro.domain.model.Wallet;
import com.fintrackpro.domain.valueobject.WalletType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Port for wallet persistence operations.
 * Defines the contract for wallet data access operations.
 */
public interface WalletRepositoryPort {

    /**
     * Saves a wallet.
     *
     * @param wallet the wallet to save
     * @return the saved wallet
     */
    Wallet save(Wallet wallet);

    /**
     * Finds a wallet by ID.
     *
     * @param id the wallet ID
     * @return an Optional containing the wallet if found, empty otherwise
     */
    Optional<Wallet> findById(Long id);

    /**
     * Finds all wallets for a specific user.
     *
     * @param userId the user ID
     * @return a list of wallets belonging to the user
     */
    List<Wallet> findByUserId(Long userId);

    /**
     * Finds the default wallet for a user.
     *
     * @param userId the user ID
     * @return an Optional containing the default wallet if found, empty otherwise
     */
    Optional<Wallet> findDefaultByUserId(Long userId);

    /**
     * Finds wallets by type for a specific user.
     *
     * @param userId the user ID
     * @param type   the wallet type
     * @return a list of wallets matching the type
     */
    List<Wallet> findByUserIdAndType(Long userId, WalletType type);

    /**
     * Finds all wallets.
     *
     * @return a list of all wallets
     */
    List<Wallet> findAll();

    /**
     * Deletes a wallet by ID.
     *
     * @param id the wallet ID to delete
     */
    void deleteById(Long id);

    /**
     * Checks if a wallet with the given name exists for a user.
     *
     * @param userId the user ID
     * @param name   the wallet name
     * @return true if a wallet with the name exists, false otherwise
     */
    boolean existsByUserIdAndName(Long userId, String name);

    /**
     * Updates the wallet balance.
     *
     * @param id      the wallet ID
     * @param balance the new balance
     * @return the updated wallet
     */
    Optional<Wallet> updateBalance(Long id, BigDecimal balance);

    /**
     * Updates the wallet's default status.
     *
     * @param id         the wallet ID
     * @param isDefault  true to set as default, false otherwise
     * @return the updated wallet
     */
    Optional<Wallet> updateDefaultStatus(Long id, boolean isDefault);

    /**
     * Finds all active wallets for a user.
     *
     * @param userId the user ID
     * @return a list of active wallets
     */
    List<Wallet> findActiveByUserId(Long userId);

    /**
     * Finds wallets included in the total balance calculation for a user.
     *
     * @param userId the user ID
     * @return a list of wallets included in the total balance
     */
    List<Wallet> findIncludedInTotalByUserId(Long userId);

    /**
     * Calculates the total balance across all wallets for a user.
     *
     * @param userId the user ID
     * @return the total balance
     */
    BigDecimal calculateTotalBalance(Long userId);

    /**
     * Transfers an amount between two wallets.
     *
     * @param fromWalletId the source wallet ID
     * @param toWalletId   the target wallet ID
     * @param amount       the amount to transfer
     * @return an array containing the updated source and target wallets
     */
    Optional<Wallet[]> transfer(Long fromWalletId, Long toWalletId, BigDecimal amount);
}
