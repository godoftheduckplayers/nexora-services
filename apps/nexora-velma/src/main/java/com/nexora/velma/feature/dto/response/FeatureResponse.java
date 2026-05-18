package com.nexora.velma.feature.dto.response;

import com.nexora.velma.shared.enums.FeatureCategory;
import java.time.Instant;

public record FeatureResponse(
    String id,
    String key,
    String appKey,
    String name,
    String description,
    FeatureCategory category,
    Boolean enabled,
    Boolean globalEnabled,
    Instant createdAt,
    Instant updatedAt) {}
