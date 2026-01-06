package com.fintrackpro.domain.model;

import com.fintrackpro.domain.valueobject.CategoryType;

import java.time.LocalDateTime;

public record Category(
        Long id,
        String name,
        String icon,
        String color,
        CategoryType type,
        Boolean isSystem,
        Long userId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
