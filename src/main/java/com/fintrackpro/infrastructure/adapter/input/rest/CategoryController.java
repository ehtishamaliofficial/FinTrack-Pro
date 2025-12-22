package com.fintrackpro.infrastructure.adapter.input.rest;

import com.fintrackpro.application.port.input.CategoryUseCase;
import com.fintrackpro.application.port.input.CurrentUserProvider;
import com.fintrackpro.domain.model.Category;
import com.fintrackpro.domain.valueobject.CategoryType;
import com.fintrackpro.infrastructure.adapter.input.dto.request.CreateCategoryRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.request.UpdateCategoryRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.response.ApiResponse;
import com.fintrackpro.infrastructure.adapter.input.mapper.CategoryApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
@Tag(name = "Category Management", description = "APIs for managing income and expense categories")
public class CategoryController {

    private final CategoryUseCase categoryUseCase;
    private final CategoryApiMapper categoryApiMapper;
    private final CurrentUserProvider currentUserProvider;

    @Operation(summary = "Create a new custom category", description = "Creates a new custom category for the authenticated user. System categories cannot be created via API.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Category created successfully", content = @Content(schema = @Schema(implementation = Category.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data or maximum categories limit reached", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Category>> createCategory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Category creation details", required = true, content = @Content(schema = @Schema(implementation = CreateCategoryRequest.class))) @Valid @RequestBody CreateCategoryRequest request) {
        log.info("Creating new category '{}' of type {}", request.name(), request.type());
        Category category = categoryApiMapper.toModel(request, currentUserProvider);
        Category createdCategory = categoryUseCase.createCategory(category);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", createdCategory));
    }

    @Operation(summary = "Get category by ID", description = "Retrieves detailed information about a specific category")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category retrieved successfully", content = @Content(schema = @Schema(implementation = Category.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategory(
            @Parameter(description = "Category ID", required = true, example = "1") @PathVariable Long id) {
        log.info("Fetching category with id: {}", id);
        return categoryUseCase.getCategoryById(id)
                .map(category -> ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Category not found")));
    }

    @Operation(summary = "Get all categories", description = "Retrieves all categories (system + custom) available to the authenticated user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categories retrieved successfully", content = @Content(schema = @Schema(implementation = List.class)))
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getUserCategories() {
        Long userId = currentUserProvider.getCurrentUserId();
        log.info("Fetching all categories for user: {}", userId);
        List<Category> categories = categoryUseCase.getUserCategories(userId);
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
    }

    @Operation(summary = "Get income categories", description = "Retrieves all income categories (system + custom) for the authenticated user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Income categories retrieved successfully", content = @Content(schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/income")
    public ResponseEntity<ApiResponse<List<Category>>> getIncomeCategories() {
        Long userId = currentUserProvider.getCurrentUserId();
        log.info("Fetching income categories for user: {}", userId);
        List<Category> categories = categoryUseCase.getCategoriesByType(userId, CategoryType.INCOME);
        return ResponseEntity.ok(ApiResponse.success("Income categories retrieved successfully", categories));
    }

    @Operation(summary = "Get expense categories", description = "Retrieves all expense categories (system + custom) for the authenticated user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Expense categories retrieved successfully", content = @Content(schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/expense")
    public ResponseEntity<ApiResponse<List<Category>>> getExpenseCategories() {
        Long userId = currentUserProvider.getCurrentUserId();
        log.info("Fetching expense categories for user: {}", userId);
        List<Category> categories = categoryUseCase.getCategoriesByType(userId, CategoryType.EXPENSE);
        return ResponseEntity.ok(ApiResponse.success("Expense categories retrieved successfully", categories));
    }

    @Operation(summary = "Update category", description = "Updates the details of an existing custom category. System categories cannot be updated.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category updated successfully", content = @Content(schema = @Schema(implementation = Category.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data or attempting to modify system category", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @Parameter(description = "Category ID", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated category details", required = true, content = @Content(schema = @Schema(implementation = UpdateCategoryRequest.class))) @Valid @RequestBody UpdateCategoryRequest request) {
        log.info("Updating category with id: {}", id);

        // Get existing category to preserve type and userId
        Category existingCategory = categoryUseCase.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Category category = new Category(
                id,
                request.name(),
                request.icon(),
                request.color(),
                existingCategory.type(),
                existingCategory.isSystem(),
                existingCategory.userId(),
                existingCategory.createdAt(),
                existingCategory.updatedAt());

        Category updatedCategory = categoryUseCase.updateCategory(category);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", updatedCategory));
    }

    @Operation(summary = "Delete category", description = "Deletes a custom category. System categories cannot be deleted. Categories with associated transactions cannot be deleted.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot delete system category or category with transactions", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @Parameter(description = "Category ID", required = true, example = "1") @PathVariable Long id) {
        log.info("Deleting category with id: {}", id);
        categoryUseCase.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}
