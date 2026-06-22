package com.nexora.xatu.daffy.fixedexpense.model;

import com.nexora.xatu.daffy.fixedexpense.dto.request.CreateFixedExpenseRequest;
import com.nexora.xatu.daffy.fixedexpense.dto.request.UpdateFixedExpenseRequest;
import com.nexora.xatu.daffy.fixedexpense.dto.response.FixedExpenseResponse;
import com.nexora.xatu.daffy.shared.enums.BudgetCategory;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "fixed_expenses")
@CompoundIndex(name = "user_active_idx", def = "{'userId': 1, 'active': 1}")
public class FixedExpense {

  @Id private String id;

  private String userId;
  private String name;
  private BigDecimal amount;
  private BudgetCategory category;
  private Integer dayOfMonth;
  private boolean active;
  private Instant createdAt;
  private Instant updatedAt;

  public static FixedExpense from(String userId, CreateFixedExpenseRequest request) {
    Instant now = Instant.now();
    FixedExpense expense = new FixedExpense();

    expense.setUserId(userId);
    expense.setName(request.name());
    expense.setAmount(request.amount());
    expense.setCategory(request.category());
    expense.setDayOfMonth(request.dayOfMonth());
    expense.setActive(request.active() == null || request.active());
    expense.setCreatedAt(now);
    expense.setUpdatedAt(now);

    return expense;
  }

  public void update(UpdateFixedExpenseRequest request) {
    this.name = request.name();
    this.amount = request.amount();
    this.category = request.category();
    this.dayOfMonth = request.dayOfMonth();
    this.active = request.active() == null || request.active();
    this.updatedAt = Instant.now();
  }

  public FixedExpenseResponse toDto() {
    return toDto(false);
  }

  public FixedExpenseResponse toDto(boolean paidThisMonth) {
    return new FixedExpenseResponse(
        this.id, this.name, this.amount, this.category, this.dayOfMonth, this.active, paidThisMonth);
  }
}
