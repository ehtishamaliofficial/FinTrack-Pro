package com.fintrackpro.infrastructure.adapter.input.mapper;

import com.fintrackpro.application.port.input.CurrentUserProvider;
import com.fintrackpro.domain.model.Transaction;
import com.fintrackpro.infrastructure.adapter.input.dto.request.CreateTransactionRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.request.UpdateTransactionRequest;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting Transaction DTOs to Domain models.
 */
@Mapper(componentModel = "spring")
public interface TransactionApiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", expression = "java(currentUserProvider.getCurrentUserId())")
    @Mapping(target = "status", expression = "java(com.fintrackpro.domain.valueobject.TransactionStatus.COMPLETED)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", expression = "java(currentUserProvider.getCurrentUserId())")
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "recurringGroupId", ignore = true)
    Transaction toModel(CreateTransactionRequest request, @Context CurrentUserProvider currentUserProvider);

    @Mapping(target = "id", ignore = true) // Set from path variable in controller
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", expression = "java(currentUserProvider.getCurrentUserId())")
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "recurringGroupId", ignore = true)
    @Mapping(target = "isRecurring", ignore = true)
    @Mapping(target = "recurringPattern", ignore = true)
    @Mapping(target = "currency", ignore = true)
    Transaction toModel(UpdateTransactionRequest request, @Context CurrentUserProvider currentUserProvider);
}
