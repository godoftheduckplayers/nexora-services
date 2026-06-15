package com.nexora.xatu.bayleef.consumption.dto.response;

import com.nexora.xatu.bayleef.shared.model.NutritionValues;
import java.time.LocalDate;
import java.util.List;

public record DailyConsumptionResponse(
    LocalDate date, List<ConsumptionResponse> items, NutritionValues totals) {}
