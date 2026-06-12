package com.nexora.xatu.nutrition.service.parser;

import com.nexora.xatu.nutrition.enums.NutritionTableFormat;
import java.text.Normalizer;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class NutritionTableLayoutDetector {

  public NutritionTableFormat detect(String rawText) {
    String text = normalize(rawText);

    boolean hasMultipleReferenceColumns =
        Pattern.compile("\\d+\\s*g.*\\d+\\s*g.*%\\s*vd", Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
            .matcher(text)
            .find();

    if (hasMultipleReferenceColumns) {
      return NutritionTableFormat.MULTI_COLUMN;
    }

    return NutritionTableFormat.SINGLE_AMOUNT;
  }

  private String normalize(String value) {
    return Normalizer.normalize(value, Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "")
        .toLowerCase()
        .replaceAll("\\s+", " ")
        .trim();
  }
}
