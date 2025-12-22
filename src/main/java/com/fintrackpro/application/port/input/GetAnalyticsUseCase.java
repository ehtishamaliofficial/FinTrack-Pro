package com.fintrackpro.application.port.input;

import com.fintrackpro.domain.valueobject.TransactionType;
import com.fintrackpro.infrastructure.adapter.input.dto.response.AnalyticsResponses.*;

import java.time.LocalDate;

/**
 * Input port for Analytics operations.
 */
public interface GetAnalyticsUseCase {

    SummaryResponse getSummary(Long userId, String period);

    TrendsResponse getTrends(Long userId, String period);

    CategoryBreakdownResponse getCategoryBreakdown(Long userId, TransactionType type, LocalDate startDate,
            LocalDate endDate);

    TopCategoryResponse getTopCategories(Long userId, int limit, int days);

    CashFlowResponse getCashFlow(Long userId, LocalDate startDate, LocalDate endDate);

    IncomeSourceResponse getIncomeSources(Long userId, String period);

    SpendingPatternResponse getSpendingPatterns(Long userId, LocalDate startDate, LocalDate endDate);

    ComparisonResponse getComparison(Long userId, String type);

    SavingsRateResponse getSavingsRate(Long userId, String period);

    NetWorthResponse getNetWorth(Long userId);
}
