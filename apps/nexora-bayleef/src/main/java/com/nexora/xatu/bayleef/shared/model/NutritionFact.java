package com.nexora.xatu.bayleef.shared.model;

import com.nexora.xatu.bayleef.shared.dto.NutritionFactRequest;
import com.nexora.xatu.bayleef.shared.dto.NutritionFactResponse;
import com.nexora.xatu.bayleef.shared.support.NutritionFactsSupport;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NutritionFact {

  private String nutrient;
  private String value;
  private String unit;
  private String dailyValuePercentage;

  public static NutritionFact fromRequest(NutritionFactRequest request) {
    return NutritionFactsSupport.normalize(request);
  }

  public NutritionFactResponse toDto() {
    return new NutritionFactResponse(nutrient, value, unit, dailyValuePercentage);
  }
}
