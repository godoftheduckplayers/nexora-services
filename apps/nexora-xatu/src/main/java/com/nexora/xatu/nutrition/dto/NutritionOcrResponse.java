package com.nexora.xatu.nutrition.dto;

import java.util.List;

public record NutritionOcrResponse(
    String servingSize, List<NutritionFactResponse> nutritionFacts, String rawText) {}
