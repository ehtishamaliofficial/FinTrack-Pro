package com.fintrackpro.domain.port.output;

import com.fintrackpro.domain.model.Category;
import com.fintrackpro.domain.valueobject.CategoryType;

import java.util.List;
import java.util.Optional;

/**
 * Port for category persistence operations.
 * Defines the contract for category data access operations.
 */
public interface CategoryRepositoryPort {

    /**
     * Saves a category.
     *
     * @param category the category to save
     * @return the saved category
     */
    Category save(Category category);

    /**
     * Finds a category by ID.
     *
     * @param id the category ID
     * @return an Optional containing the category if found, empty otherwise
     */
    Optional<Category> findById(Long id);

    /**
     * Finds all categories for a specific user (custom categories).
     *
     * @param userId the user ID
     * @return a list of categories belonging to the user
     */
    List<Category> findByUserId(Long userId);

    /**
     * Finds all system categories.
     *
     * @return a list of system-defined categories
     */
    List<Category> findSystemCategories();

    /**
     * Finds categories by type for a specific user.
     * Includes both system and custom categories.
     *
     * @param userId the user ID
     * @param type   the category type (INCOME/EXPENSE)
     * @return a list of categories matching the type
     */
    List<Category> findByUserIdAndType(Long userId, CategoryType type);

    /**
     * Finds all categories for a user (system + custom).
     *
     * @param userId the user ID
     * @return a list of all available categories for the user
     */
    List<Category> findAllByUserId(Long userId);

    /**
     * Checks if a category with the given name exists for a user.
     *
     * @param userId the user ID
     * @param name   the category name
     * @return true if a category with the name exists, false otherwise
     */
    boolean existsByUserIdAndName(Long userId, String name);

    /**
     * Deletes a category by ID.
     *
     * @param id the category ID to delete
     */
    void deleteById(Long id);

    /**
     * Counts custom categories for a user.
     *
     * @param userId the user ID
     * @return the count of custom categories
     */
    long countByUserId(Long userId);
}
