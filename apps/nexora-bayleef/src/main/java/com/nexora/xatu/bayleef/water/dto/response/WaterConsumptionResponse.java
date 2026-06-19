package com.nexora.xatu.bayleef.water.dto.response;

import java.time.Instant;
import java.time.LocalDate;

public record WaterConsumptionResponse(
    String id, Integer volumeMl, String note, LocalDate consumedOn, Instant consumedAt) {}
