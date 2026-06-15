package com.nexora.xatu.chansey.metabolism.dto.response;

import java.math.BigDecimal;

public record MetabolismGoalsResponse(
    BigDecimal weightKg,
    Integer ageYears,
    BigDecimal bmrKcal,
    BigDecimal tdeeKcal,
    BigDecimal targetKcal,
    BigDecimal targetProteinG,
    BigDecimal targetWaterMl,
    boolean profileComplete,
    boolean weightAvailable) {}
