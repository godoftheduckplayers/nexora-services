package com.nexora.xatu.bayleef.food.model;

import com.nexora.xatu.bayleef.food.dto.request.CreateFoodRequest;
import com.nexora.xatu.bayleef.food.dto.request.UpdateFoodRequest;
import com.nexora.xatu.bayleef.food.dto.response.FoodResponse;
import com.nexora.xatu.bayleef.shared.dto.NutritionFactRequest;
import com.nexora.xatu.bayleef.shared.model.NutritionFact;
import com.nexora.xatu.bayleef.shared.model.NutritionValues;
import com.nexora.xatu.bayleef.shared.model.ServingUnit;
import com.nexora.xatu.bayleef.shared.support.NutritionFactsSupport;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
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
  private String servingSize;
  private BigDecimal referenceServingGrams;
  private ServingUnit referenceServingUnit;
  private List<NutritionFact> nutritionFacts;
  private NutritionValues nutritionPer100g;

  private Instant createdAt;
  private Instant updatedAt;

  public static Food from(String userId, CreateFoodRequest request) {
    Instant now = Instant.now();
    Food food = new Food();

    food.setUserId(userId);
    food.applyPayload(
        request.name(),
        request.servingSize(),
        request.referenceServingGrams(),
        request.referenceServingUnit(),
        request.nutritionFacts());
    food.setCreatedAt(now);
    food.setUpdatedAt(now);

    return food;
  }

  public void update(UpdateFoodRequest request) {
    this.applyPayload(
        request.name(),
        request.servingSize(),
        request.referenceServingGrams(),
        request.referenceServingUnit(),
        request.nutritionFacts());
    this.updatedAt = Instant.now();
  }

  private void applyPayload(
      String name,
      String servingSize,
      BigDecimal referenceServingGrams,
      ServingUnit referenceServingUnit,
      List<NutritionFactRequest> nutritionFacts) {
    this.name = name.trim();
    this.referenceServingUnit =
        NutritionFactsSupport.resolveServingUnit(referenceServingUnit, servingSize);
    this.referenceServingGrams =
        referenceServingGrams != null
            ? referenceServingGrams
            : NutritionFactsSupport.parseReferenceServingAmount(servingSize);
    this.servingSize =
        NutritionFactsSupport.formatServingSizeLabel(
            this.referenceServingGrams, this.referenceServingUnit);
    this.nutritionFacts = NutritionFactsSupport.fromRequests(nutritionFacts);
    this.nutritionPer100g = null;
  }

  public FoodResponse toDto() {
    List<NutritionFact> facts = this.nutritionFacts;
    BigDecimal referenceServingAmount =
        NutritionFactsSupport.resolveReferenceServingAmount(
            this.referenceServingGrams, this.servingSize);
    NutritionValues nutritionPer100g =
        NutritionFactsSupport.resolveNutritionPer100g(
            facts, referenceServingAmount, this.nutritionPer100g);
    ServingUnit servingUnit =
        NutritionFactsSupport.resolveServingUnit(this.referenceServingUnit, this.servingSize);

    if (facts == null || facts.isEmpty()) {
      facts = NutritionFactsSupport.fromNutritionValues(nutritionPer100g);
    }

    return new FoodResponse(
        this.id,
        this.name,
        resolveServingSizeLabel(servingUnit),
        this.referenceServingGrams,
        servingUnit,
        facts.stream().map(NutritionFact::toDto).toList(),
        nutritionPer100g,
        this.createdAt,
        this.updatedAt);
  }

  private String resolveServingSizeLabel(ServingUnit servingUnit) {
    if (this.servingSize != null && !this.servingSize.isBlank()) {
      return this.servingSize.trim();
    }

    if (this.referenceServingGrams != null) {
      return NutritionFactsSupport.formatServingSizeLabel(
          this.referenceServingGrams, servingUnit);
    }

    return "100g";
  }
}
