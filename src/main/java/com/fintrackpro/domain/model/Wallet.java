package com.fintrackpro.domain.model;

import com.fintrackpro.domain.valueobject.WalletType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a wallet in the FinTrack Pro application.
 * A wallet can be of various types (CASH, BANK_ACCOUNT, CREDIT_CARD, etc.)
 * and contains financial information and settings.
 */
@Builder(toBuilder = true)
public record Wallet(
        Long id,
        Long userId,
        
        // Basic Information
        String name,
        String description,
        WalletType walletType,
        
        // Financial Details
        String currency,
        BigDecimal initialBalance,
        BigDecimal currentBalance,
        BigDecimal creditLimit,

        // Customization
        String color,
        String icon,

        // Status and Settings
        boolean isActive,
        boolean isDefault,
        boolean isExcludedFromTotal,

        // Bank Account Specific
        String bankName,
        String accountNumber,
        String accountType,

        // Investment Specific
        String investmentType,
        String institutionName,

        // Metadata
        Integer displayOrder,
        String notes,

        // Transaction Statistics
        Integer transactionCount,
        LocalDateTime lastTransactionDate,

        // Audit Fields
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdBy,
        Long updatedBy,

        // Soft delete
        LocalDateTime deletedAt,
        boolean deleted
) {
    // Compact constructor with defaults and validation
    public Wallet {
        // Set default values
        initialBalance = initialBalance != null ? initialBalance : BigDecimal.ZERO;
        currentBalance = currentBalance != null ? currentBalance : initialBalance;
        currency = currency != null && !currency.trim().isEmpty() ? currency : "USD";
        color = color != null && !color.trim().isEmpty() ? color : "#4A90E2";
        icon = icon != null && !icon.trim().isEmpty() ? icon : "wallet";
        transactionCount = transactionCount != null ? transactionCount : 0;
        displayOrder = displayOrder != null ? displayOrder : 0;


        // Validate based on wallet type
        if (walletType == WalletType.CREDIT_CARD) {
            creditLimit = creditLimit != null && creditLimit.compareTo(BigDecimal.ZERO) >= 0 ?
                creditLimit : BigDecimal.ZERO;
            // Allow negative balance for credit cards
        } else {
            creditLimit = null;
            // Ensure balance is not negative for non-credit cards
            if (currentBalance != null && currentBalance.compareTo(BigDecimal.ZERO) < 0) {
                currentBalance = BigDecimal.ZERO;
            }
        }

        // Update active status based on deleted flag
        isActive = !deleted;
    }

    // Factory method for creating a new default wallet
    public static Wallet createDefault(Long userId, String name, WalletType walletType) {
        return Wallet.builder()
                .userId(userId)
                .name(name)
                .walletType(walletType)
                .isDefault(true)
                .isActive(true)
                .initialBalance(BigDecimal.ZERO)
                .currentBalance(BigDecimal.ZERO)
                .currency("USD")
                .color("#4A90E2")
                .icon("wallet")
                .isExcludedFromTotal(false)
                .transactionCount(0)
                .displayOrder(0)
                .build();
    }

    // Update wallet balance with a new transaction
    public Wallet addTransaction(BigDecimal amount, LocalDateTime transactionDate) {
        BigDecimal newBalance = currentBalance.add(amount);

        // For non-credit cards, ensure balance doesn't go negative
        if (walletType != WalletType.CREDIT_CARD && newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Insufficient funds for this transaction");
        }

        return this.toBuilder()
                .currentBalance(newBalance)
                .transactionCount(transactionCount + 1)
                .lastTransactionDate(transactionDate)
                .build();
    }

    // Update wallet details
    public Wallet updateDetails(String name, String description, String color, String icon) {
        return this.toBuilder()
                .name(name != null ? name : this.name)
                .description(description != null ? description : this.description)
                .color(color != null ? color : this.color)
                .icon(icon != null ? icon : this.icon)
                .build();
    }

    // Set as default wallet
    public Wallet setAsDefault() {
        return this.toBuilder()
                .isDefault(true)
                .build();
    }

    // Remove default status
    public Wallet removeDefaultStatus() {
        return this.toBuilder()
                .isDefault(false)
                .build();
    }

    // Enable wallet
    public Wallet enable() {
        return this.toBuilder()
                .isActive(true)
                .deleted(false)
                .deletedAt(null)
                .build();
    }

    // Disable wallet (soft delete)
    public Wallet disable() {
        return this.toBuilder()
                .isActive(false)
                .deleted(true)
                .deletedAt(LocalDateTime.now())
                .build();
    }

    // Update credit limit (only for credit cards)
    public Wallet updateCreditLimit(BigDecimal newLimit) {
        if (walletType != WalletType.CREDIT_CARD) {
            throw new IllegalStateException("Only credit card wallets can have a credit limit");
        }
        return this.toBuilder()
                .creditLimit(newLimit != null && newLimit.compareTo(BigDecimal.ZERO) >= 0 ?
                    newLimit : BigDecimal.ZERO)
                .build();
    }

    // Include/Exclude from total calculations
    public Wallet setIncludedInTotal(boolean include) {
        return this.toBuilder()
                .isExcludedFromTotal(!include)
                .build();
    }

    // Update display order
    public Wallet updateDisplayOrder(int order) {
        return this.toBuilder()
                .displayOrder(order)
                .build();
    }

    // Update bank account details
    public Wallet updateBankDetails(String bankName, String accountNumber, String accountType) {
        if (walletType != WalletType.BANK_ACCOUNT) {
            throw new IllegalStateException("Only bank account wallets can have bank details");
        }
        return this.toBuilder()
                .bankName(bankName)
                .accountNumber(accountNumber)
                .accountType(accountType)
                .build();
    }

    // Update investment details
    public Wallet updateInvestmentDetails(String investmentType, String institutionName) {
        if (walletType != WalletType.INVESTMENT) {
            throw new IllegalStateException("Only investment wallets can have investment details");
        }
        return this.toBuilder()
                .investmentType(investmentType)
                .institutionName(institutionName)
                .build();
    }

    // Create a new wallet with an updated balance
    public Wallet withbalance(BigDecimal newBalance) {
        if (newBalance == null) {
            return this;
        }

        // For non-credit cards, ensure balance is not negative
        if (walletType != WalletType.CREDIT_CARD && newBalance.compareTo(BigDecimal.ZERO) < 0) {
            newBalance = BigDecimal.ZERO;
        }

        return this.toBuilder()
                .currentBalance(newBalance)
                .build();
    }

    // Mark wallet as deleted (soft delete)
    public Wallet markAsDeleted() {
        return this.disable();
    }

    // Validation methods
    public boolean isValidForTransaction(BigDecimal amount) {
        if (amount == null || !isActive || deleted) {
            return false;
        }

        // For credit cards, check against available credit
        if (walletType == WalletType.CREDIT_CARD) {
            BigDecimal availableCredit = creditLimit.add(currentBalance);
            return availableCredit.compareTo(amount) >= 0;
        }

        // For other wallets, just check if balance is sufficient
        return currentBalance.compareTo(amount) >= 0;
    }

    // Helper methods for wallet type checks
    public boolean isCreditCard() {
        return walletType == WalletType.CREDIT_CARD;
    }

    public boolean isBankAccount() {
        return walletType == WalletType.BANK_ACCOUNT;
    }

    public boolean isInvestment() {
        return walletType == WalletType.INVESTMENT;
    }

    public boolean isDigitalWallet() {
        return walletType == WalletType.DIGITAL_WALLET;
    }

    // Formatting helpers
    public String getFormattedBalance() {
        return String.format("%s %,.2f", currency, currentBalance);
    }

    public String getFormattedInitialBalance() {
        return String.format("%s %,.2f", currency, initialBalance);
    }

    public String getFormattedCreditLimit() {
        return creditLimit != null ? String.format("%s %,.2f", currency, creditLimit) : "N/A";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return Objects.equals(id, wallet.id) &&
               Objects.equals(userId, wallet.userId) &&
               Objects.equals(name, wallet.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, name);
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + walletType +
                ", balance=" + getFormattedBalance() +
                ", currency='" + currency + '\'' +
                ", isDefault=" + isDefault +
                ", isActive=" + isActive +
                '}';
    }
}
