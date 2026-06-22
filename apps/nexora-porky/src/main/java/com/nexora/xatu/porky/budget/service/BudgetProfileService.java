package com.nexora.xatu.porky.budget.service;

import com.nexora.xatu.porky.budget.dto.request.UpdateBudgetProfileRequest;
import com.nexora.xatu.porky.budget.dto.response.BudgetProfileResponse;
import com.nexora.xatu.porky.budget.dto.response.CategoryAllocationResponse;
import com.nexora.xatu.porky.budget.model.BudgetProfile;
import com.nexora.xatu.porky.budget.repository.BudgetProfileRepository;
import com.nexora.xatu.porky.shared.enums.BudgetCategory;
import com.nexora.xatu.porky.shared.service.JwtUserService;
import com.nexora.xatu.porky.shared.util.GoalPurposeDefaults;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class BudgetProfileService {

  private final BudgetProfileRepository budgetProfileRepository;
  private final JwtUserService jwtUserService;

  public BudgetProfileService(
      BudgetProfileRepository budgetProfileRepository, JwtUserService jwtUserService) {
    this.budgetProfileRepository = budgetProfileRepository;
    this.jwtUserService = jwtUserService;
  }

  public BudgetProfileResponse find(Jwt jwt) {
    String userId = jwtUserService.requireUserId(jwt);

    return budgetProfileRepository
        .findByUserId(userId)
        .map(BudgetProfile::toDto)
        .orElse(emptyProfile(userId));
  }

  public BudgetProfileResponse save(Jwt jwt, UpdateBudgetProfileRequest request) {
    String userId = jwtUserService.requireUserId(jwt);
    BudgetProfile profile =
        budgetProfileRepository
            .findByUserId(userId)
            .orElseGet(
                () -> {
                  BudgetProfile created = new BudgetProfile();
                  created.setUserId(userId);
                  return created;
                });

    profile.update(request);

    return budgetProfileRepository.save(profile).toDto();
  }

  public List<CategoryAllocationResponse> findRecommendations() {
    return Arrays.asList(
        recommendation(BudgetCategory.HOUSING, 30),
        recommendation(BudgetCategory.FOOD, 15),
        recommendation(BudgetCategory.TRANSPORT, 10),
        recommendation(BudgetCategory.HEALTH, 5),
        recommendation(BudgetCategory.INVESTMENT, 15),
        recommendation(BudgetCategory.SAVINGS, 10),
        recommendation(BudgetCategory.LEISURE, 10),
        recommendation(BudgetCategory.BETTING, 5));
  }

  private CategoryAllocationResponse recommendation(BudgetCategory category, int percentage) {
    return new CategoryAllocationResponse(
        category,
        BigDecimal.valueOf(percentage),
        null,
        GoalPurposeDefaults.defaultForCategory(category));
  }

  public BudgetProfile findEntity(Jwt jwt) {
    String userId = jwtUserService.requireUserId(jwt);

    return budgetProfileRepository.findByUserId(userId).orElse(null);
  }

  private BudgetProfileResponse emptyProfile(String userId) {
    return new BudgetProfileResponse(userId, null, List.of(), null);
  }
}
