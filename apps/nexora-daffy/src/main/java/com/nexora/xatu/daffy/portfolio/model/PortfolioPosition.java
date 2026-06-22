package com.nexora.xatu.daffy.portfolio.model;

import com.nexora.xatu.daffy.portfolio.dto.request.CreatePortfolioPositionRequest;
import com.nexora.xatu.daffy.portfolio.dto.request.UpdatePortfolioPositionRequest;
import com.nexora.xatu.daffy.portfolio.dto.request.UpdatePortfolioValueRequest;
import com.nexora.xatu.daffy.portfolio.dto.response.PortfolioPositionResponse;
import com.nexora.xatu.daffy.shared.enums.PortfolioType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "portfolio_positions")
@CompoundIndex(name = "user_type_idx", def = "{'userId': 1, 'type': 1}")
public class PortfolioPosition {

  @Id private String id;

  private String userId;
  private String name;
  private PortfolioType type;
  private BigDecimal investedAmount;
  private BigDecimal currentValue;
  private Instant valueUpdatedAt;
  private Instant createdAt;
  private Instant updatedAt;

  public static PortfolioPosition from(String userId, CreatePortfolioPositionRequest request) {
    Instant now = Instant.now();
    PortfolioPosition position = new PortfolioPosition();

    position.setUserId(userId);
    position.setName(request.name());
    position.setType(request.type());
    position.setInvestedAmount(request.investedAmount());
    position.setCurrentValue(
        request.currentValue() == null ? request.investedAmount() : request.currentValue());
    position.setValueUpdatedAt(now);
    position.setCreatedAt(now);
    position.setUpdatedAt(now);

    return position;
  }

  public void update(UpdatePortfolioPositionRequest request) {
    this.name = request.name();
    this.type = request.type();
    this.investedAmount = request.investedAmount();
    this.currentValue =
        request.currentValue() == null ? this.currentValue : request.currentValue();
    this.updatedAt = Instant.now();
  }

  public void updateValue(UpdatePortfolioValueRequest request) {
    this.currentValue = request.currentValue();
    this.valueUpdatedAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public PortfolioPositionResponse toDto() {
    BigDecimal invested = investedAmount == null ? BigDecimal.ZERO : investedAmount;
    BigDecimal current = currentValue == null ? BigDecimal.ZERO : currentValue;
    BigDecimal pnl = current.subtract(invested);
    BigDecimal pnlPercent = BigDecimal.ZERO;

    if (invested.compareTo(BigDecimal.ZERO) > 0) {
      pnlPercent =
          pnl.multiply(BigDecimal.valueOf(100)).divide(invested, 2, RoundingMode.HALF_UP);
    }

    return new PortfolioPositionResponse(
        this.id,
        this.name,
        this.type,
        invested,
        current,
        pnl,
        pnlPercent,
        this.valueUpdatedAt);
  }
}
