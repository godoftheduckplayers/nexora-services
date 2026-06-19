package com.nexora.xatu.bayleef.water.dto.response;

import java.time.LocalDate;
import java.util.List;

public record DailyWaterConsumptionResponse(
    LocalDate date, List<WaterConsumptionResponse> items, Integer totalMl) {}
