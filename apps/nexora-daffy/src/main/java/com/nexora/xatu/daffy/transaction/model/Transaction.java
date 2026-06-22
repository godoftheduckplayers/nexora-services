package com.nexora.xatu.daffy.transaction.model;

import com.nexora.xatu.daffy.fixedexpense.model.FixedExpense;
import com.nexora.xatu.daffy.shared.enums.BudgetCategory;
import com.nexora.xatu.daffy.shared.enums.TransactionType;
import com.nexora.xatu.daffy.transaction.dto.request.CreateTransactionRequest;
import com.nexora.xatu.daffy.transaction.dto.request.UpdateTransactionRequest;
import com.nexora.xatu.daffy.transaction.dto.response.TransactionResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "transactions")
@CompoundIndex(name = "user_occurred_on_idx", def = "{'userId': 1, 'occurredOn': -1}")
public class Transaction {

  @Id private String id;

  private String userId;
  private TransactionType type;
  private BudgetCategory category;
  private BigDecimal amount;
  private String description;
  private String fixedExpenseId;
  private LocalDate occurredOn;
  private Instant occurredAt;
  private Instant createdAt;
  private Instant updatedAt;

  public static Transaction fromFixedExpense(
      String userId, FixedExpense expense, LocalDate occurredOn) {
    Instant now = Instant.now();
    Transaction transaction = new Transaction();

    transaction.setUserId(userId);
    transaction.setType(TransactionType.EXPENSE);
    transaction.setCategory(expense.getCategory());
    transaction.setAmount(expense.getAmount());
    transaction.setDescription(expense.getName());
    transaction.setFixedExpenseId(expense.getId());
    transaction.setOccurredOn(occurredOn);
    transaction.setOccurredAt(now);
    transaction.setCreatedAt(now);
    transaction.setUpdatedAt(now);

    return transaction;
  }

  public static Transaction from(String userId, CreateTransactionRequest request) {
    Instant occurredAt = request.occurredAt() == null ? Instant.now() : request.occurredAt();
    Instant now = Instant.now();
    Transaction transaction = new Transaction();

    transaction.setUserId(userId);
    transaction.setType(request.type());
    transaction.setCategory(request.category());
    transaction.setAmount(request.amount());
    transaction.setDescription(request.description());
    transaction.setOccurredAt(occurredAt);
    transaction.setOccurredOn(request.occurredOn() == null ? LocalDate.now() : request.occurredOn());
    transaction.setCreatedAt(now);
    transaction.setUpdatedAt(now);

    return transaction;
  }

  public void update(UpdateTransactionRequest request) {
    Instant occurredAt = request.occurredAt() == null ? this.occurredAt : request.occurredAt();

    this.type = request.type();
    this.category = request.category();
    this.amount = request.amount();
    this.description = request.description();
    this.occurredAt = occurredAt;
    this.occurredOn = request.occurredOn() == null ? this.occurredOn : request.occurredOn();
    this.updatedAt = Instant.now();
  }

  public TransactionResponse toDto() {
    return new TransactionResponse(
        this.id,
        this.type,
        this.category,
        this.amount,
        this.description,
        this.occurredOn,
        this.occurredAt);
  }
}
