package com.nexora.xatu.bayleef.consumption.dto.response;

import com.nexora.xatu.bayleef.shared.model.NutritionValues;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ConsumptionResponse(
    String id,
    String foodId,
    String foodName,
    BigDecimal quantityGrams,
    LocalDate consumedOn,
    Instant consumedAt,
    NutritionValues nutritionPer100g,
    NutritionValues nutritionConsumed) {}
