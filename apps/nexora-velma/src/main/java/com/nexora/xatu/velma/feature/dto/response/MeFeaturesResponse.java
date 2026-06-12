package com.nexora.xatu.velma.feature.dto.response;

import com.nexora.xatu.velma.app.dto.response.AppResponse;
import java.util.List;
import java.util.Map;

public record MeFeaturesResponse(
    String userId,
    String profile,
    String plan,
    List<AppResponse> apps,
    Map<String, Boolean> features) {}
