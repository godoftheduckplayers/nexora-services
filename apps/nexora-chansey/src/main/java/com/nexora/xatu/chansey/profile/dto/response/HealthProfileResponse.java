package com.nexora.xatu.chansey.profile.dto.response;

import com.nexora.xatu.chansey.shared.enums.ActivityLevel;
import com.nexora.xatu.chansey.shared.enums.GoalType;
import com.nexora.xatu.chansey.shared.enums.Sex;
import java.time.Instant;
import java.time.LocalDate;

public record HealthProfileResponse(
    String userId,
    Integer heightCm,
    LocalDate birthDate,
    Sex sex,
    ActivityLevel activityLevel,
    GoalType goalType,
    Instant updatedAt) {}
