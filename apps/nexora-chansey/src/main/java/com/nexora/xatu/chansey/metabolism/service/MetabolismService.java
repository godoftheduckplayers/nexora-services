package com.nexora.xatu.chansey.metabolism.service;

import com.nexora.xatu.chansey.metabolism.dto.response.MetabolismGoalsResponse;
import com.nexora.xatu.chansey.profile.model.HealthProfile;
import com.nexora.xatu.chansey.profile.service.HealthProfileService;
import com.nexora.xatu.chansey.weight.service.WeightEntryService;
import java.math.BigDecimal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class MetabolismService {

  private final HealthProfileService healthProfileService;
  private final WeightEntryService weightEntryService;
  private final MetabolismCalculator metabolismCalculator;

  public MetabolismService(
      HealthProfileService healthProfileService,
      WeightEntryService weightEntryService,
      MetabolismCalculator metabolismCalculator) {
    this.healthProfileService = healthProfileService;
    this.weightEntryService = weightEntryService;
    this.metabolismCalculator = metabolismCalculator;
  }

  public MetabolismGoalsResponse findGoals(Jwt jwt) {
    HealthProfile profile = null;

    try {
      profile = healthProfileService.findEntity(jwt);
    } catch (IllegalArgumentException ignored) {
      // Profile not configured yet.
    }

    boolean profileComplete = profile != null;
    BigDecimal latestWeight = weightEntryService.findLatestWeightKg(jwt).orElse(null);
    boolean weightAvailable = latestWeight != null;

    return metabolismCalculator
        .compute(profile, latestWeight)
        .map(
            computation ->
                new MetabolismGoalsResponse(
                    computation.weightKg(),
                    computation.ageYears(),
                    computation.bmrKcal(),
                    computation.tdeeKcal(),
                    computation.targetKcal(),
                    computation.targetProteinG(),
                    computation.targetWaterMl(),
                    profileComplete,
                    weightAvailable))
        .orElseGet(
            () ->
                new MetabolismGoalsResponse(
                    latestWeight,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    profileComplete,
                    weightAvailable));
  }
}
