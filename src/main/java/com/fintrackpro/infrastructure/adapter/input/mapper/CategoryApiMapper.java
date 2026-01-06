package com.fintrackpro.infrastructure.adapter.input.mapper;

import com.fintrackpro.application.port.input.CurrentUserProvider;
import com.fintrackpro.domain.model.Category;
import com.fintrackpro.infrastructure.adapter.input.dto.request.CreateCategoryRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.request.UpdateCategoryRequest;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryApiMapper {

    /**
     * Maps CreateCategoryRequest to Category domain model
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", expression = "java(currentUserProvider.getCurrentUserId())")
    @Mapping(target = "isSystem", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toModel(CreateCategoryRequest request, @Context CurrentUserProvider currentUserProvider);

    /**
     * Maps UpdateCategoryRequest to Category domain model
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "isSystem", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toModel(UpdateCategoryRequest request);
}
