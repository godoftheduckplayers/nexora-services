package com.nexora.xatu.chansey.metabolism.controller;

import com.nexora.xatu.chansey.metabolism.dto.response.MetabolismGoalsResponse;
import com.nexora.xatu.chansey.metabolism.service.MetabolismService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metabolism")
public class MetabolismController {

  private final MetabolismService metabolismService;

  public MetabolismController(MetabolismService metabolismService) {
    this.metabolismService = metabolismService;
  }

  @GetMapping
  public MetabolismGoalsResponse findGoals(@AuthenticationPrincipal Jwt jwt) {
    return metabolismService.findGoals(jwt);
  }
}
