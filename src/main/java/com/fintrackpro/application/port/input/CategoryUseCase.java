package com.fintrackpro.application.port.input;

import com.fintrackpro.domain.model.Category;
import com.fintrackpro.domain.valueobject.CategoryType;

import java.util.List;
import java.util.Optional;

/**
 * Primary port for category-related operations.
 * Defines the core business operations that can be performed on categories.
 */
public interface CategoryUseCase {

    /**
     * Creates a new custom category.
     *
     * @param category the category to create
     * @return the created category with generated ID and timestamps
     */
    Category createCategory(Category category);

    /**
     * Retrieves a category by its ID.
     *
     * @param id the category ID
     * @return the category if found, empty otherwise
     */
    Optional<Category> getCategoryById(Long id);

    /**
     * Retrieves all categories for a specific user (system + custom).
     *
     * @param userId the user ID
     * @return list of all available categories
     */
    List<Category> getUserCategories(Long userId);

    /**
     * Retrieves categories by type for a specific user.
     *
     * @param userId the user ID
     * @param type   the category type (INCOME/EXPENSE)
     * @return list of categories matching the type
     */
    List<Category> getCategoriesByType(Long userId, CategoryType type);

    /**
     * Retrieves all system-defined categories.
     *
     * @return list of system categories
     */
    List<Category> getSystemCategories();

    /**
     * Updates an existing category.
     * System categories cannot be updated.
     *
     * @param category the category with updated information
     * @return the updated category
     */
    Category updateCategory(Category category);

    /**
     * Deletes a category by its ID.
     * System categories cannot be deleted.
     * Categories with transactions cannot be deleted.
     *
     * @param id the category ID to delete
     */
    void deleteCategory(Long id);
}
