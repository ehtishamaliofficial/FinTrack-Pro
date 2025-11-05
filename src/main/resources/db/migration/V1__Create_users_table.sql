-- V1__create_users_table.sql

CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,

    -- Authentication
                                     username VARCHAR(50) NOT NULL UNIQUE,
                                     email VARCHAR(100) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,

    -- User Profile
                                     first_name VARCHAR(50),
                                     last_name VARCHAR(50),
                                     phone_number VARCHAR(20),
                                     date_of_birth DATE,
                                     profile_picture_url VARCHAR(500),

    -- Account Status
                                     enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                     account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
                                     email_verified BOOLEAN NOT NULL DEFAULT FALSE,

    -- Security
                                     failed_login_attempts INTEGER NOT NULL DEFAULT 0,
                                     account_locked_until TIMESTAMP,
                                     last_login_at TIMESTAMP,
                                     last_login_ip VARCHAR(45),

    -- Email Verification
                                     email_verification_token VARCHAR(255),
                                     email_verification_token_expiry TIMESTAMP,

    -- Password Reset
                                     password_reset_token VARCHAR(255),
                                     password_reset_token_expiry TIMESTAMP,

    -- Preferences
                                     default_currency VARCHAR(3) DEFAULT 'USD',
                                     timezone VARCHAR(50) DEFAULT 'UTC',
                                     language VARCHAR(10) DEFAULT 'en',

    -- Audit Fields
                                     created_at TIMESTAMP NOT NULL,
                                     updated_at TIMESTAMP NOT NULL,
                                     created_by VARCHAR(50),
                                     updated_by VARCHAR(50),
                                     version BIGINT NOT NULL DEFAULT 0,

    -- Soft Delete
                                     deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                     deleted_at TIMESTAMP
);


CREATE INDEX IF NOT EXISTS idx_users_email_verified
    ON users (email_verified);

CREATE INDEX IF NOT EXISTS idx_users_account_non_locked
    ON users (account_non_locked);

CREATE INDEX IF NOT EXISTS idx_users_deleted
    ON users (deleted);
