package com.nexora.xatu.chansey.profile.service;

import com.nexora.xatu.chansey.profile.dto.request.UpdateHealthProfileRequest;
import com.nexora.xatu.chansey.profile.dto.response.HealthProfileResponse;
import com.nexora.xatu.chansey.profile.model.HealthProfile;
import com.nexora.xatu.chansey.profile.repository.HealthProfileRepository;
import com.nexora.xatu.chansey.shared.service.JwtUserService;
import java.util.Optional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class HealthProfileService {

  private final HealthProfileRepository healthProfileRepository;
  private final JwtUserService jwtUserService;

  public HealthProfileService(
      HealthProfileRepository healthProfileRepository, JwtUserService jwtUserService) {
    this.healthProfileRepository = healthProfileRepository;
    this.jwtUserService = jwtUserService;
  }

  public Optional<HealthProfileResponse> findOptional(Jwt jwt) {
    String userId = jwtUserService.requireUserId(jwt);

    return healthProfileRepository.findByUserId(userId).map(HealthProfile::toDto);
  }

  public HealthProfileResponse find(Jwt jwt) {
    return findEntity(jwt).toDto();
  }

  public HealthProfileResponse upsert(Jwt jwt, UpdateHealthProfileRequest request) {
    String userId = jwtUserService.requireUserId(jwt);

    HealthProfile profile =
        healthProfileRepository
            .findByUserId(userId)
            .map(
                existing -> {
                  existing.apply(request);
                  return existing;
                })
            .orElseGet(() -> HealthProfile.from(userId, request));

    return healthProfileRepository.save(profile).toDto();
  }

  public HealthProfile findEntity(Jwt jwt) {
    String userId = jwtUserService.requireUserId(jwt);

    return healthProfileRepository
        .findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("Health profile not found."));
  }
}
