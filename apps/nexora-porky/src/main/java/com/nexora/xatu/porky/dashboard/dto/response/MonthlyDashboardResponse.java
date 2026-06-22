package com.nexora.xatu.porky.dashboard.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record MonthlyDashboardResponse(
    int year,
    int month,
    BigDecimal monthlyIncome,
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    BigDecimal balance,
    boolean profileComplete,
    List<CategoryProgress> categories,
    PortfolioProgress investment,
    PortfolioProgress betting) {}
