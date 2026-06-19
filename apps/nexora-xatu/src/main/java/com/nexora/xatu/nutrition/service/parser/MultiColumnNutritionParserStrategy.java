package com.nexora.xatu.nutrition.service.parser;

import com.nexora.xatu.nutrition.dto.NutritionFactResponse;
import com.nexora.xatu.nutrition.dto.NutritionOcrResponse;
import com.nexora.xatu.nutrition.enums.NutritionTableFormat;
import com.nexora.xatu.nutrition.model.NutritionValue;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class MultiColumnNutritionParserStrategy extends AbstractNutritionParserStrategy {

  @Override
  public NutritionOcrResponse parse(String rawText) {
    List<String> lines = normalizeLines(rawText);
    List<NutritionFactResponse> facts = new ArrayList<>();

    int index = 0;

    while (index < lines.size()) {
      String nutrient = resolveNutrientName(lines.get(index));

      if (nutrient == null) {
        index++;
        continue;
      }

      List<String> values = findNextValues(lines, index + 1);

      if (values.isEmpty()) {
        index++;
        continue;
      }

      String amount = pickPortionAmount(values, nutrient);
      NutritionValue nutritionValue = parseNutritionValue(amount, nutrient);

      if (nutritionValue != null) {
        facts.add(
            new NutritionFactResponse(
                nutrient, nutritionValue.value(), nutritionValue.unit()));
      }

      index += values.size() + 1;
    }

    return new NutritionOcrResponse(extractServingSize(rawText), facts, rawText);
  }

  @Override
  public boolean supports(NutritionTableFormat format) {
    return format == NutritionTableFormat.MULTI_COLUMN;
  }

  private List<String> findNextValues(List<String> lines, int startIndex) {
    List<String> values = new ArrayList<>();

    for (int index = startIndex; index < Math.min(lines.size(), startIndex + 5); index++) {
      String line = lines.get(index);

      if (resolveNutrientName(line) != null) {
        break;
      }

      if (looksLikeAmount(line)) {
        values.add(line);
      }
    }

    return values;
  }

  private String pickPortionAmount(List<String> values, String nutrient) {
    return normalizeAmount(values.getFirst(), nutrient);
  }

  @Override
  protected String extractServingSize(String rawText) {
    String normalizedText =
        removeAccents(rawText).toLowerCase().replaceAll("[*'\"”]", "").replaceAll("\\s+", " ");

    Matcher matcher =
        Pattern.compile("(\\d+[,.]?\\d*)\\s*g\\s+(\\d+[,.]?\\d*)\\s*g\\s*%\\s*vd")
            .matcher(normalizedText);

    if (matcher.find()) {
      return matcher.group(1).replace(".", ",") + "g";
    }

    return super.extractServingSize(rawText);
  }
}
