-- Migration: Create wallets table
-- Description: Creates the wallets table with all necessary columns, constraints, and indexes
-- Author: FinTrackPro
-- Date: 2025-11-10

-- Create wallets table
CREATE TABLE IF NOT EXISTS wallets (
    -- Primary Key
                                       id BIGSERIAL PRIMARY KEY,

    -- Foreign Keys
                                       user_id BIGINT NOT NULL,
                                       created_by BIGINT,
                                       updated_by BIGINT,

    -- Basic Information
                                       name VARCHAR(100) NOT NULL,
    description TEXT,
    wallet_type VARCHAR(50) NOT NULL,

    -- Financial Details
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    initial_balance DECIMAL(19, 4) NOT NULL DEFAULT 0.00,
    current_balance DECIMAL(19, 4) NOT NULL DEFAULT 0.00,
    credit_limit DECIMAL(19, 4),

    -- Customization
    color VARCHAR(7) DEFAULT '#4A90E2',
    icon VARCHAR(50) DEFAULT 'wallet',

    -- Status and Settings
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_excluded_from_total BOOLEAN NOT NULL DEFAULT FALSE,

    -- Bank Account Specific
    bank_name VARCHAR(100),
    account_number VARCHAR(50),
    account_type VARCHAR(50),

    -- Investment Specific
    investment_type VARCHAR(50),
    institution_name VARCHAR(100),

    -- Metadata
    display_order INTEGER DEFAULT 0,
    notes TEXT,

    -- Transaction Statistics
    transaction_count INTEGER DEFAULT 0,
    last_transaction_date TIMESTAMP,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0,

    -- Soft Delete
    deleted_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    -- Constraints
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id)
    REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_wallet_created_by FOREIGN KEY (created_by)
    REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_wallet_updated_by FOREIGN KEY (updated_by)
    REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_wallet_currency_length CHECK (LENGTH(currency) = 3),
    CONSTRAINT chk_wallet_initial_balance CHECK (initial_balance >= 0),
    CONSTRAINT chk_wallet_credit_limit CHECK (credit_limit IS NULL OR credit_limit >= 0),
    CONSTRAINT chk_wallet_color_format CHECK (color ~* '^#[0-9A-F]{6}$'),
    CONSTRAINT chk_wallet_display_order CHECK (display_order >= 0),
    CONSTRAINT chk_wallet_transaction_count CHECK (transaction_count >= 0)
    );

-- Create indexes for better query performance
CREATE INDEX idx_wallets_user_id ON wallets(user_id);
CREATE INDEX idx_wallets_wallet_type ON wallets(wallet_type);
CREATE INDEX idx_wallets_is_active ON wallets(is_active);
CREATE INDEX idx_wallets_is_default ON wallets(is_default);
CREATE INDEX idx_wallets_deleted ON wallets(deleted);
CREATE INDEX idx_wallets_user_active ON wallets(user_id, is_active) WHERE deleted = FALSE;
CREATE INDEX idx_wallets_user_type ON wallets(user_id, wallet_type) WHERE deleted = FALSE;
CREATE INDEX idx_wallets_created_at ON wallets(created_at);
CREATE INDEX idx_wallets_last_transaction_date ON wallets(last_transaction_date);

-- Create composite index for common queries
CREATE INDEX idx_wallets_user_active_default ON wallets(user_id, is_active, is_default)
    WHERE deleted = FALSE;

-- Create partial index for active non-deleted wallets
CREATE INDEX idx_wallets_active_non_deleted ON wallets(user_id, wallet_type, current_balance)
    WHERE is_active = TRUE AND deleted = FALSE;

-- Add comments to table and columns
COMMENT ON TABLE wallets IS 'Stores user wallet information including bank accounts, cash, credit cards, and investments';
COMMENT ON COLUMN wallets.id IS 'Primary key - auto-incrementing wallet identifier';
COMMENT ON COLUMN wallets.user_id IS 'Foreign key to users table';
COMMENT ON COLUMN wallets.name IS 'User-defined name for the wallet';
COMMENT ON COLUMN wallets.description IS 'Optional description of the wallet';
COMMENT ON COLUMN wallets.wallet_type IS 'Type of wallet (CASH, BANK, CREDIT_CARD, INVESTMENT, etc.)';
COMMENT ON COLUMN wallets.currency IS 'ISO 4217 three-letter currency code';
COMMENT ON COLUMN wallets.initial_balance IS 'Starting balance when wallet was created';
COMMENT ON COLUMN wallets.current_balance IS 'Current balance of the wallet';
COMMENT ON COLUMN wallets.credit_limit IS 'Credit limit for credit card wallets';
COMMENT ON COLUMN wallets.color IS 'Hex color code for UI display';
COMMENT ON COLUMN wallets.icon IS 'Icon identifier for UI display';
COMMENT ON COLUMN wallets.is_active IS 'Whether the wallet is currently active';
COMMENT ON COLUMN wallets.is_default IS 'Whether this is the default wallet for the user';
COMMENT ON COLUMN wallets.is_excluded_from_total IS 'Whether to exclude from total balance calculations';
COMMENT ON COLUMN wallets.bank_name IS 'Name of the bank (for bank account wallets)';
COMMENT ON COLUMN wallets.account_number IS 'Bank account number';
COMMENT ON COLUMN wallets.account_type IS 'Type of bank account (SAVINGS, CHECKING, etc.)';
COMMENT ON COLUMN wallets.investment_type IS 'Type of investment (STOCKS, BONDS, MUTUAL_FUNDS, etc.)';
COMMENT ON COLUMN wallets.institution_name IS 'Name of investment institution';
COMMENT ON COLUMN wallets.display_order IS 'Order for displaying wallets in UI';
COMMENT ON COLUMN wallets.notes IS 'Additional notes about the wallet';
COMMENT ON COLUMN wallets.transaction_count IS 'Total number of transactions for this wallet';
COMMENT ON COLUMN wallets.last_transaction_date IS 'Date of the most recent transaction';
COMMENT ON COLUMN wallets.created_at IS 'Timestamp when wallet was created';
COMMENT ON COLUMN wallets.updated_at IS 'Timestamp when wallet was last updated';
COMMENT ON COLUMN wallets.created_by IS 'User who created the wallet';
COMMENT ON COLUMN wallets.updated_by IS 'User who last updated the wallet';
COMMENT ON COLUMN wallets.version IS 'Optimistic locking version number';
COMMENT ON COLUMN wallets.deleted_at IS 'Timestamp when wallet was soft deleted';
COMMENT ON COLUMN wallets.deleted IS 'Soft delete flag';