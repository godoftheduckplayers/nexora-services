package com.nexora.xatu.velma.app.dto.response;

import java.time.Instant;

public record AppResponse(
    String id,
    String key,
    String name,
    String description,
    String icon,
    String route,
    String remoteEntry,
    String exposedModule,
    Boolean enabled,
    Instant createdAt,
    Instant updatedAt) {}
