package com.nexora.xatu.porky.dashboard.controller;

import com.nexora.xatu.porky.dashboard.dto.response.MonthlyDashboardResponse;
import com.nexora.xatu.porky.dashboard.dto.response.PeriodDashboardResponse;
import com.nexora.xatu.porky.dashboard.service.DashboardService;
import java.time.LocalDate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/monthly")
  public MonthlyDashboardResponse findMonthly(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month) {
    return dashboardService.findMonthly(jwt, year, month);
  }

  @GetMapping("/period")
  public PeriodDashboardResponse findPeriod(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) LocalDate from,
      @RequestParam(required = false) LocalDate to) {
    return dashboardService.findPeriod(jwt, from, to);
  }
}
