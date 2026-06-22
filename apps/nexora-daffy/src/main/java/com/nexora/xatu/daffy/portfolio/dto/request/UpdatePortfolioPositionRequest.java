package com.nexora.xatu.daffy.portfolio.dto.request;

import com.nexora.xatu.daffy.shared.enums.PortfolioType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UpdatePortfolioPositionRequest(
    @NotBlank String name,
    @NotNull PortfolioType type,
    @NotNull @DecimalMin("0.01") BigDecimal investedAmount,
    @DecimalMin("0") BigDecimal currentValue) {}
