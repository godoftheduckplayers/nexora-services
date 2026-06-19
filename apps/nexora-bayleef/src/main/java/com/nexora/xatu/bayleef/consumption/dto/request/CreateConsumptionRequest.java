package com.nexora.xatu.bayleef.consumption.dto.request;

import com.nexora.xatu.bayleef.shared.model.ServingUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;

public record CreateConsumptionRequest(
    @NotBlank String foodId,
    @NotNull @Positive BigDecimal quantityGrams,
    @NotNull ServingUnit quantityUnit,
    Instant consumedAt) {}
