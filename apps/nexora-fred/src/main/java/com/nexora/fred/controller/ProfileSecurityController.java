package com.nexora.fred.controller;

import com.nexora.fred.service.ProfileSecurityService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me/security")
public class ProfileSecurityController {

  private final ProfileSecurityService service;

  public ProfileSecurityController(ProfileSecurityService service) {
    this.service = service;
  }

  @PostMapping("/reset-password")
  public void resetPassword(@AuthenticationPrincipal Jwt jwt) {
    service.sendResetPasswordEmail(jwt.getSubject());
  }
}
