package com.nexora.velma.feature.controller;

import com.nexora.velma.feature.dto.response.MeFeaturesResponse;
import com.nexora.velma.feature.service.MeFeatureService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeFeatureController {

  private final MeFeatureService meFeatureService;

  public MeFeatureController(MeFeatureService meFeatureService) {
    this.meFeatureService = meFeatureService;
  }

  @GetMapping("/api/me/features")
  public MeFeaturesResponse findMyFeatures(@AuthenticationPrincipal Jwt jwt) {
    return meFeatureService.findMyFeatures(jwt);
  }
}
