package com.nexora.xatu.chansey.profile.dto.request;

import com.nexora.xatu.chansey.shared.enums.ActivityLevel;
import com.nexora.xatu.chansey.shared.enums.GoalType;
import com.nexora.xatu.chansey.shared.enums.Sex;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public record UpdateHealthProfileRequest(
    @NotNull @Min(100) @Max(250) Integer heightCm,
    @NotNull @Past LocalDate birthDate,
    @NotNull Sex sex,
    @NotNull ActivityLevel activityLevel,
    @NotNull GoalType goalType) {}
