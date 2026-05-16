package com.nexora.velma.feature.dto.response;

import com.nexora.velma.shared.enums.FeatureCategory;

public record FeatureResponse(
    String id,
    String key,
    String name,
    String description,
    FeatureCategory category,
    Boolean enabled,
    Boolean globalEnabled) {}
