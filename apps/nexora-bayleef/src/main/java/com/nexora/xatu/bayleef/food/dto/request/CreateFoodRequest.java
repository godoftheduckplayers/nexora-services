package com.nexora.xatu.bayleef.food.dto.request;

import com.nexora.xatu.bayleef.shared.dto.NutritionValuesRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateFoodRequest(
    @NotBlank @Size(max = 120) String name,
    @Valid NutritionValuesRequest nutritionPer100g,
    @Positive BigDecimal referenceServingGrams) {}
