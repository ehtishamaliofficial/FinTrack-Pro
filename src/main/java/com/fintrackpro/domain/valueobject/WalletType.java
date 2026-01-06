package com.fintrackpro.domain.valueobject;

/**
 * Represents the type of wallet in the FinTrack Pro application.
 * This enum defines the various types of wallets that can be created by users.
 */
public enum WalletType {
    /**
     * Physical cash wallet
     */
    CASH,
    
    /**
     * Traditional bank account
     */
    BANK_ACCOUNT,
    
    /**
     * Credit card account
     */
    CREDIT_CARD,
    
    /**
     * Investment account (stocks, bonds, etc.)
     */
    INVESTMENT,
    
    /**
     * Savings account
     */
    SAVINGS,
    
    /**
     * Digital wallet (PayPal, Venmo, etc.)
     */
    DIGITAL_WALLET,
    
    /**
     * Any other type of wallet not covered by the above
     */
    OTHER;
    
    /**
     * Checks if this wallet type is a credit card
     * @return true if the wallet type is CREDIT_CARD, false otherwise
     */
    public boolean isCreditCard() {
        return this == CREDIT_CARD;
    }
    
    /**
     * Checks if this wallet type is a bank account
     * @return true if the wallet type is BANK_ACCOUNT, false otherwise
     */
    public boolean isBankAccount() {
        return this == BANK_ACCOUNT;
    }
    
    /**
     * Checks if this wallet type is an investment account
     * @return true if the wallet type is INVESTMENT, false otherwise
     */
    public boolean isInvestment() {
        return this == INVESTMENT;
    }
    
    /**
     * Gets a user-friendly display name for the wallet type
     * @return A formatted string representation of the wallet type
     */
    public String getDisplayName() {
        return name().charAt(0) + name().substring(1).toLowerCase().replace("_", " ");
    }
}
