package com.nexora.fred.profile.controller;

import com.nexora.fred.profile.dto.request.UpdateProfileRequest;
import com.nexora.fred.profile.service.ProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me/profile")
public class ProfileController {

  private final ProfileService profileService;

  public ProfileController(ProfileService profileService) {
    this.profileService = profileService;
  }

  @PatchMapping
  public void updateProfile(
      @AuthenticationPrincipal Jwt jwt, @RequestBody UpdateProfileRequest request) {
    profileService.updateProfile(jwt.getSubject(), request);
  }
}
