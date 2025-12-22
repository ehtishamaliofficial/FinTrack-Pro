package com.fintrackpro.infrastructure.adapter.input.rest;

import com.fintrackpro.application.port.input.CurrentUserProvider;
import com.fintrackpro.application.port.input.GetAnalyticsUseCase;
import com.fintrackpro.domain.valueobject.TransactionType;
import com.fintrackpro.infrastructure.adapter.input.dto.response.ApiResponse;
import com.fintrackpro.infrastructure.adapter.input.dto.response.AnalyticsResponses.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Financial analytics and insights")
public class AnalyticsController {

    private final GetAnalyticsUseCase analyticsUseCase;
    private final CurrentUserProvider currentUserProvider;

    @Operation(summary = "Get monthly income vs expense summary")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<SummaryResponse>> getSummary(
            @RequestParam(defaultValue = "current") String period) {
        if ("current".equals(period)) {
            period = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM").format(LocalDate.now());
        }
        Long userId = currentUserProvider.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Summary retrieved successfully",
                analyticsUseCase.getSummary(userId, period)));
    }

    @Operation(summary = "Get spending trends over time")
    @GetMapping("/trends")
    public ResponseEntity<ApiResponse<TrendsResponse>> getTrends(
            @RequestParam(defaultValue = "current") String period) {
        if ("current".equals(period)) {
            period = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM").format(LocalDate.now());
        }
        Long userId = currentUserProvider.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Trends retrieved successfully",
                analyticsUseCase.getTrends(userId, period)));
    }

    @Operation(summary = "Get category-wise breakdown")
    @GetMapping("/category-breakdown")
    public ResponseEntity<ApiResponse<CategoryBreakdownResponse>> getCategoryBreakdown(
            @RequestParam TransactionType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = currentUserProvider.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Breakdown retrieved successfully",
                analyticsUseCase.getCategoryBreakdown(userId, type, startDate, endDate)));
    }

    @Operation(summary = "Get top spending categories")
    @GetMapping("/top-categories")
    public ResponseEntity<ApiResponse<TopCategoryResponse>> getTopCategories(
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "30") int days) {
        Long userId = currentUserProvider.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Top categories retrieved successfully",
                analyticsUseCase.getTopCategories(userId, limit, days)));
    }

    @Operation(summary = "Get cash flow analysis")
    @GetMapping("/cash-flow")
    public ResponseEntity<ApiResponse<CashFlowResponse>> getCashFlow(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = currentUserProvider.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Cash flow retrieved successfully",
                analyticsUseCase.getCashFlow(userId, startDate, endDate)));
    }

    @Operation(summary = "Get income sources breakdown")
    @GetMapping("/income-sources")
    public ResponseEntity<ApiResponse<IncomeSourceResponse>> getIncomeSources(
            @RequestParam(defaultValue = "current") String period) {
        if ("current".equals(period)) {
            period = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM").format(LocalDate.now());
        }
        Long userId = currentUserProvider.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Income sources retrieved successfully",
                analyticsUseCase.getIncomeSources(userId, period)));
    }

    @Operation(summary = "Get spending patterns by day of week")
    @GetMapping("/spending-patterns")
    public ResponseEntity<ApiResponse<SpendingPatternResponse>> getSpendingPatterns(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = currentUserProvider.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Spending patterns retrieved successfully",
                analyticsUseCase.getSpendingPatterns(userId, startDate, endDate)));
    }

    @Operation(summary = "Get period comparisons")
    @GetMapping("/comparison")
    public ResponseEntity<ApiResponse<ComparisonResponse>> getComparison(
            @RequestParam(defaultValue = "month-over-month") String type) {
        Long userId = currentUserProvider.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Comparison retrieved successfully",
                analyticsUseCase.getComparison(userId, type)));
    }

    @Operation(summary = "Get savings rate calculation")
    @GetMapping("/savings-rate")
    public ResponseEntity<ApiResponse<SavingsRateResponse>> getSavingsRate(
            @RequestParam(defaultValue = "current") String period) {
        if ("current".equals(period)) {
            period = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM").format(LocalDate.now());
        }
        Long userId = currentUserProvider.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Savings rate retrieved successfully",
                analyticsUseCase.getSavingsRate(userId, period)));
    }

    @Operation(summary = "Get total net worth")
    @GetMapping("/net-worth")
    public ResponseEntity<ApiResponse<NetWorthResponse>> getNetWorth() {
        Long userId = currentUserProvider.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Net worth retrieved successfully",
                analyticsUseCase.getNetWorth(userId)));
    }
}
