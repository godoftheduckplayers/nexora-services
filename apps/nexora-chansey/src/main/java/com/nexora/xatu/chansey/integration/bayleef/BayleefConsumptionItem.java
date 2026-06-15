package com.nexora.xatu.chansey.integration.bayleef;

import java.math.BigDecimal;

public record BayleefConsumptionItem(
    String id,
    String foodName,
    BigDecimal quantityGrams,
    BayleefNutritionValues nutritionConsumed) {}
