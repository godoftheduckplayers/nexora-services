package com.nexora.xatu.daffy.summary.dto.response;

import com.nexora.xatu.daffy.shared.enums.BudgetCategory;
import java.math.BigDecimal;
import java.util.Map;

public record MonthlyLedgerSummary(
    int year,
    int month,
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    Map<BudgetCategory, BigDecimal> spentByCategory,
    Map<BudgetCategory, BigDecimal> fixedExpensesByCategory,
    PortfolioMovementSummary investment,
    PortfolioMovementSummary betting) {}
