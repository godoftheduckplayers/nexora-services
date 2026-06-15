package com.nexora.xatu.chansey.dashboard.service;

import com.nexora.xatu.chansey.dashboard.dto.response.DailyDashboardResponse;
import com.nexora.xatu.chansey.dashboard.dto.response.GoalProgress;
import com.nexora.xatu.chansey.integration.bayleef.BayleefClient;
import com.nexora.xatu.chansey.integration.bayleef.BayleefDailyConsumptionResponse;
import com.nexora.xatu.chansey.integration.bayleef.BayleefNutritionValues;
import com.nexora.xatu.chansey.metabolism.dto.response.MetabolismGoalsResponse;
import com.nexora.xatu.chansey.metabolism.service.MetabolismService;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class DashboardService {

  private final MetabolismService metabolismService;
  private final BayleefClient bayleefClient;

  public DashboardService(MetabolismService metabolismService, BayleefClient bayleefClient) {
    this.metabolismService = metabolismService;
    this.bayleefClient = bayleefClient;
  }

  public DailyDashboardResponse findDaily(Jwt jwt, LocalDate date) {
    LocalDate targetDate = date == null ? LocalDate.now(ZoneOffset.UTC) : date;
    MetabolismGoalsResponse metabolism = metabolismService.findGoals(jwt);
    BayleefDailyConsumptionResponse consumption =
        bayleefClient.fetchDailyConsumption(resolveAuthorizationHeader(), targetDate);

    GoalProgress kcal =
        buildProgress(metabolism.targetKcal(), safeValue(consumption.totals(), "kcal"));
    GoalProgress protein =
        buildProgress(metabolism.targetProteinG(), safeValue(consumption.totals(), "proteins"));

    return new DailyDashboardResponse(targetDate, metabolism, consumption, kcal, protein);
  }

  private GoalProgress buildProgress(BigDecimal target, BigDecimal consumed) {
    BigDecimal safeTarget = target == null ? BigDecimal.ZERO : target;
    BigDecimal safeConsumed = consumed == null ? BigDecimal.ZERO : consumed;
    BigDecimal remaining = safeTarget.subtract(safeConsumed);
    Integer progressPercent = null;

    if (safeTarget.compareTo(BigDecimal.ZERO) > 0) {
      progressPercent =
          safeConsumed
              .multiply(BigDecimal.valueOf(100))
              .divide(safeTarget, 0, RoundingMode.HALF_UP)
              .intValue();
    }

    return new GoalProgress(safeTarget, safeConsumed, remaining, progressPercent);
  }

  private BigDecimal safeValue(BayleefNutritionValues totals, String field) {
    if (totals == null) {
      return BigDecimal.ZERO;
    }

    return switch (field) {
      case "kcal" -> totals.kcal() == null ? BigDecimal.ZERO : totals.kcal();
      case "proteins" -> totals.proteins() == null ? BigDecimal.ZERO : totals.proteins();
      default -> BigDecimal.ZERO;
    };
  }

  private String resolveAuthorizationHeader() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    if (attributes == null) {
      throw new IllegalStateException("Request context is unavailable.");
    }

    HttpServletRequest request = attributes.getRequest();
    String authorization = request.getHeader("Authorization");

    if (authorization == null || authorization.isBlank()) {
      throw new IllegalArgumentException("Authorization header is required.");
    }

    return authorization;
  }
}
