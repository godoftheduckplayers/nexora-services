package com.nexora.xatu.nutrition.service.parser;

import com.nexora.xatu.nutrition.dto.NutritionFactResponse;
import com.nexora.xatu.nutrition.dto.NutritionOcrResponse;
import com.nexora.xatu.nutrition.enums.NutritionTableFormat;
import com.nexora.xatu.nutrition.model.NutritionValue;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SingleAmountNutritionParserStrategy extends AbstractNutritionParserStrategy {

  @Override
  public NutritionOcrResponse parse(String rawText) {
    List<String> lines = normalizeLines(rawText);
    List<NutritionFactResponse> facts = new ArrayList<>();

    int index = 0;

    while (index < lines.size()) {
      String nutrient = normalizeNutrient(lines.get(index));

      if (nutrient == null) {
        index++;
        continue;
      }

      int amountIndex = findNextAmountIndex(lines, index + 1);

      if (amountIndex == -1) {
        index++;
        continue;
      }

      NutritionValue nutritionValue = parseNutritionValue(lines.get(amountIndex), nutrient);
      String dailyValue = findDailyValue(lines, amountIndex + 1);

      facts.add(
          new NutritionFactResponse(
              nutrient, nutritionValue.value(), nutritionValue.unit(), dailyValue));
      index = amountIndex + 1;
    }

    return new NutritionOcrResponse(extractServingSize(rawText), facts, rawText);
  }

  @Override
  public boolean supports(NutritionTableFormat format) {
    return format == NutritionTableFormat.SINGLE_AMOUNT;
  }

  private int findNextAmountIndex(List<String> lines, int startIndex) {
    for (int index = startIndex; index < Math.min(lines.size(), startIndex + 4); index++) {
      String line = lines.get(index);

      if (normalizeNutrient(line) != null) {
        return -1;
      }

      if (looksLikeAmount(line)) {
        return index;
      }
    }

    return -1;
  }

  private String findDailyValue(List<String> lines, int startIndex) {
    for (int index = startIndex; index < Math.min(lines.size(), startIndex + 2); index++) {
      String line = lines.get(index);

      if (normalizeNutrient(line) != null) {
        return null;
      }

      if (looksLikeDailyValue(line)) {
        return normalizeDailyValue(line);
      }
    }

    return null;
  }
}
