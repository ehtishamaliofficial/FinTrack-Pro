package com.fintrackpro.infrastructure.adapter.output.persistence.repository;

import com.fintrackpro.domain.valueobject.CategoryType;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.CategoryEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, Long> {

    /**
     * Find all categories for a user
     */
    List<CategoryEntity> findByUser(UserEntity user);

    /**
     * Find categories by user and type
     */
    List<CategoryEntity> findByUserAndType(UserEntity user, CategoryType type);

    /**
     * Find all system categories
     */
    List<CategoryEntity> findByIsSystemTrue();

    /**
     * Find system categories by type
     */
    List<CategoryEntity> findByIsSystemTrueAndType(CategoryType type);

    /**
     * Check if category exists with given name for user
     */
    boolean existsByUserAndName(UserEntity user, String name);

    /**
     * Count custom categories for a user
     */
    long countByUserAndIsSystemFalse(UserEntity user);
}
