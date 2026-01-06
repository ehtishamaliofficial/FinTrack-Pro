package com.fintrackpro.infrastructure.adapter.input.dto.mapper;


import com.fintrackpro.domain.model.User;
import com.fintrackpro.infrastructure.adapter.input.dto.request.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Value;

import java.time.Clock;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public abstract class UserApiMapper {

    @Value("${app.default.language:en}")
    protected String defaultLanguage;

    @Value("${app.default.timezone:UTC}")
    protected String defaultTimezone;
    
    @Value("${app.default.currency:PKR}")
    protected String defaultCurrency;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "profilePictureUrl", source = "profilePictureUrl")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "failedLoginAttempts", constant = "0")
    @Mapping(target = "defaultCurrency", expression = "java(registerRequest.currency() != null && !registerRequest.currency().trim().isEmpty() ? registerRequest.currency() : defaultCurrency)")
    @Mapping(target = "timezone", expression = "java(registerRequest.timezone() != null && !registerRequest.timezone().trim().isEmpty() ? registerRequest.timezone() : defaultTimezone)")
    @Mapping(target = "language", expression = "java(registerRequest.language() != null && !registerRequest.language().trim().isEmpty() ? registerRequest.language() : defaultLanguage)")
    @Mapping(target = "createdAt", expression = "java(now())")
    @Mapping(target = "updatedAt", expression = "java(now())")
    @Mapping(target = "deleted", constant = "false")
    public abstract User toDomain(RegisterRequest registerRequest);

    protected LocalDateTime now() {
        return LocalDateTime.now(Clock.systemUTC());
    }
}
