package com.fintrackpro.application.port.input;

import com.fintrackpro.domain.model.Wallet;
import com.fintrackpro.domain.valueobject.WalletType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Primary port for wallet-related operations.
 * Defines the core business operations that can be performed on wallets.
 */
public interface WalletUseCase {

    /**
     * Creates a new wallet.
     *
     * @param wallet the wallet to create
     * @return the created wallet with generated ID and timestamps
     */
    Wallet createWallet(Wallet wallet);

    /**
     * Retrieves a wallet by its ID.
     *
     * @param id the wallet ID
     * @return the wallet if found, empty otherwise
     */
    Optional<Wallet> getWalletById(Long id);

    /**
     * Retrieves all wallets for a specific user.
     *
     * @param userId the user ID
     * @return list of user's wallets
     */
    List<Wallet> getUserWallets(Long userId);

    /**
     * Updates an existing wallet.
     *
     * @param wallet the wallet with updated information
     * @return the updated wallet
     */
    Wallet updateWallet(Wallet wallet);

    /**
     * Deletes a wallet by its ID (soft delete).
     *
     * @param id the wallet ID to delete
     */
    void deleteWallet(Long id);

    /**
     * Updates the wallet's balance by a specific amount.
     *
     * @param walletId the wallet ID
     * @param amount   the amount to add (positive) or subtract (negative)
     * @return the updated wallet
     */
    Wallet updateWalletBalance(Long walletId, BigDecimal amount);

    /**
     * Transfers an amount between two wallets.
     *
     * @param sourceWalletId the source wallet ID
     * @param targetWalletId the target wallet ID
     * @param amount        the amount to transfer
     * @param description   optional description of the transfer
     * @return array containing updated source and target wallets
     */
    Wallet[] transferBetweenWallets(Long sourceWalletId, Long targetWalletId, BigDecimal amount, String description);

    /**
     * Retrieves wallets by type for a specific user.
     *
     * @param userId the user ID
     * @param type   the wallet type
     * @return list of matching wallets
     */
    List<Wallet> getWalletsByType(Long userId, WalletType type);

    /**
     * Toggles the default status of a wallet.
     * Only one wallet can be default per user.
     *
     * @param walletId the wallet ID to update
     * @param isDefault whether to set as default
     * @return the updated wallet
     */
    Wallet setDefaultWallet(Long walletId, boolean isDefault);

    /**
     * Gets the total balance across all active wallets for a user.
     *
     * @param userId the user ID
     * @return the total balance
     */
    BigDecimal getTotalBalance(Long userId);
}
