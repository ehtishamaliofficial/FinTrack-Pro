-- Migration: Create transactions table
-- Description: Creates the transactions table with all necessary columns, constraints, and indexes
-- Author: FinTrackPro
-- Date: 2025-12-19

CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    wallet_id BIGINT NOT NULL,
    category_id BIGINT,
    to_wallet_id BIGINT,
    type VARCHAR(20) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    transaction_date DATE NOT NULL DEFAULT CURRENT_DATE,
    description VARCHAR(255),
    notes TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    reference_number VARCHAR(100),
    payee VARCHAR(255),
    location VARCHAR(255),
    tags VARCHAR(500),
    receipt_url VARCHAR(500),
    attachment_url VARCHAR(500),
    is_recurring BOOLEAN NOT NULL DEFAULT FALSE,
    recurring_pattern VARCHAR(100),
    recurring_group_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_transaction_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL,
    CONSTRAINT fk_transaction_to_wallet FOREIGN KEY (to_wallet_id) REFERENCES wallets(id) ON DELETE CASCADE,
    CONSTRAINT chk_transaction_amount_positive CHECK (amount >= 0),
    CONSTRAINT chk_transaction_currency_length CHECK (LENGTH(currency) = 3)
);

-- Create indexes for performance
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_wallet_id ON transactions(wallet_id);
CREATE INDEX idx_transactions_category_id ON transactions(category_id);
CREATE INDEX idx_transactions_date ON transactions(transaction_date);
CREATE INDEX idx_transactions_type ON transactions(type);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_deleted ON transactions(deleted);

-- Add comments
COMMENT ON TABLE transactions IS 'Stores all financial transactions (income, expense, and transfers)';
COMMENT ON COLUMN transactions.user_id IS 'Owner of the transaction';
COMMENT ON COLUMN transactions.wallet_id IS 'Wallet associated with the transaction (source for transfers)';
COMMENT ON COLUMN transactions.category_id IS 'Category for income/expense';
COMMENT ON COLUMN transactions.to_wallet_id IS 'Destination wallet for transfers';
COMMENT ON COLUMN transactions.type IS 'INCOME, EXPENSE, or TRANSFER';
COMMENT ON COLUMN transactions.amount IS 'Transaction amount';
COMMENT ON COLUMN transactions.currency IS 'ISO 4217 three-letter currency code';
COMMENT ON COLUMN transactions.transaction_date IS 'Date when the transaction occurred';
