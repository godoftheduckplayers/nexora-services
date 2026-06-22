package com.nexora.xatu.daffy.summary.dto.response;

import java.math.BigDecimal;

public record PortfolioMovementSummary(
    BigDecimal deposits,
    BigDecimal withdrawals,
    BigDecimal gains,
    BigDecimal losses,
    BigDecimal netFlow) {}
