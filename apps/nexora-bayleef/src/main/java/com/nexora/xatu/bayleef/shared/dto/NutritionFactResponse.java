package com.nexora.xatu.bayleef.shared.dto;

public record NutritionFactResponse(
    String nutrient, String value, String unit, String dailyValuePercentage) {}
