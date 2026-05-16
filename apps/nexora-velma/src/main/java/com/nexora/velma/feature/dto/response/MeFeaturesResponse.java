package com.nexora.velma.feature.dto.response;

import java.util.Map;

public record MeFeaturesResponse(
    String userId, String profile, String plan, Map<String, Boolean> features) {}
