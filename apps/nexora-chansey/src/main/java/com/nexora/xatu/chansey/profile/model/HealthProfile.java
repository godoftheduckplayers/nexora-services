package com.nexora.xatu.chansey.profile.model;

import com.nexora.xatu.chansey.profile.dto.request.UpdateHealthProfileRequest;
import com.nexora.xatu.chansey.profile.dto.response.HealthProfileResponse;
import com.nexora.xatu.chansey.shared.enums.ActivityLevel;
import com.nexora.xatu.chansey.shared.enums.GoalType;
import com.nexora.xatu.chansey.shared.enums.Sex;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "health_profiles")
public class HealthProfile {

  @Id private String id;

  @Indexed(unique = true)
  private String userId;

  private Integer heightCm;
  private LocalDate birthDate;
  private Sex sex;
  private ActivityLevel activityLevel;
  private GoalType goalType;
  private Instant updatedAt;

  public static HealthProfile from(String userId, UpdateHealthProfileRequest request) {
    HealthProfile profile = new HealthProfile();

    profile.setUserId(userId);
    profile.apply(request);
    profile.setUpdatedAt(Instant.now());

    return profile;
  }

  public void apply(UpdateHealthProfileRequest request) {
    this.heightCm = request.heightCm();
    this.birthDate = request.birthDate();
    this.sex = request.sex();
    this.activityLevel = request.activityLevel();
    this.goalType = request.goalType();
    this.updatedAt = Instant.now();
  }

  public HealthProfileResponse toDto() {
    return new HealthProfileResponse(
        userId, heightCm, birthDate, sex, activityLevel, goalType, updatedAt);
  }
}
