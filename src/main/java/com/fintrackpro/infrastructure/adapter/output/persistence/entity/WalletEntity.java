package com.fintrackpro.infrastructure.adapter.output.persistence.entity;

import com.fintrackpro.domain.valueobject.WalletType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity representing a wallet in the database.
 * Maps to the 'wallets' table.
 */
@Entity
@Table(name = "wallets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user"})
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_wallet_user"))
    private UserEntity user;

    // Basic Information
    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_type", nullable = false, length = 50)
    private WalletType walletType;

    // Financial Details
    @Column(nullable = false, length = 3, columnDefinition = "VARCHAR(3) DEFAULT 'USD'")
    private String currency;

    @Column(name = "initial_balance", nullable = false, precision = 19, scale = 4, columnDefinition = "DECIMAL(19,4) DEFAULT 0.00")
    private BigDecimal initialBalance;

    @Column(name = "current_balance", nullable = false, precision = 19, scale = 4, columnDefinition = "DECIMAL(19,4) DEFAULT 0.00")
    private BigDecimal currentBalance;

    @Column(name = "credit_limit", precision = 19, scale = 4)
    private BigDecimal creditLimit;

    // Customization
    @Column(columnDefinition = "VARCHAR(7) DEFAULT '#4A90E2'")
    private String color;

    @Column(columnDefinition = "VARCHAR(50) DEFAULT 'wallet'")
    private String icon;

    // Status and Settings
    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean active;

    @Column(name = "is_default", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean defaultWallet;

    @Column(name = "is_excluded_from_total", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean excludedFromTotal;

    // Bank Account Specific
    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "account_type", length = 50)
    private String accountType;

    // Investment Specific
    @Column(name = "investment_type", length = 50)
    private String investmentType;

    @Column(name = "institution_name", length = 100)
    private String institutionName;

    // Metadata
    @Column(name = "display_order", columnDefinition = "INTEGER DEFAULT 0")
    private Integer displayOrder;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Transaction Statistics
    @Column(name = "transaction_count", columnDefinition = "INTEGER DEFAULT 0")
    private Integer transactionCount;

    @Column(name = "last_transaction_date")
    private LocalDateTime lastTransactionDate;

    // Audit Fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", foreignKey = @ForeignKey(name = "fk_wallet_created_by"))
    private UserEntity createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", foreignKey = @ForeignKey(name = "fk_wallet_updated_by"))
    private UserEntity updatedBy;

    // Soft Delete
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean deleted;

    @PrePersist
    @PreUpdate
    private void validate() {
        // Set default values
        if (initialBalance == null) {
            initialBalance = BigDecimal.ZERO;
        }
        if (currentBalance == null) {
            currentBalance = initialBalance;
        }
        if (currency == null || currency.trim().isEmpty()) {
            currency = "USD";
        }
        if (color == null || color.trim().isEmpty()) {
            color = "#4A90E2";
        }
        if (icon == null || icon.trim().isEmpty()) {
            icon = "wallet";
        }
        if (transactionCount == null) {
            transactionCount = 0;
        }
        if (displayOrder == null) {
            displayOrder = 0;
        }

        
        // Validate based on wallet type
        if (walletType == WalletType.CREDIT_CARD) {
            if (creditLimit == null || creditLimit.compareTo(BigDecimal.ZERO) < 0) {
                creditLimit = BigDecimal.ZERO;
            }
        } else {
            creditLimit = null;
            // Ensure balance is not negative for non-credit cards
            if (currentBalance.compareTo(BigDecimal.ZERO) < 0) {
                currentBalance = BigDecimal.ZERO;
            }
        }
        
        // Update active status based on deleted flag
        active = !deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WalletEntity that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    // Business methods
    public boolean isActive() {
        return active && !deleted;
    }
    
    public String getFormattedBalance() {
        return String.format("%s %,.2f", currency, currentBalance);
    }
}
