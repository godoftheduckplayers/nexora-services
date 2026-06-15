package com.nexora.xatu.chansey.profile.controller;

import com.nexora.xatu.chansey.profile.dto.request.UpdateHealthProfileRequest;
import com.nexora.xatu.chansey.profile.dto.response.HealthProfileResponse;
import com.nexora.xatu.chansey.profile.service.HealthProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health-profile")
public class HealthProfileController {

  private final HealthProfileService healthProfileService;

  public HealthProfileController(HealthProfileService healthProfileService) {
    this.healthProfileService = healthProfileService;
  }

  @GetMapping
  public ResponseEntity<HealthProfileResponse> find(@AuthenticationPrincipal Jwt jwt) {
    return healthProfileService
        .findOptional(jwt)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.noContent().build());
  }

  @PutMapping
  public HealthProfileResponse upsert(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateHealthProfileRequest request) {
    return healthProfileService.upsert(jwt, request);
  }
}
