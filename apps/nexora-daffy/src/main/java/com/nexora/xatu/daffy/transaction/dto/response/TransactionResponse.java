package com.nexora.xatu.daffy.transaction.dto.response;

import com.nexora.xatu.daffy.shared.enums.BudgetCategory;
import com.nexora.xatu.daffy.shared.enums.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record TransactionResponse(
    String id,
    TransactionType type,
    BudgetCategory category,
    BigDecimal amount,
    String description,
    LocalDate occurredOn,
    Instant occurredAt) {}
