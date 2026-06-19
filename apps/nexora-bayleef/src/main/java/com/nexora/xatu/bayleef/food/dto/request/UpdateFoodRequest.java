package com.nexora.xatu.bayleef.food.dto.request;

import com.nexora.xatu.bayleef.shared.dto.NutritionFactRequest;
import com.nexora.xatu.bayleef.shared.model.ServingUnit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record UpdateFoodRequest(
    @NotBlank @Size(max = 120) String name,
    @Size(max = 64) String servingSize,
    @Positive BigDecimal referenceServingGrams,
    @NotNull ServingUnit referenceServingUnit,
    @NotEmpty @Valid List<NutritionFactRequest> nutritionFacts) {}
