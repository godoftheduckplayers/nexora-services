package com.nexora.xatu.bayleef.shared.support;

import com.nexora.xatu.bayleef.shared.dto.NutritionFactRequest;
import com.nexora.xatu.bayleef.shared.model.NutritionFact;
import com.nexora.xatu.bayleef.shared.model.NutritionValues;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NutritionFactsSupport {

  private static final Pattern VALUE_WITH_UNIT_PATTERN =
      Pattern.compile(
          "^(\\d+[,.]?\\d*)\\s*(kcal|kj|mg|g|gr|gramas?)\\b",
          Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

  private NutritionFactsSupport() {}

  public static List<NutritionFact> fromRequests(List<NutritionFactRequest> requests) {
    if (requests == null || requests.isEmpty()) {
      return List.of();
    }

    LinkedHashMap<String, NutritionFact> deduped = new LinkedHashMap<>();

    for (NutritionFactRequest request : requests) {
      NutritionFact fact = normalize(request);

      if (fact.getNutrient() == null || fact.getValue() == null) {
        continue;
      }

      deduped.put(normalizeNutrientKey(fact.getNutrient()), fact);
    }

    return List.copyOf(deduped.values());
  }

  public static NutritionFact normalize(NutritionFactRequest request) {
    NutritionFact fact = new NutritionFact();

    String canonicalNutrient = resolveCanonicalNutrientName(request.nutrient());
    ParsedAmount parsedAmount = parseAmount(request.value(), request.unit(), canonicalNutrient);

    fact.setNutrient(canonicalNutrient);
    fact.setValue(parsedAmount == null ? null : parsedAmount.value());
    fact.setUnit(parsedAmount == null ? null : parsedAmount.unit());
    fact.setDailyValuePercentage(normalizeDailyValuePercentage(request.dailyValuePercentage()));

    return fact;
  }

  public static NutritionValues deriveNutritionPer100g(
      List<NutritionFact> facts, BigDecimal referenceServingGrams) {
    NutritionValues values = NutritionValues.empty();

    if (facts == null || facts.isEmpty()) {
      return values;
    }

    for (NutritionFact fact : facts) {
      BigDecimal amountPer100g = toAmountPer100g(fact.getValue(), referenceServingGrams);

      if (amountPer100g == null) {
        continue;
      }

      applyKnownNutrient(values, normalizeNutrientKey(fact.getNutrient()), amountPer100g);
    }

    return values;
  }

  public static List<NutritionFact> fromNutritionValues(NutritionValues values) {
    if (values == null) {
      return List.of();
    }

    List<NutritionFact> facts = new ArrayList<>();

    addFact(facts, "Valor energético", values.getKcal(), "kcal");
    addFact(facts, "Proteínas", values.getProteins(), "g");
    addFact(facts, "Carboidratos", values.getCarbs(), "g");
    addFact(facts, "Gorduras Saturadas", values.getSaturatedFat(), "g");
    addFact(facts, "Fibra alimentar", values.getFiber(), "g");
    addFact(facts, "Gorduras totais", values.getFat(), "g");

    return facts;
  }

  public static String normalizeServingSizeLabel(String servingSize) {
    if (servingSize == null || servingSize.isBlank()) {
      return null;
    }

    String trimmed = servingSize.trim().replaceAll("\\s+", " ");
    String normalized = removeAccents(trimmed).toLowerCase(Locale.ROOT);

    Matcher matcher =
        Pattern.compile("(\\d+[,.]?\\d*)\\s*(g|gr|gramas?|ml)\\b", Pattern.CASE_INSENSITIVE)
            .matcher(normalized);

    if (matcher.find()) {
      String amount = matcher.group(1).replace('.', ',');
      String unit = matcher.group(2).toLowerCase(Locale.ROOT);

      if (unit.startsWith("gram")) {
        unit = "g";
      }

      return amount + unit;
    }

    Matcher portionMatcher =
        Pattern.compile("(?:por[cç][aã]o|porcao)\\s*(?:de\\s*)?(\\d+[,.]?\\d*)")
            .matcher(normalized);

    if (portionMatcher.find()) {
      return portionMatcher.group(1).replace('.', ',') + "g";
    }

    if (normalized.matches("\\d+[,.]?\\d*")) {
      return trimmed.replace('.', ',') + "g";
    }

    return trimmed;
  }

  public static BigDecimal parseReferenceServingGrams(String servingSize) {
    String normalized = servingSize == null ? "" : removeAccents(servingSize).toLowerCase(Locale.ROOT);

    Matcher matcher =
        Pattern.compile("(\\d+[,.]?\\d*)\\s*(?:g|gr|gramas?)\\b").matcher(normalized);

    if (matcher.find()) {
      return parseDecimal(matcher.group(1));
    }

    Matcher portionMatcher =
        Pattern.compile("(?:por[cç][aã]o|porcao)\\s*(?:de\\s*)?(\\d+[,.]?\\d*)")
            .matcher(normalized);

    if (portionMatcher.find()) {
      return parseDecimal(portionMatcher.group(1));
    }

    if (normalized.matches("\\d+[,.]?\\d*")) {
      return parseDecimal(normalized);
    }

    return null;
  }

  private static void addFact(
      List<NutritionFact> facts, String nutrient, BigDecimal amount, String unit) {
    if (amount == null) {
      return;
    }

    NutritionFact fact = new NutritionFact();

    fact.setNutrient(nutrient);
    fact.setValue(formatDecimal(amount));
    fact.setUnit(unit);
    fact.setDailyValuePercentage(null);

    facts.add(fact);
  }

  private static ParsedAmount parseAmount(String rawValue, String rawUnit, String canonicalNutrient) {
    String value = trimToNull(rawValue);
    String unit = trimToNull(rawUnit);

    if (value != null) {
      Matcher matcher = VALUE_WITH_UNIT_PATTERN.matcher(value.trim());

      if (matcher.find()) {
        value = matcher.group(1);
        unit = unit == null ? normalizeUnit(matcher.group(2)) : normalizeUnit(unit);
      }
    }

    if (unit == null) {
      unit = inferUnit(canonicalNutrient);
    } else {
      unit = normalizeUnit(unit);
    }

    BigDecimal amount = parseDecimal(value);

    if (amount == null) {
      return null;
    }

    return new ParsedAmount(formatDecimal(amount), unit);
  }

  private static String inferUnit(String canonicalNutrient) {
    if (canonicalNutrient == null) {
      return "g";
    }

    String key = normalizeNutrientKey(canonicalNutrient);

    if (key.contains("valor energetico") || key.contains("energia")) {
      return "kcal";
    }

    if (key.contains("sodio")
        || key.contains("calcio")
        || key.contains("ferro")
        || key.contains("colesterol")
        || key.contains("cafeina")) {
      return "mg";
    }

    return "g";
  }

  private static String normalizeUnit(String unit) {
    if (unit == null || unit.isBlank()) {
      return "g";
    }

    String normalized = removeAccents(unit).toLowerCase(Locale.ROOT).trim();

    if (normalized.equals("kcal") || normalized.equals("kj")) {
      return "kcal";
    }

    if (normalized.equals("mg")) {
      return "mg";
    }

    if (normalized.equals("g") || normalized.equals("gr") || normalized.startsWith("gram")) {
      return "g";
    }

    return unit.trim();
  }

  private static String normalizeDailyValuePercentage(String rawValue) {
    if (rawValue == null || rawValue.isBlank()) {
      return null;
    }

    String normalized = rawValue.trim();

    if (normalized.equals("**")) {
      return "**";
    }

    String digits = normalized.replace("%", "").trim();

    if (!digits.matches("\\d{1,3}")) {
      return normalized;
    }

    return digits + "%";
  }

  private static String resolveCanonicalNutrientName(String rawNutrient) {
    if (rawNutrient == null || rawNutrient.isBlank()) {
      return null;
    }

    String normalized = normalizeNutrientKey(rawNutrient);

    if (normalized.contains("valor energetico")
        || normalized.contains("energia")
        || normalized.equals("kcal")
        || normalized.contains("calorias")) {
      return "Valor energético";
    }

    if (normalized.contains("carboidrato") || normalized.contains("carbeiarato")) {
      return "Carboidratos";
    }

    if (normalized.contains("acucar") && normalized.contains("adicionad")) {
      return "Açúcares adicionados";
    }

    if (normalized.contains("acucar") || normalized.contains("agucar")) {
      return "Açúcares totais";
    }

    if (normalized.contains("galactose")) {
      return "Galactose";
    }

    if (normalized.contains("lactose") || normalized.contains("laetose")) {
      return "Lactose";
    }

    if (normalized.contains("proteina") || normalized.contains("rotena")) {
      return "Proteínas";
    }

    if (normalized.contains("gordura") && normalized.contains("trans")) {
      return "Gorduras Trans";
    }

    if (normalized.contains("gordura") && normalized.contains("saturad")) {
      return "Gorduras Saturadas";
    }

    if (normalized.contains("gordura") || normalized.contains("lipidio")) {
      return "Gorduras totais";
    }

    if (normalized.contains("colesterol")) {
      return "Colesterol";
    }

    if (normalized.contains("fibra")) {
      return "Fibra alimentar";
    }

    if (normalized.contains("sodio") || normalized.contains("sedio") || normalized.equals("eso")) {
      return "Sódio";
    }

    if (normalized.contains("calcio")) {
      return "Cálcio";
    }

    if (normalized.contains("ferro")) {
      return "Ferro";
    }

    if (normalized.contains("cafeina")) {
      return "Cafeína";
    }

    return formatNutrientLabel(rawNutrient.trim());
  }

  private static String formatNutrientLabel(String line) {
    String cleaned = line.trim().replaceAll("\\s+", " ");
    String[] words = cleaned.split(" ");
    StringBuilder formatted = new StringBuilder();

    for (int index = 0; index < words.length; index++) {
      String word = words[index];

      if (word.isBlank()) {
        continue;
      }

      if (index > 0) {
        formatted.append(' ');
      }

      formatted
          .append(Character.toUpperCase(word.charAt(0)))
          .append(word.substring(1).toLowerCase(Locale.ROOT));
    }

    return formatted.toString();
  }

  private static BigDecimal toAmountPer100g(String rawValue, BigDecimal referenceServingGrams) {
    BigDecimal amount = parseDecimal(rawValue);

    if (amount == null) {
      return null;
    }

    if (referenceServingGrams == null
        || referenceServingGrams.compareTo(BigDecimal.ZERO) <= 0
        || referenceServingGrams.compareTo(BigDecimal.valueOf(100)) == 0) {
      return amount.setScale(2, RoundingMode.HALF_UP);
    }

    return amount
        .multiply(BigDecimal.valueOf(100))
        .divide(referenceServingGrams, 2, RoundingMode.HALF_UP);
  }

  private static void applyKnownNutrient(
      NutritionValues values, String nutrient, BigDecimal amountPer100g) {
    if (nutrient == null || nutrient.isBlank()) {
      return;
    }

    if (nutrient.contains("valor energetico") || nutrient.contains("energia")) {
      values.setKcal(amountPer100g);
      return;
    }

    if (nutrient.contains("proteina")) {
      values.setProteins(amountPer100g);
      return;
    }

    if (nutrient.contains("carboidrato")) {
      values.setCarbs(amountPer100g);
      return;
    }

    if (nutrient.contains("gordura") && nutrient.contains("trans")) {
      return;
    }

    if (nutrient.contains("gordura") && nutrient.contains("saturad")) {
      values.setSaturatedFat(amountPer100g);
      return;
    }

    if (nutrient.contains("gordura") || nutrient.contains("lipidio")) {
      values.setFat(amountPer100g);
      return;
    }

    if (nutrient.contains("fibra")) {
      values.setFiber(amountPer100g);
    }
  }

  private static BigDecimal parseDecimal(String value) {
    if (value == null) {
      return null;
    }

    String normalized = value.trim().replace(',', '.');

    if (normalized.isEmpty()) {
      return null;
    }

    try {
      return new BigDecimal(normalized);
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  private static String formatDecimal(BigDecimal value) {
    return value.stripTrailingZeros().toPlainString().replace('.', ',');
  }

  private static String normalizeNutrientKey(String value) {
    if (value == null) {
      return "";
    }

    return removeAccents(value).toLowerCase(Locale.ROOT).trim();
  }

  private static String removeAccents(String value) {
    return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
  }

  private static String trimToNull(String value) {
    if (value == null) {
      return null;
    }

    String trimmed = value.trim();

    return trimmed.isEmpty() ? null : trimmed;
  }

  private record ParsedAmount(String value, String unit) {}
}
