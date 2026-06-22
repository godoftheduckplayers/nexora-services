package com.nexora.xatu.porky.dashboard.dto.response;

import com.nexora.xatu.porky.shared.enums.BudgetCategory;
import com.nexora.xatu.porky.shared.enums.BudgetStatus;
import com.nexora.xatu.porky.shared.enums.GoalPurpose;
import java.math.BigDecimal;

public record CategoryProgress(
    BudgetCategory category,
    String label,
    GoalPurpose purpose,
    BigDecimal budgetAmount,
    BigDecimal spentAmount,
    BigDecimal fixedAmount,
    BigDecimal totalUsed,
    BigDecimal remaining,
    Integer progressPercent,
    BudgetStatus status) {}
