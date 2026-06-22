package com.nexora.xatu.porky.dashboard.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record PeriodTotals(
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    BigDecimal balance,
    BigDecimal totalBudgetAllocated,
    BigDecimal totalCategoryUsed,
    List<CategoryPeriodTotal> categories,
    PortfolioMovementTotals investment,
    PortfolioMovementTotals betting) {}
