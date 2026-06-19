package com.nexora.xatu.bayleef.consumption.dto.request;

import com.nexora.xatu.bayleef.shared.model.ServingUnit;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;

public record UpdateConsumptionRequest(
    @NotNull @Positive BigDecimal quantityGrams,
    @NotNull ServingUnit quantityUnit,
    Instant consumedAt) {}
