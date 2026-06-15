package com.nexora.xatu.bayleef.food.model;

import com.nexora.xatu.bayleef.food.dto.request.CreateFoodRequest;
import com.nexora.xatu.bayleef.food.dto.request.UpdateFoodRequest;
import com.nexora.xatu.bayleef.food.dto.response.FoodResponse;
import com.nexora.xatu.bayleef.shared.dto.NutritionValuesRequest;
import com.nexora.xatu.bayleef.shared.model.NutritionValues;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "foods")
@CompoundIndex(name = "user_name_idx", def = "{'userId': 1, 'name': 1}")
public class Food {

  @Id private String id;

  private String userId;
  private String name;
  private NutritionValues nutritionPer100g;

  private Instant createdAt;
  private Instant updatedAt;

  public static Food from(String userId, CreateFoodRequest request) {
    Instant now = Instant.now();
    Food food = new Food();

    food.setUserId(userId);
    food.setName(request.name().trim());
    food.setNutritionPer100g(
        normalizeNutritionPer100g(request.nutritionPer100g(), request.referenceServingGrams()));
    food.setCreatedAt(now);
    food.setUpdatedAt(now);

    return food;
  }

  public void update(UpdateFoodRequest request) {
    this.name = request.name().trim();
    this.nutritionPer100g =
        normalizeNutritionPer100g(request.nutritionPer100g(), request.referenceServingGrams());
    this.updatedAt = Instant.now();
  }

  private static NutritionValues normalizeNutritionPer100g(
      NutritionValuesRequest nutrition, BigDecimal referenceServingGrams) {
    if (nutrition == null) {
      return NutritionValues.empty();
    }

    return nutrition.toModel().normalizeFromServingToPer100g(referenceServingGrams);
  }

  public FoodResponse toDto() {
    return new FoodResponse(
        this.id, this.name, this.nutritionPer100g, this.createdAt, this.updatedAt);
  }
}
