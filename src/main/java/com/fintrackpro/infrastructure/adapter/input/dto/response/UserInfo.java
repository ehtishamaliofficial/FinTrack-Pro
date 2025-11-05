package com.fintrackpro.infrastructure.adapter.input.dto.response;

import lombok.Builder;

@Builder
public record UserInfo(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        Boolean emailVerified
) {
}
