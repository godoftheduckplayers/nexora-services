package com.nexora.xatu.chansey.integration.bayleef;

import java.time.LocalDate;
import java.util.List;

public record BayleefDailyConsumptionResponse(
    LocalDate date, List<BayleefConsumptionItem> items, BayleefNutritionValues totals) {}
