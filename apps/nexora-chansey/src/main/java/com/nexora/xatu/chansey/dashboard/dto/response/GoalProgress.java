package com.nexora.xatu.chansey.dashboard.dto.response;

import java.math.BigDecimal;

public record GoalProgress(
    BigDecimal target,
    BigDecimal consumed,
    BigDecimal remaining,
    Integer progressPercent) {}
