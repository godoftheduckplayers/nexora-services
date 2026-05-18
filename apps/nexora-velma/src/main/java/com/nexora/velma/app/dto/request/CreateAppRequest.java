package com.nexora.velma.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAppRequest(
    @NotBlank(message = "Key is required")
        @Size(max = 80, message = "Key must have at most 80 characters")
        String key,
    @NotBlank(message = "Name is required")
        @Size(max = 120, message = "Name must have at most 120 characters")
        String name,
    @Size(max = 500, message = "Description must have at most 500 characters") String description,
    @NotBlank(message = "Icon is required")
        @Size(max = 80, message = "Icon must have at most 80 characters")
        String icon,
    @NotBlank(message = "Route is required")
        @Size(max = 160, message = "Route must have at most 160 characters")
        String route,
    @Size(max = 255, message = "Remote entry must have at most 255 characters") String remoteEntry,
    @Size(max = 80, message = "Exposed module must have at most 80 characters")
        String exposedModule,
    @NotNull(message = "Enabled is required") Boolean enabled) {}
