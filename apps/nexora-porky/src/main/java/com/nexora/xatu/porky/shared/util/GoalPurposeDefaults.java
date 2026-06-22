package com.nexora.xatu.porky.shared.util;

import com.nexora.xatu.porky.shared.enums.BudgetCategory;
import com.nexora.xatu.porky.shared.enums.GoalPurpose;

public final class GoalPurposeDefaults {

  private GoalPurposeDefaults() {}

  public static GoalPurpose resolve(BudgetCategory category, GoalPurpose purpose) {
    if (purpose != null) {
      return purpose;
    }

    return defaultForCategory(category);
  }

  public static GoalPurpose defaultForCategory(BudgetCategory category) {
    return switch (category) {
      case INVESTMENT, SAVINGS, BETTING -> GoalPurpose.SAVING;
      default -> GoalPurpose.SPENDING;
    };
  }
}
