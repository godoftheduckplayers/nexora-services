package com.nexora.xatu.porky.dashboard.dto.response;

import java.math.BigDecimal;

public record PortfolioMovementTotals(
    BigDecimal deposits,
    BigDecimal withdrawals,
    BigDecimal gains,
    BigDecimal losses,
    BigDecimal netFlow) {}
