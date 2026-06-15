package com.nexora.xatu.chansey.weight.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WeightProgressPoint(String label, LocalDate periodStart, BigDecimal weightKg) {}
