package com.nexora.xatu.porky.budget.dto.response;

import com.nexora.xatu.porky.shared.enums.BudgetCategory;
import com.nexora.xatu.porky.shared.enums.GoalPurpose;
import java.math.BigDecimal;

public record CategoryAllocationResponse(
    BudgetCategory category, BigDecimal percentage, String label, GoalPurpose purpose) {}
