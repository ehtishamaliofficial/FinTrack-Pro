package com.fintrackpro.application.service;

import com.fintrackpro.application.port.input.CategoryUseCase;
import com.fintrackpro.domain.exception.InvalidRequestException;
import com.fintrackpro.domain.model.Category;
import com.fintrackpro.domain.port.output.CategoryRepositoryPort;
import com.fintrackpro.domain.port.output.TransactionRepositoryPort;
import com.fintrackpro.domain.valueobject.CategoryType;
import com.fintrackpro.infrastructure.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service implementation for category operations.
 */
@Slf4j
@Service
public class CategoryService implements CategoryUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final MessageUtil messageUtil;
    private final TransactionRepositoryPort transactionRepositoryPort;

    /**
     * Constructor with optional TransactionRepositoryPort.
     * The transaction repository is optional to allow the service to work
     * even when the Transaction feature hasn't been implemented yet.
     */
    public CategoryService(
            CategoryRepositoryPort categoryRepositoryPort,
            MessageUtil messageUtil,
            @Autowired(required = false) TransactionRepositoryPort transactionRepositoryPort) {
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.messageUtil = messageUtil;
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    private static final String CATEGORY_NOT_FOUND = "Category not found with id: ";
    private static final int MAX_CUSTOM_CATEGORIES = 50;

    @Override
    @Transactional
    public Category createCategory(Category category) {
        log.info("Creating new category '{}' for user: {}", category.name(), category.userId());

        // 1. Validate input
        validateCategoryRequest(category);

        // 2. Check maximum custom categories limit
        long customCategoryCount = categoryRepositoryPort.countByUserId(category.userId());
        if (customCategoryCount >= MAX_CUSTOM_CATEGORIES) {
            throw new InvalidRequestException(
                    "Maximum limit of " + MAX_CUSTOM_CATEGORIES + " custom categories reached");
        }

        // 3. Validate uniqueness
        if (categoryRepositoryPort.existsByUserIdAndName(category.userId(), category.name())) {
            throw new InvalidRequestException(
                    messageUtil.getMessage("error.category.existsWithName") + category.name());
        }

        // 4. Build and save category
        Category categoryToCreate = prepareCategoryForCreation(category);
        Category createdCategory = categoryRepositoryPort.save(categoryToCreate);

        log.info("Successfully created category '{}' (ID: {}) for user {}",
                createdCategory.name(), createdCategory.id(), category.userId());

        return createdCategory;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        log.debug("Fetching category with id: {}", id);
        return categoryRepositoryPort.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getUserCategories(Long userId) {
        log.debug("Fetching all categories for user: {}", userId);
        return categoryRepositoryPort.findAllByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoriesByType(Long userId, CategoryType type) {
        log.debug("Fetching {} categories for user: {}", type, userId);
        return categoryRepositoryPort.findByUserIdAndType(userId, type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getSystemCategories() {
        log.debug("Fetching system categories");
        return categoryRepositoryPort.findSystemCategories();
    }

    @Override
    @Transactional
    public Category updateCategory(Category category) {
        log.info("Updating category with id: {}", category.id());

        Category existingCategory = categoryRepositoryPort.findById(category.id())
                .orElseThrow(() -> new InvalidRequestException(CATEGORY_NOT_FOUND + category.id()));

        // Prevent updating system categories
        if (existingCategory.isSystem()) {
            throw new InvalidRequestException("System categories cannot be modified");
        }

        // Prevent changing category type
        if (!existingCategory.type().equals(category.type())) {
            throw new InvalidRequestException("Category type cannot be changed after creation");
        }

        // If changing name, check for duplicates
        if (!existingCategory.name().equals(category.name()) &&
                categoryRepositoryPort.existsByUserIdAndName(category.userId(), category.name())) {
            throw new InvalidRequestException("Category with name " + category.name() + " already exists");
        }

        Category updatedCategory = new Category(
                category.id(),
                category.name(),
                category.icon(),
                category.color(),
                existingCategory.type(), // Keep original type
                existingCategory.isSystem(), // Keep system flag
                category.userId(),
                existingCategory.createdAt(), // Keep original creation time
                LocalDateTime.now() // Update timestamp
        );

        return categoryRepositoryPort.save(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);

        Category category = categoryRepositoryPort.findById(id)
                .orElseThrow(() -> new InvalidRequestException(CATEGORY_NOT_FOUND + id));

        // Prevent deleting system categories
        if (category.isSystem()) {
            throw new InvalidRequestException("System categories cannot be deleted");
        }

        // Check if category has associated transactions
        if (transactionRepositoryPort != null) {
            if (transactionRepositoryPort.existsByCategoryId(id)) {
                throw new InvalidRequestException(
                        "Cannot delete category with associated transactions. " +
                                "Please delete or reassign all transactions first.");
            }
        } else {
            log.warn("TransactionRepositoryPort not available. " +
                    "Skipping transaction check for category deletion (ID: {}). " +
                    "This check will be enforced once the Transaction feature is implemented.", id);
        }

        categoryRepositoryPort.deleteById(id);
        log.info("Successfully deleted category with id: {}", id);
    }

    /**
     * Validates the category request
     */
    private void validateCategoryRequest(Category category) {
        Objects.requireNonNull(category, "Category cannot be null");

        if (category.userId() == null) {
            throw new InvalidRequestException("User ID must be provided");
        }

        if (category.name() == null || category.name().trim().isEmpty()) {
            throw new InvalidRequestException("Category name is required");
        }

        if (category.type() == null) {
            throw new InvalidRequestException("Category type is required");
        }
    }

    /**
     * Prepares the category for creation with all required fields
     */
    private Category prepareCategoryForCreation(Category category) {
        LocalDateTime now = LocalDateTime.now();

        return new Category(
                null, // ID will be generated
                category.name(),
                category.icon() != null ? category.icon() : getDefaultIcon(category.type()),
                category.color() != null ? category.color() : getDefaultColor(category.type()),
                category.type(),
                false, // Custom categories are never system categories
                category.userId(),
                now,
                now);
    }

    /**
     * Gets default icon based on category type
     */
    private String getDefaultIcon(CategoryType type) {
        return type == CategoryType.INCOME ? "trending-up" : "shopping-cart";
    }

    /**
     * Gets default color based on category type
     */
    private String getDefaultColor(CategoryType type) {
        return type == CategoryType.INCOME ? "#4CAF50" : "#F44336";
    }
}
