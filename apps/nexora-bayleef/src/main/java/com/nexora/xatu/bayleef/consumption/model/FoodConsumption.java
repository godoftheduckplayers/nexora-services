package com.nexora.xatu.bayleef.consumption.model;

import com.nexora.xatu.bayleef.consumption.dto.request.CreateConsumptionRequest;
import com.nexora.xatu.bayleef.consumption.dto.request.UpdateConsumptionRequest;
import com.nexora.xatu.bayleef.consumption.dto.response.ConsumptionResponse;
import com.nexora.xatu.bayleef.food.model.Food;
import com.nexora.xatu.bayleef.shared.model.NutritionValues;
import com.nexora.xatu.bayleef.shared.model.ServingUnit;
import com.nexora.xatu.bayleef.shared.support.NutritionFactsSupport;
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
  private ServingUnit quantityUnit;
  private LocalDate consumedOn;
  private Instant consumedAt;

  private Instant createdAt;
  private Instant updatedAt;

  public static FoodConsumption from(String userId, Food food, CreateConsumptionRequest request) {
    Instant consumedAt = request.consumedAt() == null ? Instant.now() : request.consumedAt();
    Instant now = Instant.now();
    ServingUnit foodUnit =
        NutritionFactsSupport.resolveServingUnit(
            food.getReferenceServingUnit(), food.getServingSize());

    NutritionFactsSupport.validateMatchingUnits(foodUnit, request.quantityUnit());

    FoodConsumption consumption = new FoodConsumption();

    consumption.setUserId(userId);
    consumption.setFoodId(food.getId());
    consumption.setFoodName(food.getName());
    consumption.setQuantityGrams(request.quantityGrams());
    consumption.setQuantityUnit(request.quantityUnit());
    consumption.setConsumedAt(consumedAt);
    consumption.setConsumedOn(LocalDate.ofInstant(consumedAt, ZoneOffset.UTC));
    consumption.setCreatedAt(now);
    consumption.setUpdatedAt(now);

    return consumption;
  }

  public void update(UpdateConsumptionRequest request, Food food) {
    Instant consumedAt = request.consumedAt() == null ? this.consumedAt : request.consumedAt();
    ServingUnit foodUnit =
        NutritionFactsSupport.resolveServingUnit(
            food.getReferenceServingUnit(), food.getServingSize());

    NutritionFactsSupport.validateMatchingUnits(foodUnit, request.quantityUnit());

    this.quantityGrams = request.quantityGrams();
    this.quantityUnit = request.quantityUnit();
    this.consumedAt = consumedAt;
    this.consumedOn = LocalDate.ofInstant(consumedAt, ZoneOffset.UTC);
    this.updatedAt = Instant.now();
  }

  public void updateWithoutFood(UpdateConsumptionRequest request) {
    Instant consumedAt = request.consumedAt() == null ? this.consumedAt : request.consumedAt();

    this.quantityGrams = request.quantityGrams();
    this.quantityUnit = request.quantityUnit();
    this.consumedAt = consumedAt;
    this.consumedOn = LocalDate.ofInstant(consumedAt, ZoneOffset.UTC);
    this.updatedAt = Instant.now();
  }

  public ConsumptionResponse toDto(Food food) {
    BigDecimal referenceServingAmount =
        NutritionFactsSupport.resolveReferenceServingAmount(
            food.getReferenceServingGrams(), food.getServingSize());
    NutritionValues nutritionPer100g =
        NutritionFactsSupport.resolveNutritionPer100g(
            food.getNutritionFacts(), referenceServingAmount, food.getNutritionPer100g());
    NutritionValues nutritionConsumed =
        NutritionFactsSupport.computeNutritionConsumed(
            food.getNutritionFacts(),
            referenceServingAmount,
            food.getNutritionPer100g(),
            this.quantityGrams);
    ServingUnit unit = resolveQuantityUnit();

    return new ConsumptionResponse(
        this.id,
        this.foodId,
        this.foodName,
        this.quantityGrams,
        unit,
        this.consumedOn,
        this.consumedAt,
        nutritionPer100g,
        nutritionConsumed);
  }

  public ConsumptionResponse toDtoMissingFood() {
    return new ConsumptionResponse(
        this.id,
        this.foodId,
        this.foodName,
        this.quantityGrams,
        resolveQuantityUnit(),
        this.consumedOn,
        this.consumedAt,
        NutritionValues.empty(),
        NutritionValues.empty());
  }

  private ServingUnit resolveQuantityUnit() {
    if (this.quantityUnit != null) {
      return this.quantityUnit;
    }

    return ServingUnit.G;
  }
}
