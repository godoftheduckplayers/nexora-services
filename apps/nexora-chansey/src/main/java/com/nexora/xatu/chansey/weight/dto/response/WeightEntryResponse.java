package com.nexora.xatu.chansey.weight.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record WeightEntryResponse(
    String id, LocalDate recordedOn, BigDecimal weightKg, String note, Instant recordedAt) {}
