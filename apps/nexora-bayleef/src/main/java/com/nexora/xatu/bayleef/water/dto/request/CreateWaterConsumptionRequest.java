package com.nexora.xatu.bayleef.water.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

public record CreateWaterConsumptionRequest(
    @NotNull @Positive Integer volumeMl, Instant consumedAt, String note) {}
