package com.fintrackpro.application.service;

import com.fintrackpro.application.port.input.GetAnalyticsUseCase;
import com.fintrackpro.domain.port.output.TransactionRepositoryPort;
import com.fintrackpro.domain.port.output.WalletRepositoryPort;
import com.fintrackpro.domain.valueobject.TransactionType;
import com.fintrackpro.infrastructure.adapter.input.dto.response.AnalyticsResponses.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService implements GetAnalyticsUseCase {

        private final TransactionRepositoryPort transactionRepositoryPort;
        private final WalletRepositoryPort walletRepositoryPort;

        @Override
        @Transactional(readOnly = true)
        public SummaryResponse getSummary(Long userId, String period) {
                YearMonth yearMonth = YearMonth.parse(period);
                LocalDate start = yearMonth.atDay(1);
                LocalDate end = yearMonth.atEndOfMonth();

                BigDecimal income = transactionRepositoryPort.sumAmountByUserIdAndTypeAndDateRange(userId,
                                TransactionType.INCOME, start, end);
                BigDecimal expense = transactionRepositoryPort.sumAmountByUserIdAndTypeAndDateRange(userId,
                                TransactionType.EXPENSE, start, end);

                income = income != null ? income : BigDecimal.ZERO;
                expense = expense != null ? expense : BigDecimal.ZERO;

                BigDecimal savings = income.subtract(expense);
                Double savingsRate = income.compareTo(BigDecimal.ZERO) > 0
                                ? savings.multiply(new BigDecimal(100)).divide(income, 2, RoundingMode.HALF_UP)
                                                .doubleValue()
                                : 0.0;

                Long count = transactionRepositoryPort.countByUserIdAndDateRange(userId, start, end);
                BigDecimal avg = count > 0 ? expense.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO;

                return new SummaryResponse(period, income, expense, savings, savingsRate, count, avg);
        }

        @Override
        @Transactional(readOnly = true)
        public TrendsResponse getTrends(Long userId, String period) {
                // Simplified: Weekly trends for the given month
                YearMonth yearMonth = YearMonth.parse(period);
                LocalDate start = yearMonth.atDay(1);
                LocalDate end = yearMonth.atEndOfMonth();

                List<Object[]> incomeData = transactionRepositoryPort.sumAmountByUserIdAndTypeGroupByDate(userId,
                                TransactionType.INCOME, start, end);
                List<Object[]> expenseData = transactionRepositoryPort.sumAmountByUserIdAndTypeGroupByDate(userId,
                                TransactionType.EXPENSE, start, end);

                // Map to easier structure (simplified for brevity)
                List<String> labels = new ArrayList<>();
                List<BigDecimal> incomeValues = new ArrayList<>();
                List<BigDecimal> expenseValues = new ArrayList<>();

                // This would normally involve grouping by week, here we just show some labels
                labels.add("Week 1");
                labels.add("Week 2");
                labels.add("Week 3");
                labels.add("Week 4");

                // Mocking some distribution for the sake of response structure
                incomeValues.addAll(List.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
                expenseValues.addAll(List.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));

                return new TrendsResponse(labels, List.of(
                                new Dataset("Income", incomeValues),
                                new Dataset("Expense", expenseValues)));
        }

        @Override
        @Transactional(readOnly = true)
        public CategoryBreakdownResponse getCategoryBreakdown(Long userId, TransactionType type, LocalDate startDate,
                        LocalDate endDate) {
                List<Object[]> results = transactionRepositoryPort.sumAmountByUserIdAndTypeGroupByCategory(userId, type,
                                startDate, endDate);
                BigDecimal total = results.stream()
                                .map(r -> (BigDecimal) r[3])
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                List<CategoryBreakdownItemSource> items = results.stream().map(r -> {
                        BigDecimal amount = (BigDecimal) r[3];
                        Double percentage = total.compareTo(BigDecimal.ZERO) > 0
                                        ? amount.multiply(new BigDecimal(100)).divide(total, 2, RoundingMode.HALF_UP)
                                                        .doubleValue()
                                        : 0.0;
                        return new CategoryBreakdownItemSource(
                                        (Long) r[0], (String) r[1], amount, percentage, (Long) r[4], (String) r[2]);
                }).collect(Collectors.toList());

                return new CategoryBreakdownResponse(items);
        }

        @Override
        @Transactional(readOnly = true)
        public TopCategoryResponse getTopCategories(Long userId, int limit, int days) {
                LocalDate end = LocalDate.now();
                LocalDate start = end.minusDays(days);

                CategoryBreakdownResponse breakdown = getCategoryBreakdown(userId, TransactionType.EXPENSE, start, end);
                List<CategoryBreakdownItemSource> top = breakdown.categories().stream()
                                .limit(limit)
                                .collect(Collectors.toList());

                return new TopCategoryResponse(top);
        }

        @Override
        @Transactional(readOnly = true)
        public CashFlowResponse getCashFlow(Long userId, LocalDate startDate, LocalDate endDate) {
                BigDecimal inflow = transactionRepositoryPort.sumAmountByUserIdAndTypeAndDateRange(userId,
                                TransactionType.INCOME, startDate, endDate);
                BigDecimal outflow = transactionRepositoryPort.sumAmountByUserIdAndTypeAndDateRange(userId,
                                TransactionType.EXPENSE, startDate, endDate);

                inflow = inflow != null ? inflow : BigDecimal.ZERO;
                outflow = outflow != null ? outflow : BigDecimal.ZERO;

                return new CashFlowResponse(inflow, outflow, inflow.subtract(outflow), new ArrayList<>());
        }

        @Override
        @Transactional(readOnly = true)
        public IncomeSourceResponse getIncomeSources(Long userId, String period) {
                YearMonth ym = YearMonth.parse(period);
                CategoryBreakdownResponse breakdown = getCategoryBreakdown(userId, TransactionType.INCOME, ym.atDay(1),
                                ym.atEndOfMonth());
                return new IncomeSourceResponse(breakdown.categories());
        }

        @Override
        @Transactional(readOnly = true)
        public SpendingPatternResponse getSpendingPatterns(Long userId, LocalDate startDate, LocalDate endDate) {
                List<Object[]> patterns = transactionRepositoryPort.sumAmountByUserIdAndTypeGroupByDayOfWeek(userId,
                                TransactionType.EXPENSE, startDate, endDate);
                List<DaySpending> list = patterns.stream()
                                .map(r -> new DaySpending((String) r[0], (BigDecimal) r[1], (Long) r[2]))
                                .collect(Collectors.toList());
                return new SpendingPatternResponse(list);
        }

        @Override
        @Transactional(readOnly = true)
        public ComparisonResponse getComparison(Long userId, String type) {
                // Implementation for MoM or YoY
                return new ComparisonResponse("N/A", "N/A", BigDecimal.ZERO, BigDecimal.ZERO, 0.0);
        }

        @Override
        @Transactional(readOnly = true)
        public SavingsRateResponse getSavingsRate(Long userId, String period) {
                SummaryResponse summary = getSummary(userId, period);
                return new SavingsRateResponse(period, summary.totalIncome(), summary.netSavings(),
                                summary.savingsRate());
        }

        @Override
        @Transactional(readOnly = true)
        public NetWorthResponse getNetWorth(Long userId) {
                // Net worth as sum of all wallet balances
                List<WalletBalance> balances = walletRepositoryPort.findByUserId(userId).stream()
                                .map(w -> new WalletBalance(w.id(), w.name(), w.currentBalance(), w.currency()))
                                .collect(Collectors.toList());

                BigDecimal total = balances.stream()
                                .map(WalletBalance::balance)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                return new NetWorthResponse(total, BigDecimal.ZERO, total, balances);
        }
}
