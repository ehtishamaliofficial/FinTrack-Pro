package com.fintrackpro.infrastructure.adapter.input.mapper;

import com.fintrackpro.application.port.input.CurrentUserProvider;
import com.fintrackpro.domain.model.Wallet;
import com.fintrackpro.infrastructure.adapter.input.dto.request.CreateWalletRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.request.UpdateWalletRequest;
import org.mapstruct.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface WalletApiMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", expression = "java(currentUserProvider.getCurrentUserId())")
    @Mapping(target = "currentBalance", source = "initialBalance")
    Wallet toModel(CreateWalletRequest request, @Context CurrentUserProvider currentUserProvider);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "currentBalance", ignore = true)
    @Mapping(target = "initialBalance", ignore = true)

    default Wallet toModel(UpdateWalletRequest request) {
        if (request == null) {
            return null;
        }
        return Wallet.builder()
            .name(request.name())
            .description(request.description())
            .walletType(request.walletType())
            .currency(request.currency())
            .creditLimit(request.creditLimit())
            .color(request.color())
            .icon(request.icon())
            .isDefault(request.isDefault())
            .isExcludedFromTotal(request.isExcludedFromTotal())
            .bankName(request.bankName())
            .accountNumber(request.accountNumber())
            .accountType(request.accountType())
            .investmentType(request.investmentType())
            .institutionName(request.institutionName())
            .displayOrder(request.displayOrder())
            .notes(request.notes())
            .build();
    }
}
