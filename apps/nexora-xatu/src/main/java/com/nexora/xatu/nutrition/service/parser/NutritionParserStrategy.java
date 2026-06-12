package com.nexora.xatu.nutrition.service.parser;

import com.nexora.xatu.nutrition.dto.NutritionOcrResponse;
import com.nexora.xatu.nutrition.enums.NutritionTableFormat;

public interface NutritionParserStrategy {

  NutritionOcrResponse parse(String rawText);

  boolean supports(NutritionTableFormat format);
}
