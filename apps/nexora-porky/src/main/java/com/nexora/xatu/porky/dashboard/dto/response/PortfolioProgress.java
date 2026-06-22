package com.nexora.xatu.porky.dashboard.dto.response;

import java.math.BigDecimal;

public record PortfolioProgress(
    String type,
    String label,
    BigDecimal investedAmount,
    BigDecimal currentValue,
    BigDecimal pnl,
    BigDecimal pnlPercent,
    BigDecimal budgetAmount) {}
