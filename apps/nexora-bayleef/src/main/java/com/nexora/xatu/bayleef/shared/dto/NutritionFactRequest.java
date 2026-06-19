package com.nexora.xatu.bayleef.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NutritionFactRequest(
    @NotBlank @Size(max = 120) String nutrient,
    @NotBlank String value,
    @Size(max = 32) String unit) {}
