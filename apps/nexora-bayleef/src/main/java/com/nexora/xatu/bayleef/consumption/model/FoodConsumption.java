package com.nexora.xatu.bayleef.consumption.model;

import com.nexora.xatu.bayleef.consumption.dto.request.CreateConsumptionRequest;
import com.nexora.xatu.bayleef.consumption.dto.request.UpdateConsumptionRequest;
import com.nexora.xatu.bayleef.consumption.dto.response.ConsumptionResponse;
import com.nexora.xatu.bayleef.food.model.Food;
import com.nexora.xatu.bayleef.shared.model.NutritionValues;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "food_consumptions")
@CompoundIndex(name = "user_consumed_on_idx", def = "{'userId': 1, 'consumedOn': -1}")
public class FoodConsumption {

  @Id private String id;

  private String userId;
  private String foodId;
  private String foodName;
  private BigDecimal quantityGrams;
  private LocalDate consumedOn;
  private Instant consumedAt;

  private Instant createdAt;
  private Instant updatedAt;

  public static FoodConsumption from(String userId, Food food, CreateConsumptionRequest request) {
    Instant consumedAt = request.consumedAt() == null ? Instant.now() : request.consumedAt();
    Instant now = Instant.now();

    FoodConsumption consumption = new FoodConsumption();

    consumption.setUserId(userId);
    consumption.setFoodId(food.getId());
    consumption.setFoodName(food.getName());
    consumption.setQuantityGrams(request.quantityGrams());
    consumption.setConsumedAt(consumedAt);
    consumption.setConsumedOn(LocalDate.ofInstant(consumedAt, ZoneOffset.UTC));
    consumption.setCreatedAt(now);
    consumption.setUpdatedAt(now);

    return consumption;
  }

  public void update(UpdateConsumptionRequest request) {
    Instant consumedAt = request.consumedAt() == null ? this.consumedAt : request.consumedAt();

    this.quantityGrams = request.quantityGrams();
    this.consumedAt = consumedAt;
    this.consumedOn = LocalDate.ofInstant(consumedAt, ZoneOffset.UTC);
    this.updatedAt = Instant.now();
  }

  public ConsumptionResponse toDto(Food food) {
    NutritionValues nutritionPer100g =
        food.getNutritionPer100g() == null
            ? NutritionValues.empty()
            : food.getNutritionPer100g().copy();
    NutritionValues nutritionConsumed = nutritionPer100g.scale(this.quantityGrams);

    return new ConsumptionResponse(
        this.id,
        this.foodId,
        this.foodName,
        this.quantityGrams,
        this.consumedOn,
        this.consumedAt,
        nutritionPer100g,
        nutritionConsumed);
  }
}
