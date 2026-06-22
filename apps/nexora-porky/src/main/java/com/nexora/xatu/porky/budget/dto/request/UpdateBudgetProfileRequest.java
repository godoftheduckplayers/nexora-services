package com.nexora.xatu.porky.budget.dto.request;

import com.nexora.xatu.porky.shared.enums.BudgetCategory;
import com.nexora.xatu.porky.shared.enums.GoalPurpose;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record UpdateBudgetProfileRequest(
    @NotNull @DecimalMin("0.01") BigDecimal monthlyIncome,
    @NotEmpty List<CategoryAllocationRequest> allocations) {

  public record CategoryAllocationRequest(
      @NotNull BudgetCategory category,
      @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal percentage,
      String label,
      GoalPurpose purpose) {}
}
