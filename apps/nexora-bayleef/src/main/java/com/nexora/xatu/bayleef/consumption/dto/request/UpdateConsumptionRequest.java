package com.nexora.xatu.bayleef.consumption.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;

public record UpdateConsumptionRequest(
    @NotNull @Positive BigDecimal quantityGrams, Instant consumedAt) {}
