package com.fintrackpro.infrastructure.mapper;

import com.fintrackpro.domain.model.Category;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.CategoryEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CategoryPersistenceMapper {
    @Mapping(target = "userId", source = "user.id")
    Category toDomain(CategoryEntity entity);

    @InheritInverseConfiguration
    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUserId")
    CategoryEntity toEntity(Category domain);

    @Named("mapUserId")
    default UserEntity mapUserId(Long userId) {
        if (userId == null) return null;
        UserEntity user = new UserEntity();
        user.setId(userId);
        return user;
    }
}
