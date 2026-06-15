package com.nexora.xatu.chansey.weight.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UpsertWeightEntryRequest(
    LocalDate recordedOn,
    @NotNull @DecimalMin("20.0") @DecimalMax("400.0") BigDecimal weightKg,
    @Size(max = 200) String note) {}
