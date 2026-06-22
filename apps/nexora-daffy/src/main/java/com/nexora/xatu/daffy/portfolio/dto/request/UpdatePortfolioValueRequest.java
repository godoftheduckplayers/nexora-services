package com.nexora.xatu.daffy.portfolio.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UpdatePortfolioValueRequest(@NotNull @DecimalMin("0") BigDecimal currentValue) {}
