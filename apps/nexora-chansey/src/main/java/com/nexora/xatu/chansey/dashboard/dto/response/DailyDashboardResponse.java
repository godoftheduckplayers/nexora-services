package com.nexora.xatu.chansey.dashboard.dto.response;

import com.nexora.xatu.chansey.integration.bayleef.BayleefDailyConsumptionResponse;
import com.nexora.xatu.chansey.metabolism.dto.response.MetabolismGoalsResponse;
import java.time.LocalDate;

public record DailyDashboardResponse(
    LocalDate date,
    MetabolismGoalsResponse metabolism,
    BayleefDailyConsumptionResponse consumption,
    GoalProgress kcal,
    GoalProgress protein) {}
