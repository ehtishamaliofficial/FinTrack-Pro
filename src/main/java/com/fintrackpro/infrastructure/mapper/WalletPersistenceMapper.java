package com.fintrackpro.infrastructure.mapper;

import com.fintrackpro.domain.model.Wallet;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.WalletEntity;
import com.fintrackpro.infrastructure.helper.EntityReferenceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Wallet domain model and WalletEntity.
 */
@Component
@RequiredArgsConstructor
public class WalletPersistenceMapper {

    private final EntityReferenceHelper entityReferenceHelper;



    /**
     * Converts a Wallet domain model to WalletEntity.
     *
     * @param wallet the domain model to convert
     * @return the converted entity
     */
    public WalletEntity toEntity(Wallet wallet) {
        if (wallet == null) {
            return null;
        }

        return WalletEntity.builder()
                .id(wallet.id())
                .name(wallet.name())
                .description(wallet.description())
                .walletType(wallet.walletType())
                .currency(wallet.currency())
                .initialBalance(wallet.initialBalance())
                .currentBalance(wallet.currentBalance())
                .creditLimit(wallet.creditLimit())
                .color(wallet.color())
                .icon(wallet.icon())
                .active(wallet.isActive())
                .defaultWallet(wallet.isDefault())
                .excludedFromTotal(wallet.isExcludedFromTotal())
                .bankName(wallet.bankName())
                .accountNumber(wallet.accountNumber())
                .accountType(wallet.accountType())
                .investmentType(wallet.investmentType())
                .institutionName(wallet.institutionName())
                .displayOrder(wallet.displayOrder())
                .notes(wallet.notes())
                .transactionCount(wallet.transactionCount())
                .lastTransactionDate(wallet.lastTransactionDate())
                .createdAt(wallet.createdAt())
                .updatedAt(wallet.updatedAt())
                .createdBy(entityReferenceHelper.getUserReference(wallet.userId()))
                .updatedBy(entityReferenceHelper.getUserReference(wallet.userId()))
                .deleted(wallet.deleted())
                .deletedAt(wallet.deletedAt())
                .user(entityReferenceHelper.getUserReference(wallet.userId()))
                .build();
    }

    /**
     * Converts a WalletEntity to Wallet domain model.
     *
     * @param entity the entity to convert
     * @return the converted domain model
     */
    public Wallet toDomain(WalletEntity entity) {
        if (entity == null) {
            return null;
        }

        return Wallet.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .name(entity.getName())
                .description(entity.getDescription())
                .walletType(entity.getWalletType())
                .currency(entity.getCurrency())
                .initialBalance(entity.getInitialBalance())
                .currentBalance(entity.getCurrentBalance())
                .creditLimit(entity.getCreditLimit())
                .color(entity.getColor())
                .icon(entity.getIcon())
                .isActive(entity.isActive())
                .isDefault(entity.isDefaultWallet())
                .isExcludedFromTotal(entity.isExcludedFromTotal())
                .bankName(entity.getBankName())
                .accountNumber(entity.getAccountNumber())
                .accountType(entity.getAccountType())
                .investmentType(entity.getInvestmentType())
                .institutionName(entity.getInstitutionName())
                .displayOrder(entity.getDisplayOrder())
                .notes(entity.getNotes())
                .transactionCount(entity.getTransactionCount())
                .lastTransactionDate(entity.getLastTransactionDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy().getId())
                .updatedBy(entity.getUpdatedBy().getId())
                .deleted(entity.isDeleted())
                .deletedAt(entity.getDeletedAt())
                .build();
    }
}