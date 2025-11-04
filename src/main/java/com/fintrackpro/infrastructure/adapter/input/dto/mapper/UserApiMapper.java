package com.fintrackpro.infrastructure.adapter.input.dto.mapper;


import com.fintrackpro.domain.model.User;
import com.fintrackpro.infrastructure.adapter.input.dto.request.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserApiMapper {

    @Mapping(target = "firstName", source = "fullName", qualifiedByName = "extractFirstName")
    @Mapping(target = "lastName", source = "fullName", qualifiedByName = "extractLastName")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "enabled", constant = "true")
    User toDomain(RegisterRequest request);


    @Named("extractFirstName")
    default String extractFirstName(String fullName) {
        return fullName != null && fullName.contains(" ")
                ? fullName.split(" ")[0]
                : fullName;
    }

    @Named("extractLastName")
    default String extractLastName(String fullName) {
        if (fullName == null) return null;
        String[] parts = fullName.split(" ", 2);
        return parts.length > 1 ? parts[1] : "";
    }

}
