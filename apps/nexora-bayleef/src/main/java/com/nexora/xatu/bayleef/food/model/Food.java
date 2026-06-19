package com.nexora.xatu.bayleef.food.model;

import com.nexora.xatu.bayleef.food.dto.request.CreateFoodRequest;
import com.nexora.xatu.bayleef.food.dto.request.UpdateFoodRequest;
import com.nexora.xatu.bayleef.food.dto.response.FoodResponse;
import com.nexora.xatu.bayleef.shared.dto.NutritionFactRequest;
import com.nexora.xatu.bayleef.shared.model.NutritionFact;
import com.nexora.xatu.bayleef.shared.model.NutritionValues;
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
  private List<NutritionFact> nutritionFacts;
  private NutritionValues nutritionPer100g;

  private Instant createdAt;
  private Instant updatedAt;

  public static Food from(String userId, CreateFoodRequest request) {
    Instant now = Instant.now();
    Food food = new Food();

    food.setUserId(userId);
    food.applyPayload(request.name(), request.servingSize(), request.referenceServingGrams(), request.nutritionFacts());
    food.setCreatedAt(now);
    food.setUpdatedAt(now);

    return food;
  }

  public void update(UpdateFoodRequest request) {
    this.applyPayload(request.name(), request.servingSize(), request.referenceServingGrams(), request.nutritionFacts());
    this.updatedAt = Instant.now();
  }

  private void applyPayload(
      String name,
      String servingSize,
      BigDecimal referenceServingGrams,
      List<NutritionFactRequest> nutritionFacts) {
    this.name = name.trim();
    this.servingSize = NutritionFactsSupport.normalizeServingSizeLabel(servingSize);
    this.referenceServingGrams =
        referenceServingGrams != null
            ? referenceServingGrams
            : NutritionFactsSupport.parseReferenceServingGrams(servingSize);
    this.nutritionFacts = NutritionFactsSupport.fromRequests(nutritionFacts);
    this.nutritionPer100g =
        NutritionFactsSupport.deriveNutritionPer100g(this.nutritionFacts, this.referenceServingGrams);
  }

  public FoodResponse toDto() {
    List<NutritionFact> facts = this.nutritionFacts;

    if (facts == null || facts.isEmpty()) {
      facts = NutritionFactsSupport.fromNutritionValues(this.nutritionPer100g);
    }

    return new FoodResponse(
        this.id,
        this.name,
        resolveServingSizeLabel(),
        this.referenceServingGrams,
        facts.stream().map(NutritionFact::toDto).toList(),
        this.nutritionPer100g,
        this.createdAt,
        this.updatedAt);
  }

  private String resolveServingSizeLabel() {
    if (this.servingSize != null && !this.servingSize.isBlank()) {
      return this.servingSize.trim();
    }

    if (this.referenceServingGrams != null) {
      return this.referenceServingGrams.stripTrailingZeros().toPlainString().replace('.', ',') + "g";
    }

    return "100g";
  }
}
