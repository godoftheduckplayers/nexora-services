package com.nexora.xatu.porky.dashboard.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record PeriodMonthPoint(
    int year,
    int month,
    String label,
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    BigDecimal balance,
    BigDecimal totalBudgetAllocated,
    BigDecimal totalCategoryUsed,
    List<CategoryProgress> categories,
    PortfolioMovementTotals investment,
    PortfolioMovementTotals betting) {}
