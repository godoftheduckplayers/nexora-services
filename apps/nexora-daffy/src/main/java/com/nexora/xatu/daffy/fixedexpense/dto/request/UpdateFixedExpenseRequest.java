package com.nexora.xatu.daffy.fixedexpense.dto.request;

import com.nexora.xatu.daffy.shared.enums.BudgetCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UpdateFixedExpenseRequest(
    @NotBlank String name,
    @NotNull @DecimalMin("0.01") BigDecimal amount,
    @NotNull BudgetCategory category,
    @NotNull @Min(1) @Max(28) Integer dayOfMonth,
    Boolean active) {}
