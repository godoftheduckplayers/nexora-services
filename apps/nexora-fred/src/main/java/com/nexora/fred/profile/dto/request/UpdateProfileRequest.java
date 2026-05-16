package com.nexora.fred.profile.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must have at most 100 characters")
        String firstName,
    @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must have at most 100 characters")
        String lastName) {}
