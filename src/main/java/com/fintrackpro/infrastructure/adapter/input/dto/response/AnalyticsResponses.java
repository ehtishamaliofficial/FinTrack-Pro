package com.fintrackpro.infrastructure.adapter.input.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * Collection of response DTOs for Analytics endpoints.
 */
public class AnalyticsResponses {

    public record SummaryResponse(
            String period,
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal netSavings,
            Double savingsRate,
            Long transactionCount,
            BigDecimal averageTransactionAmount) {
    }

    public record CategoryBreakdownItemSource(
            Long categoryId,
            String categoryName,
            BigDecimal amount,
            Double percentage,
            Long transactionCount,
            String color) {
    }

    public record CategoryBreakdownResponse(
            List<CategoryBreakdownItemSource> categories) {
    }

    public record Dataset(
            String label,
            List<BigDecimal> data) {
    }

    public record TrendsResponse(
            List<String> labels,
            List<Dataset> datasets) {
    }

    public record TopCategoryResponse(
            List<CategoryBreakdownItemSource> topCategories) {
    }

    public record CashFlowResponse(
            BigDecimal totalInflow,
            BigDecimal totalOutflow,
            BigDecimal netCashFlow,
            List<CashFlowDaily> dailyData) {
    }

    public record CashFlowDaily(
            String date,
            BigDecimal inflow,
            BigDecimal outflow) {
    }

    public record IncomeSourceResponse(
            List<CategoryBreakdownItemSource> sources) {
    }

    public record SpendingPatternResponse(
            List<DaySpending> patterns) {
    }

    public record DaySpending(
            String dayOfWeek,
            BigDecimal amount,
            Long transactionCount) {
    }

    public record ComparisonResponse(
            String currentPeriod,
            String previousPeriod,
            BigDecimal currentAmount,
            BigDecimal previousAmount,
            Double percentageChange) {
    }

    public record SavingsRateResponse(
            String period,
            BigDecimal income,
            BigDecimal savings,
            Double rate) {
    }

    public record NetWorthResponse(
            BigDecimal totalAssets,
            BigDecimal totalLiabilities,
            BigDecimal netWorth,
            List<WalletBalance> walletBalances) {
    }

    public record WalletBalance(
            Long walletId,
            String walletName,
            BigDecimal balance,
            String currency) {
    }
}
