package com.fintrackpro.infrastructure.mapper;

import com.fintrackpro.domain.model.RefreshToken;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.RefreshTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserPersistenceMapper.class})
public interface RefreshTokenPersistenceMapper {
    @Mapping(target = "user", ignore = true)
    RefreshToken toDomain(RefreshTokenEntity entity);

    @Mapping(target = "user", ignore = true)
    RefreshTokenEntity toEntity(RefreshToken domain);
}
