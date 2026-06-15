package com.nexora.xatu.bayleef.food.dto.response;

import com.nexora.xatu.bayleef.shared.model.NutritionValues;
import java.time.Instant;

public record FoodResponse(
    String id,
    String name,
    NutritionValues nutritionPer100g,
    Instant createdAt,
    Instant updatedAt) {}
