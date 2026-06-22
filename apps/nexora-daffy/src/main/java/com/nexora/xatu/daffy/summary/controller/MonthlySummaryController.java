package com.nexora.xatu.daffy.summary.controller;

import com.nexora.xatu.daffy.summary.dto.response.MonthlyLedgerSummary;
import com.nexora.xatu.daffy.summary.dto.response.PeriodLedgerSummary;
import com.nexora.xatu.daffy.summary.service.MonthlySummaryService;
import java.time.LocalDate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/summary")
public class MonthlySummaryController {

  private final MonthlySummaryService monthlySummaryService;

  public MonthlySummaryController(MonthlySummaryService monthlySummaryService) {
    this.monthlySummaryService = monthlySummaryService;
  }

  @GetMapping("/monthly")
  public MonthlyLedgerSummary findMonthly(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month) {
    return monthlySummaryService.findMonthly(jwt, year, month);
  }

  @GetMapping("/period")
  public PeriodLedgerSummary findPeriod(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) LocalDate from,
      @RequestParam(required = false) LocalDate to) {
    return monthlySummaryService.findPeriod(jwt, from, to);
  }
}
