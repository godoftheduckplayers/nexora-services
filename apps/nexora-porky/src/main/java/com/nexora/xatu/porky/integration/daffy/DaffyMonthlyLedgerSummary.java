package com.nexora.xatu.porky.integration.daffy;

import com.nexora.xatu.porky.shared.enums.BudgetCategory;
import java.math.BigDecimal;
import java.util.Map;

public record DaffyMonthlyLedgerSummary(
    int year,
    int month,
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    Map<BudgetCategory, BigDecimal> spentByCategory,
    Map<BudgetCategory, BigDecimal> fixedExpensesByCategory,
    DaffyPortfolioMovementSummary investment,
    DaffyPortfolioMovementSummary betting) {}
