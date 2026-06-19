package com.nexora.xatu.chansey.integration.bayleef;

import java.math.BigDecimal;

public record BayleefNutritionValues(
    BigDecimal kcal,
    BigDecimal proteins,
    BigDecimal carbs,
    BigDecimal saturatedFat,
    BigDecimal transFat,
    BigDecimal fat,
    BigDecimal fiber) {}
