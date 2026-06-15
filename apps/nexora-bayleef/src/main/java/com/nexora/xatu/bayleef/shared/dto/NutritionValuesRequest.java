package com.nexora.xatu.bayleef.shared.dto;

import com.nexora.xatu.bayleef.shared.model.NutritionValues;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record NutritionValuesRequest(
    @PositiveOrZero BigDecimal kcal,
    @PositiveOrZero BigDecimal proteins,
    @PositiveOrZero BigDecimal carbs,
    @PositiveOrZero BigDecimal saturatedFat,
    @PositiveOrZero BigDecimal fat,
    @PositiveOrZero BigDecimal fiber) {

  public NutritionValues toModel() {
    NutritionValues values = new NutritionValues();

    values.setKcal(kcal);
    values.setProteins(proteins);
    values.setCarbs(carbs);
    values.setSaturatedFat(saturatedFat);
    values.setFat(fat);
    values.setFiber(fiber);

    return values;
  }
}
