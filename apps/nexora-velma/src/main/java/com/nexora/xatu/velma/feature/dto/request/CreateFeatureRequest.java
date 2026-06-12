package com.nexora.xatu.velma.feature.dto.request;

import com.nexora.xatu.velma.shared.enums.FeatureCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateFeatureRequest(
    @NotBlank(message = "Feature key is required")
        @Size(max = 120, message = "Feature key must have at most 120 characters")
        String key,
    @NotBlank(message = "Feature name is required")
        @Size(max = 120, message = "Feature name must have at most 120 characters")
        String name,
    @Size(max = 500, message = "Description must have at most 500 characters") String description,
    @NotNull(message = "Category is required") FeatureCategory category,
    @NotNull(message = "Enabled flag is required") Boolean enabled,
    @NotNull(message = "Global enabled flag is required") Boolean globalEnabled) {}
