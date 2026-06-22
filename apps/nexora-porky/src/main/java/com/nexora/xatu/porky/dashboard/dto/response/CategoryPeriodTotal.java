package com.nexora.xatu.porky.dashboard.dto.response;

import com.nexora.xatu.porky.shared.enums.GoalPurpose;
import java.math.BigDecimal;

public record CategoryPeriodTotal(
    String label,
    GoalPurpose purpose,
    BigDecimal budgetAmount,
    BigDecimal usedAmount,
    BigDecimal remainingAmount) {}
