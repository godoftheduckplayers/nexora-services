package com.nexora.xatu.extraction.service.parser;

import com.nexora.xatu.extraction.dto.NutritionOcrResponse;
import com.nexora.xatu.extraction.enums.NutritionTableFormat;

public interface NutritionParserStrategy {

  NutritionOcrResponse parse(String rawText);

  boolean supports(NutritionTableFormat format);
}
