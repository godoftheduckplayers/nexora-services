package com.nexora.velma.feature.dto.request;

import com.nexora.velma.shared.enums.FeatureCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateFeatureRequest(
    @NotBlank String name,
    String description,
    @NotNull FeatureCategory category,
    @NotNull Boolean enabled,
    @NotNull Boolean globalEnabled) {}
