package com.nexora.xatu.daffy.portfolio.dto.response;

import com.nexora.xatu.daffy.shared.enums.PortfolioType;
import java.math.BigDecimal;
import java.time.Instant;

public record PortfolioPositionResponse(
    String id,
    String name,
    PortfolioType type,
    BigDecimal investedAmount,
    BigDecimal currentValue,
    BigDecimal pnl,
    BigDecimal pnlPercent,
    Instant valueUpdatedAt) {}
