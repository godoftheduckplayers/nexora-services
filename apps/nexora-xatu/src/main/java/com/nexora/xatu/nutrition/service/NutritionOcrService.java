package com.nexora.xatu.nutrition.service;

import com.nexora.xatu.nutrition.dto.NutritionOcrResponse;
import com.nexora.xatu.nutrition.service.ocr.NutritionTableOcrService;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NutritionOcrService {

  private final ImagePreprocessorService imagePreprocessorService;
  private final NutritionTableOcrService nutritionTableOcrService;
  private final NutritionParserService nutritionParserService;

  public NutritionOcrResponse extractNutrition(File imageFile) {

    var image = imagePreprocessorService.preprocess(imageFile);

    String rawText = nutritionTableOcrService.read(image);

    return nutritionParserService.parse(rawText);
  }
}
