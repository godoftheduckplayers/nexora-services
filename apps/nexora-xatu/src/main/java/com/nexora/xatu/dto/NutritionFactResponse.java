package com.nexora.xatu.dto;

public record NutritionFactResponse(
    String nutrient, String value, String unit, String dailyValuePercentage) {}
