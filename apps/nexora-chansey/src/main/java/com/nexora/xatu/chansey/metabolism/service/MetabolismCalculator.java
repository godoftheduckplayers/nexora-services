package com.nexora.xatu.chansey.metabolism.service;

import com.nexora.xatu.chansey.profile.model.HealthProfile;
import com.nexora.xatu.chansey.shared.enums.ActivityLevel;
import com.nexora.xatu.chansey.shared.enums.GoalType;
import com.nexora.xatu.chansey.shared.enums.Sex;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MetabolismCalculator {

  public Optional<MetabolismComputation> compute(HealthProfile profile, BigDecimal weightKg) {
    if (profile == null
        || profile.getHeightCm() == null
        || profile.getBirthDate() == null
        || profile.getSex() == null
        || profile.getActivityLevel() == null
        || profile.getGoalType() == null
        || weightKg == null) {
      return Optional.empty();
    }

    int ageYears = Period.between(profile.getBirthDate(), LocalDate.now(ZoneOffset.UTC)).getYears();
    BigDecimal bmr = calculateBmr(weightKg, profile.getHeightCm(), ageYears, profile.getSex());
    BigDecimal tdee = bmr.multiply(activityFactor(profile.getActivityLevel()));
    BigDecimal targetKcal = applyGoalAdjustment(tdee, profile.getGoalType());
    BigDecimal targetProteinG = weightKg.multiply(proteinFactor(profile.getGoalType()));
    BigDecimal targetWaterMl = weightKg.multiply(BigDecimal.valueOf(35));

    return Optional.of(
        new MetabolismComputation(
            weightKg,
            ageYears,
            scale(bmr),
            scale(tdee),
            scale(targetKcal),
            scale(targetProteinG),
            scale(targetWaterMl)));
  }

  private BigDecimal calculateBmr(BigDecimal weightKg, int heightCm, int ageYears, Sex sex) {
    BigDecimal base =
        weightKg
            .multiply(BigDecimal.TEN)
            .add(BigDecimal.valueOf(heightCm).multiply(BigDecimal.valueOf(6.25)))
            .subtract(BigDecimal.valueOf(ageYears).multiply(BigDecimal.valueOf(5)));

    return sex == Sex.MALE ? base.add(BigDecimal.valueOf(5)) : base.subtract(BigDecimal.valueOf(161));
  }

  private BigDecimal activityFactor(ActivityLevel activityLevel) {
    return switch (activityLevel) {
      case SEDENTARY -> BigDecimal.valueOf(1.2);
      case LIGHT -> BigDecimal.valueOf(1.375);
      case MODERATE -> BigDecimal.valueOf(1.55);
      case ACTIVE -> BigDecimal.valueOf(1.725);
      case VERY_ACTIVE -> BigDecimal.valueOf(1.9);
    };
  }

  private BigDecimal applyGoalAdjustment(BigDecimal tdee, GoalType goalType) {
    return switch (goalType) {
      case LOSE -> tdee.subtract(BigDecimal.valueOf(500));
      case GAIN -> tdee.add(BigDecimal.valueOf(300));
      case MAINTAIN -> tdee;
    };
  }

  private BigDecimal proteinFactor(GoalType goalType) {
    return switch (goalType) {
      case LOSE -> BigDecimal.valueOf(1.8);
      case GAIN -> BigDecimal.valueOf(2.0);
      case MAINTAIN -> BigDecimal.valueOf(1.6);
    };
  }

  private BigDecimal scale(BigDecimal value) {
    return value.setScale(0, RoundingMode.HALF_UP);
  }

  public record MetabolismComputation(
      BigDecimal weightKg,
      Integer ageYears,
      BigDecimal bmrKcal,
      BigDecimal tdeeKcal,
      BigDecimal targetKcal,
      BigDecimal targetProteinG,
      BigDecimal targetWaterMl) {}
}
