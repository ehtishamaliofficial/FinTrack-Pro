package com.fintrackpro.infrastructure.mapper;

import com.fintrackpro.domain.model.Transaction;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.CategoryEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.TransactionEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.WalletEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TransactionPersistenceMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "walletId", source = "wallet.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "toWalletId", source = "toWallet.id")
    Transaction toDomain(TransactionEntity entity);

    @InheritInverseConfiguration
    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUser")
    @Mapping(target = "wallet", source = "walletId", qualifiedByName = "mapWallet")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategory")
    @Mapping(target = "toWallet", source = "toWalletId", qualifiedByName = "mapWallet")
    TransactionEntity toEntity(Transaction domain);

    @Named("mapUser")
    default UserEntity mapUser(Long userId) {
        if (userId == null)
            return null;
        UserEntity user = new UserEntity();
        user.setId(userId);
        return user;
    }

    @Named("mapWallet")
    default WalletEntity mapWallet(Long walletId) {
        if (walletId == null)
            return null;
        WalletEntity wallet = new WalletEntity();
        wallet.setId(walletId);
        return wallet;
    }

    @Named("mapCategory")
    default CategoryEntity mapCategory(Long categoryId) {
        if (categoryId == null)
            return null;
        CategoryEntity category = new CategoryEntity();
        category.setId(categoryId);
        return category;
    }
}
