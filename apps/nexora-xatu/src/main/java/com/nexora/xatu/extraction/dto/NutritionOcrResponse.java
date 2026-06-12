package com.nexora.xatu.extraction.dto;

import com.nexora.xatu.dto.NutritionFactResponse;
import java.util.List;

public record NutritionOcrResponse(
    String servingSize, List<NutritionFactResponse> nutritionFacts, String rawText) {}
