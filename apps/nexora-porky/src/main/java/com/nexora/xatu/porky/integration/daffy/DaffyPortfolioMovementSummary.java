package com.nexora.xatu.porky.integration.daffy;

import java.math.BigDecimal;

public record DaffyPortfolioMovementSummary(
    BigDecimal deposits,
    BigDecimal withdrawals,
    BigDecimal gains,
    BigDecimal losses,
    BigDecimal netFlow) {}
