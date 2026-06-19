package com.nexora.xatu.bayleef.food.dto.response;

import com.nexora.xatu.bayleef.shared.dto.NutritionFactResponse;
import com.nexora.xatu.bayleef.shared.model.NutritionValues;
import com.nexora.xatu.bayleef.shared.model.ServingUnit;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record FoodResponse(
    String id,
    String name,
    String servingSize,
    BigDecimal referenceServingGrams,
    ServingUnit referenceServingUnit,
    List<NutritionFactResponse> nutritionFacts,
    NutritionValues nutritionPer100g,
    Instant createdAt,
    Instant updatedAt) {}
