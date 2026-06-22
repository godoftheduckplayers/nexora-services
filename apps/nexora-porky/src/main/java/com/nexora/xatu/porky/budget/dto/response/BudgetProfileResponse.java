package com.nexora.xatu.porky.budget.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record BudgetProfileResponse(
    String userId,
    BigDecimal monthlyIncome,
    List<CategoryAllocationResponse> allocations,
    Instant updatedAt) {}
