package com.nexora.xatu.porky.dashboard.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PeriodDashboardResponse(
    LocalDate from,
    LocalDate to,
    BigDecimal monthlyIncome,
    boolean profileComplete,
    List<PeriodMonthPoint> months,
    PeriodTotals totals) {}
