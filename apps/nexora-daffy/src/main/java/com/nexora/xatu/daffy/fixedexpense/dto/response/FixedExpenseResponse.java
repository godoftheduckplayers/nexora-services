package com.nexora.xatu.daffy.fixedexpense.dto.response;

import com.nexora.xatu.daffy.shared.enums.BudgetCategory;
import java.math.BigDecimal;

public record FixedExpenseResponse(
    String id,
    String name,
    BigDecimal amount,
    BudgetCategory category,
    Integer dayOfMonth,
    boolean active,
    boolean paidThisMonth) {}
