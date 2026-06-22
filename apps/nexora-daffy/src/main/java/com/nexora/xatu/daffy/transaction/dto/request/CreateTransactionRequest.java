package com.nexora.xatu.daffy.transaction.dto.request;

import com.nexora.xatu.daffy.shared.enums.BudgetCategory;
import com.nexora.xatu.daffy.shared.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record CreateTransactionRequest(
    @NotNull TransactionType type,
    @NotNull BudgetCategory category,
    @NotNull @DecimalMin("0.01") BigDecimal amount,
    String description,
    LocalDate occurredOn,
    Instant occurredAt) {}
