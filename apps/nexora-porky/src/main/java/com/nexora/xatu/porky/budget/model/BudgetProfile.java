package com.nexora.xatu.porky.budget.model;

import com.nexora.xatu.porky.budget.dto.request.UpdateBudgetProfileRequest;
import com.nexora.xatu.porky.budget.dto.response.BudgetProfileResponse;
import com.nexora.xatu.porky.budget.dto.response.CategoryAllocationResponse;
import com.nexora.xatu.porky.shared.enums.BudgetCategory;
import com.nexora.xatu.porky.shared.enums.GoalPurpose;
import com.nexora.xatu.porky.shared.util.GoalPurposeDefaults;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "budget_profiles")
public class BudgetProfile {

  @Id private String id;

  private String userId;
  private BigDecimal monthlyIncome;
  private List<CategoryAllocation> allocations = new ArrayList<>();
  private Instant updatedAt;

  public BudgetProfileResponse toDto() {
    return new BudgetProfileResponse(
        this.userId,
        this.monthlyIncome,
        this.allocations.stream().map(CategoryAllocation::toDto).toList(),
        this.updatedAt);
  }

  public void update(UpdateBudgetProfileRequest request) {
    this.monthlyIncome = request.monthlyIncome();
    this.allocations =
        request.allocations().stream()
            .map(
                allocation ->
                    new CategoryAllocation(
                        allocation.category(),
                        allocation.percentage(),
                        allocation.label(),
                        GoalPurposeDefaults.resolve(allocation.category(), allocation.purpose())))
            .toList();
    this.updatedAt = Instant.now();
  }

  @Getter
  @Setter
  public static class CategoryAllocation {

    private BudgetCategory category;
    private BigDecimal percentage;
    private String label;
    private GoalPurpose purpose;

    public CategoryAllocation() {}

    public CategoryAllocation(BudgetCategory category, BigDecimal percentage) {
      this(category, percentage, null, null);
    }

    public CategoryAllocation(BudgetCategory category, BigDecimal percentage, String label) {
      this(category, percentage, label, null);
    }

    public CategoryAllocation(
        BudgetCategory category, BigDecimal percentage, String label, GoalPurpose purpose) {
      this.category = category;
      this.percentage = percentage;
      this.label = label;
      this.purpose = GoalPurposeDefaults.resolve(category, purpose);
    }

    public CategoryAllocationResponse toDto() {
      return new CategoryAllocationResponse(
          this.category,
          this.percentage,
          this.label,
          GoalPurposeDefaults.resolve(this.category, this.purpose));
    }
  }
}
