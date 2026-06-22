package com.nexora.xatu.porky.budget.controller;

import com.nexora.xatu.porky.budget.dto.request.UpdateBudgetProfileRequest;
import com.nexora.xatu.porky.budget.dto.response.BudgetProfileResponse;
import com.nexora.xatu.porky.budget.dto.response.CategoryAllocationResponse;
import com.nexora.xatu.porky.budget.service.BudgetProfileService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budget-profile")
public class BudgetProfileController {

  private final BudgetProfileService budgetProfileService;

  public BudgetProfileController(BudgetProfileService budgetProfileService) {
    this.budgetProfileService = budgetProfileService;
  }

  @GetMapping
  public BudgetProfileResponse find(@AuthenticationPrincipal Jwt jwt) {
    return budgetProfileService.find(jwt);
  }

  @PutMapping
  public BudgetProfileResponse save(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateBudgetProfileRequest request) {
    return budgetProfileService.save(jwt, request);
  }

  @GetMapping("/recommendations")
  public List<CategoryAllocationResponse> findRecommendations() {
    return budgetProfileService.findRecommendations();
  }
}
